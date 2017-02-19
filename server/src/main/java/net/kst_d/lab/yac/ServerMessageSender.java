package net.kst_d.lab.yac;

import java.util.List;

public interface ServerMessageSender extends MessageSender {

    void broadcast(Message message);

    void broadcast(List<Message> messages);
}
