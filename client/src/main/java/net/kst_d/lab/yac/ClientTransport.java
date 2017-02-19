package net.kst_d.lab.yac;

import java.util.function.Consumer;

public interface ClientTransport {
    void start();

    void setListener(Consumer<Message> consumer);

    void removeListener();

    void stop();

    void send(String message);
}
