package net.kst_d.lab.yac.tcp;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public interface ConnectionFactory {
    Connection newConnection(SocketChannel channel, Selector writerSelector);
}
