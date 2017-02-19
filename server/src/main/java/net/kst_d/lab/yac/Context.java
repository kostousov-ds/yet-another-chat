package net.kst_d.lab.yac;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.kst_d.lab.yac.cmd.Command;
import net.kst_d.lab.yac.cmd.HelpCommand;
import net.kst_d.lab.yac.cmd.LastMessagesCommand;
import net.kst_d.lab.yac.cmd.LoginCommand;

public class Context {

    protected static final List<Command> list =Arrays.asList(new HelpCommand(), new LastMessagesCommand(), new LoginCommand());
    protected static final Map<String,Command> map;
    static {
        map = Collections.unmodifiableMap(list.stream().collect(Collectors.toMap(Command::name, Function.identity())));
    }

    protected final ServerMessageSender sender;
    protected final Supplier<List<Message>> lastMessagesSupplier;
    protected final Map<UUID, User> loggedByConnection = new ConcurrentHashMap<>();
    protected final Map<User, UUID> loggedByUser = new ConcurrentHashMap<>();

    public Context(ServerMessageSender sender, Supplier<List<Message>> lastMessagesSupplier) {
	this.sender = sender;
	this.lastMessagesSupplier = lastMessagesSupplier;
    }

    public List<Command> knownCommands(){
	return list;
    }

    Map<String, Command> commandMap() {
        return map;
    }

    public ServerMessageSender sender() {
	return sender;
    }

    public List<Message> lastMessages() {
        return lastMessagesSupplier.get();
    }

    public User findUser(UUID connection) {
	return loggedByConnection.get(connection);
    }

    public UUID findUser(User user){
	return loggedByUser.get(user);
    }

    public void removeConnection(UUID connection) {
	final User user = loggedByConnection.remove(connection);
	loggedByUser.remove(user);
    }

    public void addConnection(UUID connection, User user) {
	loggedByConnection.put(connection, user);
	loggedByUser.put(user, connection);
    }
}
