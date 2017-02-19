package net.kst_d.lab.yac.cmd;

import java.util.List;

import net.kst_d.common.SID;
import net.kst_d.lab.yac.Context;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.User;

public class LastMessagesCommand implements  Command {
    @Override
    public String name() {
	return "last";
    }

    @Override
    public String desc() {
	return "отправляет клиенту несколько последних сообщений";
    }

    @Override
    public void exec(Context ctx, Message msg, User user, SID sid) {
	final List<Message> messages = ctx.lastMessages();
	ctx.sender().broadcast(messages);
    }
}
