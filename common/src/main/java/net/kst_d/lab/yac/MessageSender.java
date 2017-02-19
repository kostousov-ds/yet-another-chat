package net.kst_d.lab.yac;

import java.util.List;
import java.util.UUID;

public interface MessageSender {
    void send(UUID to, Message message);

    void send(UUID to, List<Message> messages);
}
