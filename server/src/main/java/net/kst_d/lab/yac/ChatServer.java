package net.kst_d.lab.yac;

import net.kst_d.common.SID;
import net.kst_d.lab.yac.tcp.TcpTransport;

public class ChatServer {
    protected final int port;
    protected ChatTransport transport;
    protected CommandProcessor processor;

    public ChatServer(int port) {
	this.port = port;
    }

    public static void main(String[] args) {
        ChatServer cs = new ChatServer(8888);
        cs.start();

	Runtime.getRuntime().addShutdownHook(new Thread(cs::stop));
    }

    public void start() {
	transport = new TcpTransport(port);

	processor = new CommandProcessor(transport.sender());
	transport.setListener(msg -> processor.process(msg, SID.NONE));

	transport.start();

    }

    public void stop() {
	if (transport != null) {
	    transport.stop();
	}
    }

}
