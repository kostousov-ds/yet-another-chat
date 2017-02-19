package net.kst_d.lab.yac.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import net.kst_d.common.log.MethodLogger;

public class TcpUtils {
    static boolean tryWriteSomething(ByteBuffer buffer, Selector selector, MethodLogger logger) throws IOException {
	boolean nothing = true;
	if (selector.selectNow() > 0) {
	    nothing = false;
	    final Set<SelectionKey> keys = selector.selectedKeys();
	    for (Iterator<SelectionKey> iterator = keys.iterator(); iterator.hasNext(); ) {
		SelectionKey key = iterator.next();
		iterator.remove();

		final Connection connection = (Connection) key.attachment();
		if (connection != null) {
		    connection.write(buffer);
		} else {
		    logger.debug("Select channel without attachment! {}", key.channel());
		}
	    }
	}
	return nothing;
    }

    static boolean tryReadSomething(ByteBuffer buffer, Selector selector) throws IOException {
	boolean nothing = true;
	if (selector.selectNow() > 0) {
	    nothing = false;
	    final Set<SelectionKey> keys = selector.selectedKeys();

	    for (Iterator<SelectionKey> iterator = keys.iterator(); iterator.hasNext(); ) {
		SelectionKey key = iterator.next();
		iterator.remove();

		final Connection connection = (Connection) key.attachment();
		final int read = connection.channel.read(buffer);
		if (read > 0) {
		    buffer.flip();
		    if (buffer.remaining() > 0) {
			connection.acceptReveivedBytes(buffer);
		    }
		    buffer.clear();
		}
	    }
	}
	return nothing;
    }
}
