package net.kst_d.lab.yac;

import java.util.function.Consumer;

public interface ChatTransport {
    void start();

    void stop();

    void setListener(Consumer<Message> listener);

    void removeListener();

    ServerMessageSender sender();
}
