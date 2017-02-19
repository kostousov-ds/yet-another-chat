package net.kst_d.lab.yac;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.kst_d.common.Generator;
import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;
import net.kst_d.lab.yac.tcp.TcpClient;

public class ChatClient {
    private static final KstLogger LOG = KstLoggerFactory.logger(ChatClient.class);

    private volatile boolean running = false;
    protected Thread thread;

    public static void main(String[] args) {

	final ChatClient client = new ChatClient();
	client.start();


    }

    private void start() {
	SID sid = Generator.sid();
	final MethodLogger logger = LOG.silentEnter(sid, "start");
	ClientTransport transport = new TcpClient(8888, "localhost");

	Queue<String> queue = new ConcurrentLinkedQueue<>();

	transport.setListener(m -> queue.add(m.getData()));


	running = true;
	thread = new Thread(() -> {
	    try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (running) {
		    boolean nothing = true;


		    if (reader.ready()) {
			nothing = false;
			final String msg = reader.readLine();
			transport.send(msg);
		    }

		    String msg;
		    if ((msg = queue.poll()) != null) {
		        nothing = false;
			System.out.println(msg.trim());
		    }

		    if (nothing) {
			Thread.sleep(200);
		    }
		}
	    } catch (Exception e) {
		logger.error("", e);
	    }
	}, "yac-client");
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    running = false;
	    try {
		thread.join();
	    } catch (InterruptedException e) {
		logger.error("", e);
	    }
	}));

	thread.start();
	transport.start();

    }
}
