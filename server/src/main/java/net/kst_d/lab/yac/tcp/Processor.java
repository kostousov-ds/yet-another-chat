package net.kst_d.lab.yac.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;
import net.kst_d.lab.yac.Utils;

public class Processor {
    private static final KstLogger LOG = KstLoggerFactory.logger(Processor.class);

    private final Supplier<SocketChannel> supplier;
    private final String name;
    private final ConnectionFactory factory;

    private volatile boolean running = false;
    private Thread thread;

    private ByteBuffer inBuffer = ByteBuffer.allocate(4096);
    private ByteBuffer outBuffer = ByteBuffer.allocate(4096);

    private Selector readSelector;
    private Selector writeSelector;

    private Map<UUID, Connection> connections = new HashMap<>();

    public Processor(String name, ConnectionFactory factory, Supplier<SocketChannel> supplier) {
	this.supplier = supplier;
	this.name = name;

	this.factory = factory;
    }

    public void start(SID sid) {
	final MethodLogger logger = LOG.silentEnter(sid, "start");

	try {
	    readSelector = Selector.open();
	    writeSelector = Selector.open();
	    running = true;
	    thread = new Thread(() -> {
		logger.trace("processor thread started");

		try {
		    while (running) {

			boolean nothing = true;
			{
			    //новые подключения
			    SocketChannel channel;
			    while ((channel = supplier.get()) != null) {
				logger.trace("found new channel {}", channel);
				nothing = false;

				Connection connection = factory.newConnection(channel, writeSelector);
				channel.configureBlocking(false);

				//todo обработать исключения (вдруг канал уже закрыт и тд)
				final SelectionKey key = channel.register(readSelector, SelectionKey.OP_READ);
				key.attach(connection);

				connections.put(connection.uuid, connection);
			    }
			}

			nothing = nothing && TcpUtils.tryReadSomething(inBuffer, readSelector);

			nothing = nothing && TcpUtils.tryWriteSomething(outBuffer, writeSelector, logger);

			if (nothing) {
			    Thread.sleep(50);
			}
		    }
		} catch (InterruptedException | IOException e) {
		    logger.error("", e);
		    running = false;
		}
		logger.trace("done");
	    }, name);
	    thread.start();
	} catch (IOException e) {
	    logger.error("{}", e, name);
	}
    }

    public void stop(SID sid) {
	final MethodLogger logger = LOG.silentEnter(sid, "stop");

	running = false;
	try {
	    thread.join();
	} catch (InterruptedException e) {
	    logger.error("", e);
	}
	Utils.closer(readSelector, logger, Objects.toString(readSelector));
	Utils.closer(writeSelector, logger, Objects.toString(writeSelector));
	connections.values().forEach(c -> Utils.closer(c.channel, logger, Objects.toString(c.channel)));
    }

}
