package net.kst_d.lab.yac.cmd;

import java.util.UUID;

import net.kst_d.common.SID;
import net.kst_d.lab.yac.Context;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.User;

public interface Command {
    char COMMAND_PREFFIX='\\';

    String name();

    String desc();

    default boolean allowAnonymous() {
	return false;
    }

    void exec(Context ctx, Message msg, User user, SID sid);

    default void execAnonymous(Context ctx, Message msg, SID sid){
	final UUID connection = msg.getConnection();
	ctx.sender().send(connection, new Message(connection, "Вы должны залогироваться. Помощь \\help"));
    }
}
