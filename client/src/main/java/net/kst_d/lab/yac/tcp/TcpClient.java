package net.kst_d.lab.yac.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.function.Consumer;

import net.kst_d.common.Generator;
import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;
import net.kst_d.lab.yac.ClientTransport;
import net.kst_d.lab.yac.Constants;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.Utils;


public class TcpClient implements ClientTransport {
    private static final KstLogger LOG = KstLoggerFactory.logger(TcpClient.class);

    private final int port;
    private final String host;
    private volatile InMessageTransformer imt;
    private volatile OutMessageTransformer omt;
    private Selector rs;
    private Selector ws;
    private Connection connection;
    private volatile boolean running = false;
    private volatile Consumer<Message> listener;
    private Thread thread;

    public TcpClient(int port, String host) {
	this.port = port;
	this.host = host;
    }

    @Override
    public void start() {
	SID sid = Generator.sid();
	final MethodLogger logger = LOG.silentEnter(sid, "start");

	try {
	    final SocketChannel channel = SocketChannel.open(new InetSocketAddress(host, port));
	    channel.configureBlocking(false);
	    rs = Selector.open();
	    ws = Selector.open();
	    connection = new Connection(channel, c -> {
		imt = new InMessageTransformer(c, this::onMessage);
		return imt;
	    }, (c, s) -> {
		omt = new OutMessageTransformer(c, s);
		return omt;
	    }, ws);
	    final SelectionKey key = channel.register(rs, SelectionKey.OP_READ);
	    key.attach(connection);
	    running = true;

	    thread = new Thread(() -> {
		ByteBuffer inBuffer = ByteBuffer.allocate(Constants.MESSAGE_TRANSFORMER_BUFF_LEN);
		ByteBuffer outBuffer = ByteBuffer.allocate(Constants.MESSAGE_TRANSFORMER_BUFF_LEN);
		try {
		    while (running) {
			boolean nothing;

			nothing = TcpUtils.tryReadSomething(inBuffer, rs);

			nothing = nothing && TcpUtils.tryWriteSomething(outBuffer, ws, logger);

			if (nothing) {
			    Thread.sleep(100);
			}
		    }
		} catch (Exception e) {

		}

	    }, "yac-tcp-transport");
	    thread.start();


	} catch (IOException e) {
	    logger.error("", e);
	}

    }

    @Override
    public void setListener(Consumer<Message> consumer) {
	listener = consumer;
    }

    @Override
    public void removeListener() {
	listener = null;
    }

    @Override
    public void stop() {
	final MethodLogger logger = LOG.silentEnter(SID.NONE, "stop");
	running = false;
	try {
	    thread.join();
	} catch (InterruptedException e) {
	    logger.error("", e);
	}
	Utils.closer(rs, logger, Objects.toString(rs));
	Utils.closer(ws, logger, Objects.toString(ws));
	Utils.closer(connection.channel, logger, Objects.toString(connection.channel));
    }

    @Override
    public void send(String message) {
	omt.send(connection.uuid, new Message(connection.uuid, message));
    }

    private void onMessage(Message message) {
	if (listener != null) {
	    listener.accept(message);
	}
    }
}
