package net.kst_d.lab.yac.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;

/**
 * Сетевой слушатель.
 * Поднимает TCP сервер на заданном порту.
 * При получении соединения — передает его в <code>consumer</code>
 */
public class Listener {
    private KstLogger LOG = KstLoggerFactory.logger(Listener.class);

    protected volatile boolean running = false;
    protected Thread thread;

    protected final int port;
    protected final String name;
    protected final Consumer<SocketChannel> consumer;
    protected ServerSocketChannel channel;

    public Listener(int port, String name, Consumer<SocketChannel> consumer) {
	this.port = port;
	this.name = name;
	this.consumer = consumer;
    }

    public void start(SID sid) {
	final MethodLogger logger = LOG.silentEnter(sid, "start");
	try {
	    channel = ServerSocketChannel.open();
	    channel.bind(new InetSocketAddress("0.0.0.0", port));

	    running = true;
	    thread = new Thread(() -> {
		logger.trace("listener thread started on {}", channel.socket().getLocalSocketAddress());

		while (running) {
		    try {
			final SocketChannel sc = channel.accept();
			logger.debug("new connection {}", sc);
			consumer.accept(sc);
		    } catch (AsynchronousCloseException e){
		        logger.info("server channel was closed");
		        running = false;
		    } catch (IOException e) {
			logger.error("", e);
			running = false;
		    }
		}
		logger.trace("done");
	    }, name);
	    thread.start();
	} catch (IOException e) {
	    logger.error("{} port {}", e, name, port);
	}
    }

    public void stop(SID sid) {
	final MethodLogger logger = LOG.silentEnter(sid, "stop");

	running = false;
	if (channel != null) {
	    try {
		channel.close();
	    } catch (IOException e) {
		logger.error("port {}", e, port);
	    }
	}
	try {
	    thread.join();
	} catch (InterruptedException e) {
	    logger.error("", e);
	}
    }

}
