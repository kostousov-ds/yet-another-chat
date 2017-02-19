package net.kst_d.lab.yac;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.lab.yac.cmd.Command;

public class CommandProcessor {
    private static final KstLogger LOG = KstLoggerFactory.logger(CommandProcessor.class);

    protected ExecutorService es = Executors.newSingleThreadExecutor();
    protected final CircularFifoQueue<Message> messages = new CircularFifoQueue<>(100);
    protected final Context ctx;

    public CommandProcessor(ServerMessageSender sender) {
	ctx = new Context(sender, () -> new ArrayList<>(messages));
    }


    public void process(Message msg, SID sid) {
	UUID connection = msg.getConnection();
	User user = ctx.findUser(connection);
	final Command command = findCommand(msg.getData());
	if (user == null) {
	    if (command == null) {
		sendReply(ctx, connection, "Вы должны залогироваться. Помощь \\help");
	    } else {
		command.execAnonymous(ctx, msg, sid);
	    }
	} else {
	    if (command == null) {
		onMessage(ctx, msg, user);
	    } else {
		command.exec(ctx, msg, user, sid);
	    }
	}


    }

    Command findCommand(String msg) {
	if (msg == null) {
	    return null;
	}
	String data = msg.trim();
	if (data.length() <= 1 || data.charAt(0) != Command.COMMAND_PREFFIX) {
	    return null;
	}

	final String cmdCandidate;
	final int index = data.indexOf(' ');
	if (index < 0) {
	    cmdCandidate = data.substring(1);
	} else {
	    cmdCandidate = data.substring(1, index);
	}

	return ctx.commandMap().get(cmdCandidate);
    }

    private void sendReply(Context ctx, UUID connection, String message) {
	es.submit(() -> ctx.sender().send(connection, new Message(connection, message)));
    }

    private void onMessage(Context ctx, Message msg, User user) {
	Message message = new Message(msg.getConnection(), user.getLogin() + ": " + msg.getData());
	es.submit(() -> {
	    messages.add(message);
	    ctx.sender().broadcast(message);
	});
    }
}
