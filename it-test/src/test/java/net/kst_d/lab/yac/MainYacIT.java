package net.kst_d.lab.yac;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import net.kst_d.common.Generator;
import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;
import net.kst_d.lab.yac.tcp.TcpTransport;
import net.kst_d.lab.yac.tcp.TcpTransportBackDoor;

public class MainYacIT {
    private static final KstLogger LOG = KstLoggerFactory.logger(MainYacIT.class);

    public static final String SUITE = "MAIN";

    public static final int CLIENTS_COUNT = 500;
    public static final int MESSAGES_COUNT = 20;
    public static final int WAIT_MAX_MILLIS = 600_000;

    private SID sid = Generator.sid();

    @Test (description = "Запуск YAC Server", suiteName = SUITE, testName = "server start")
    public void testServerStart(ITestContext context) throws Exception {
	ChatServer server = new ChatServer(YacTestConstants.SERVER_PORT);
	Assert.assertNotNull(server);
	server.start();
	Thread.sleep(200);
	final TcpTransportBackDoor backDoor = new TcpTransportBackDoor((TcpTransport) server.transport);
	Assert.assertNotNull(backDoor.channels());
	Assert.assertEquals(backDoor.channels().size(), 0);
	Assert.assertTrue(backDoor.serverChannel().isOpen());
	Assert.assertFalse(backDoor.serverChannel().isRegistered());

	context.setAttribute(YacTestConstants.CTX_PARAM_SERVER, server);
	context.setAttribute(YacTestConstants.CTX_PARAM_TRANSPORT, backDoor);
    }

    @Test (suiteName = SUITE, dependsOnMethods = {"testServerStart"})
    public void massiveClientConnectionTest(ITestContext context) throws Exception {
	final MethodLogger logger = LOG.silentEnter(sid, "massiveClientConnectionTest");

	final List<TestChatClient> clients = IntStream.range(0, CLIENTS_COUNT).boxed()
			.map(i -> String.format("client-%04d", i))
			.map(s -> new TestChatClient(s, "localhost", YacTestConstants.SERVER_PORT))
			.collect(Collectors.toList());
	Assert.assertNotNull(clients);
	Assert.assertEquals(clients.get(0).getName(), "client-0000");

	clients.forEach(TestChatClient::start);
	Thread.sleep(200);
	logger.debug("{} clients started", CLIENTS_COUNT);

	final TcpTransportBackDoor backDoor = (TcpTransportBackDoor) context.getAttribute(YacTestConstants.CTX_PARAM_TRANSPORT);
	Assert.assertEquals(backDoor.consumers().size(), CLIENTS_COUNT);

	context.setAttribute(YacTestConstants.CTX_PARAM_CLIENTS, clients);
    }

    @Test (suiteName = SUITE, dependsOnMethods = "massiveClientConnectionTest")
    public void massiveLoginTest(ITestContext context) throws Exception {
	final MethodLogger logger = LOG.silentEnter(sid, "massiveLoginTest");
	final List<TestChatClient> clients = (List<TestChatClient>) context.getAttribute(YacTestConstants.CTX_PARAM_CLIENTS);
//	final TcpTransportBackDoor backDoor = (TcpTransportBackDoor) context.getAttribute(YacTestConstants.CTX_PARAM_TRANSPORT);
	final ChatServer server = (ChatServer) context.getAttribute(YacTestConstants.CTX_PARAM_SERVER);

	clients.forEach(TestChatClient::login);
	Thread.sleep(200);
	logger.debug("{} clients logged", CLIENTS_COUNT);
	Assert.assertEquals(server.processor.ctx.loggedByConnection.size(), CLIENTS_COUNT);
	Assert.assertEquals(clients.get(0).getMessages().size(), 1);
	Assert.assertEquals(clients.get(0).getMessages().get(0).getData(), "вы вошли как client-0000");
    }

    @Test (suiteName = SUITE, dependsOnMethods = "massiveLoginTest")
    public void massiveChattingTest(ITestContext context) throws Exception {
	final MethodLogger logger = LOG.silentEnter(sid, "massiveChattingTest");

	final List<TestChatClient> clients = (List<TestChatClient>) context.getAttribute(YacTestConstants.CTX_PARAM_CLIENTS);
//	final TcpTransportBackDoor backDoor = (TcpTransportBackDoor) context.getAttribute(YacTestConstants.CTX_PARAM_TRANSPORT);
	final ChatServer server = (ChatServer) context.getAttribute(YacTestConstants.CTX_PARAM_SERVER);

	final List<String> messages = IntStream.range(0, MESSAGES_COUNT).boxed()
			.map(i -> String.format("message-%04d", i))
			.collect(Collectors.toList());

	clients.forEach(client -> client.send(messages));
	do {
	    Thread.sleep(500);
	} while (clients.stream().anyMatch(c -> c.queueSize() > 0));
	logger.debug("All {} messages was sent", CLIENTS_COUNT * MESSAGES_COUNT);

	long before = System.currentTimeMillis();
	int current;
	while ((current = clients.get(0).getMessages().size()) < 1 + CLIENTS_COUNT * MESSAGES_COUNT && System.currentTimeMillis() < before + WAIT_MAX_MILLIS) {
	    Thread.sleep(500);
	}
	Assert.assertEquals(current, 1 + CLIENTS_COUNT * MESSAGES_COUNT);
    }

    @AfterSuite
    public void shutdown(ITestContext context) {
	final MethodLogger logger = LOG.silentEnter(sid, "shutdown");
	logger.debug("shutdown ...");
	final Object attribute = context.getAttribute(YacTestConstants.CTX_PARAM_SERVER);
	Assert.assertNotNull(attribute);
	Assert.assertTrue(attribute instanceof ChatServer);

	ChatServer server = (ChatServer) attribute;
	server.stop();

	final List<TestChatClient> clients = (List<TestChatClient>) context.getAttribute(YacTestConstants.CTX_PARAM_CLIENTS);
	clients.forEach(TestChatClient::stop);
	logger.debug("all threads was stopped");
    }
}
