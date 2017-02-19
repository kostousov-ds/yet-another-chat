package net.kst_d.lab.yac.tcp;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.SingleConnectionMessageSender;

public class TcpTransportBackDoor {
    private final TcpTransport transport;

    public TcpTransportBackDoor(TcpTransport transport) {
	this.transport = transport;
    }

    public Listener listener() {
	return transport.listener;
    }

    public Processor processor() {
        return transport.processor;
    }

    public Queue<SocketChannel> channels() {
        return transport.queue;
    }

    public ConcurrentMap<UUID, SingleConnectionMessageSender> consumers() {
        return transport.messageConsumers;
    }

    public Consumer<Message> messageListener() {
        return transport.messageListener;
    }

    public ServerSocketChannel serverChannel() {
        return transport.listener.channel;
    }
}
