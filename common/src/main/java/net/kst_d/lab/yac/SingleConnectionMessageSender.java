package net.kst_d.lab.yac;

import java.util.UUID;

public interface SingleConnectionMessageSender extends MessageSender{
    UUID connection();
}
