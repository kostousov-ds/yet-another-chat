package net.kst_d.lab.yac.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;
import net.kst_d.lab.yac.Constants;
import net.kst_d.lab.yac.Message;
import net.kst_d.lab.yac.SingleConnectionMessageSender;

public class OutMessageTransformer implements SingleConnectionMessageSender {
    private static final KstLogger LOG = KstLoggerFactory.logger(OutMessageTransformer.class);

    private final byte[] buffer = new byte[Constants.MESSAGE_TRANSFORMER_BUFF_LEN];
    private final Connection connection;
    private final Selector selector;
    private final MethodLogger logger = LOG.silentEnter(SID.NONE, "write");
    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();


    private int rightEdge = 0;
    private int offset = 0;

    public OutMessageTransformer(Connection connection, Selector selector) {
	this.connection = connection;
	this.selector = selector;
    }

    public void write(ByteBuffer bb) {
	if (rightEdge > offset) {
	    bb.clear();
	    bb.put(buffer, offset, rightEdge);
	    bb.flip();
	    try {
		final int r = connection.channel.write(bb);
		offset += r;
	    } catch (Exception e) {
		logger.warn("channel {}: {}", connection.uuid, e);
	    }
	}else{
	    offset = 0;
	    rightEdge = 0;
	    Message message;
	    if ((message = queue.poll()) != null) {
		logger.trace("out {}", message);
		final byte[] bytes = message.getData().getBytes(StandardCharsets.UTF_8);
		int len = Math.min(buffer.length - 2, bytes.length);
		System.arraycopy(bytes, 0, buffer, 0, len);
		buffer[len] = 0xd;
		buffer[len + 1] = 0xa;
		rightEdge = len + 2;
	    } else {
		SelectionKey key;
		if ((key = connection.channel.keyFor(selector)) != null) {
		    key.cancel();
		}
	    }
	}
    }

    @Override
    public void send(UUID to, Message message) {
	queue.add(message);
	registerForWrite();
    }

    @Override
    public void send(UUID to, List<Message> messages){
        queue.addAll(messages);
        registerForWrite();
    }

    @Override
    public UUID connection() {
	return connection.uuid;
    }

    private void registerForWrite() {
	try {
	    final SelectionKey key = connection.channel.register(selector, SelectionKey.OP_WRITE);
	    key.attach(connection);
	} catch (ClosedChannelException e) {
	    logger.warn("channel {}:{}", connection.uuid, e);
	}
    }

}
