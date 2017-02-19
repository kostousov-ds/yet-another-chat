package net.kst_d.lab.yac.cmd;

import java.util.UUID;

import net.kst_d.common.SID;
import net.kst_d.lab.yac.Context;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.User;

public class LoginCommand implements Command {
    @Override
    public String name() {
	return "login";
    }

    @Override
    public String desc() {
	return "Осуществляет вход пользователя. Обязательный аргумент - имя пользователя";
    }

    @Override
    public void execAnonymous(Context ctx, Message msg, SID sid) {
	final UUID connection = msg.getConnection();
	final String[] tokens = msg.getData().split(" ");
	if (tokens.length < 2) {
	    ctx.sender().send(connection, new Message(connection, "не указано имя пользователя"));
	}else{
	    final User user = new User(tokens[1]);
	    final UUID prevConnection = ctx.findUser(user);
	    if (prevConnection == null) {
		ctx.addConnection(connection, user);
		ctx.sender().send(connection, new Message(connection, "вы вошли как " + tokens[1]));
	    }else{
		ctx.sender().send(connection, new Message(connection, "имя уже занято, используйте другое"));
	    }
	}
    }

    @Override
    public void exec(Context ctx, Message msg, User user, SID sid) {
	final UUID connection = msg.getConnection();
	ctx.sender().send(connection, new Message(connection, "вы уже вошли как " + user.getLogin()));
    }

    @Override
    public boolean allowAnonymous() {
	return true;
    }
}
