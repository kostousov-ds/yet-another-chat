package net.kst_d.lab.yac.cmd;

import java.util.UUID;
import java.util.stream.Collectors;

import net.kst_d.common.SID;
import net.kst_d.lab.yac.Context;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.User;

public class HelpCommand implements Command {
    @Override
    public String name() {
	return "help";
    }

    @Override
    public String desc() {
	return "Справка по известным командам";
    }

    @Override
    public void exec(Context ctx, Message msg, User user, SID sid) {
	String text = ctx.knownCommands().stream().map(c -> COMMAND_PREFFIX + c.name() + " - " + c.desc()).collect(Collectors.joining("\n"));

	final UUID to = msg.getConnection();
	ctx.sender().send(to, new Message(to, text));
    }

    @Override
    public boolean allowAnonymous() {
	return true;
    }

    @Override
    public void execAnonymous(Context ctx, Message msg, SID sid) {
	String text = ctx.knownCommands().stream().filter(Command::allowAnonymous).map(c -> COMMAND_PREFFIX + c.name() + " - " + c.desc()).collect(Collectors.joining("\n"));

	final UUID to = msg.getConnection();
	ctx.sender().send(to, new Message(to, text));

    }
}
