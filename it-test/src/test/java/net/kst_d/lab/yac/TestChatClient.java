package net.kst_d.lab.yac;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.kst_d.lab.yac.tcp.TcpClient;

public class TestChatClient {

    private ClientTransport transport;
    private List<Message> messages = new ArrayList<>(100);
    private final String name;
    private final String host;
    private final int port;
    protected final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private ExecutorService es = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, workQueue);

    public TestChatClient(String name, String host, int port) {
	this.name = name;
	this.host = host;
	this.port = port;
    }


    public void start() {
	transport = new TcpClient(port, host);
	transport.setListener(messages::add);
	transport.start();
    }

    public void login() {
	send("\\login " + name);
    }

    public void send(String msg) {
	es.submit(() -> transport.send(msg));
    }

    public void send(List<String> msgs) {
	msgs.forEach(msg -> es.submit(() -> transport.send(msg)));
    }

    public int stop() {
	transport.stop();
	return es.shutdownNow().size();
    }

    public ClientTransport getTransport() {
	return transport;
    }

    public List<Message> getMessages() {
	return messages;
    }

    public String getName() {
	return name;
    }

    public String getHost() {
	return host;
    }

    public int getPort() {
	return port;
    }

    public ExecutorService getEs() {
	return es;
    }

    public int queueSize() {
        return workQueue.size();
    }
}
