package net.kst_d.lab.yac.tcp;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.kst_d.common.Generator;
import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.lab.yac.ChatTransport;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.MessageSender;
import net.kst_d.lab.yac.ServerMessageSender;
import net.kst_d.lab.yac.SingleConnectionMessageSender;

public class TcpTransport implements ChatTransport, ServerMessageSender {
    private static final KstLogger LOG = KstLoggerFactory.logger(TcpTransport.class);

    protected Listener listener;
    protected Processor processor;

    protected final Queue<SocketChannel> queue = new ConcurrentLinkedQueue<>();
    protected final ConcurrentMap<UUID, SingleConnectionMessageSender> messageConsumers = new ConcurrentHashMap<>();

    protected volatile Consumer<Message> messageListener = null;
    protected final int port;

    public TcpTransport(int port) {
	this.port = port;
    }

    @Override
    public void start() {
	SID sid = Generator.sid();

	BiFunction<Connection, Selector, OutMessageTransformer> omtFactory = ((connection, selector) -> {
	    final OutMessageTransformer transformer = new OutMessageTransformer(connection, selector);
	    messageConsumers.put(connection.uuid, transformer);
	    return transformer;
	});

	Function<Connection, InMessageTransformer> imtFactory = connection -> new InMessageTransformer(connection, this::onMessage);

	listener = new Listener(port, "yac-tcp-listener", queue::add);
	processor = new Processor("yac-tcp-processor", ((channel, selector) -> new Connection(channel, imtFactory, omtFactory, selector)), queue::poll);

	listener.start(sid);
	processor.start(sid);
    }

    @Override
    public void stop() {
	listener.stop(SID.NONE);
	processor.stop(SID.NONE);
    }


    @Override
    public void setListener(Consumer<Message> listener) {
	messageListener = listener;
    }

    @Override
    public void removeListener() {
	messageListener = null;
    }

    @Override
    public void send(UUID to, Message message) {
	final MessageSender consumer = messageConsumers.get(to);
	if (consumer != null) {
	    consumer.send(to, message);
	}
    }

    @Override
    public void send(UUID to, List<Message> messages) {
	final MessageSender consumer = messageConsumers.get(to);
	if (consumer != null) {
	    consumer.send(to, messages);
	}

    }

    @Override
    public void broadcast(Message message) {
	messageConsumers.values().parallelStream().forEach(con -> con.send(con.connection(), message));
    }

    @Override
    public void broadcast(List<Message> messages) {
	messageConsumers.values().parallelStream().forEach(con -> con.send(con.connection(), messages));
    }

    @Override
    public ServerMessageSender sender() {
	return this;
    }

    private void onMessage(Message message) {
	if (messageListener != null) {
	    messageListener.accept(message);
	}
    }


}
