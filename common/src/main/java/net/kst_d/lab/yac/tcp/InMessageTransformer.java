package net.kst_d.lab.yac.tcp;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;

import net.kst_d.common.SID;
import net.kst_d.common.log.KstLogger;
import net.kst_d.common.log.KstLoggerFactory;
import net.kst_d.common.log.MethodLogger;
import net.kst_d.lab.yac.Constants;
import net.kst_d.lab.yac.Message;

public class InMessageTransformer {
    private static final KstLogger LOG = KstLoggerFactory.logger(InMessageTransformer.class);
    final MethodLogger logger = LOG.silentEnter(SID.NONE, "--unknown--");

    private final Consumer<Message> consumer;
    private final Connection connection;

    private byte[] buffer = new byte[Constants.MESSAGE_TRANSFORMER_BUFF_LEN];
    private int position;

    public InMessageTransformer(Connection connection, Consumer<Message> consumer) {
	this.consumer = consumer;
	this.connection = connection;
    }

    public void addBytes(ByteBuffer data) {
	int free = buffer.length - position;
	int count = Math.min(data.remaining(), free);
	if (count > 0) {
	    //если хоть что-то прочитали
	    try {
		data.get(buffer, position, count);
		int startCopyPosition = 0;
		int startSearchPosition = Math.max(0, position - 1);
		position += count;

		startCopyPosition = searchMessages(connection.uuid, buffer, startSearchPosition, startCopyPosition, position, consumer);

		shiftToLeftEdge(buffer, startCopyPosition, position - startCopyPosition);
		position -= startCopyPosition;
	    } catch (Exception e) {
		logger.error("free {}, count {}, position {}, data {}", free, count, position, data, e);
	    }
	}
    }

    static void shiftToLeftEdge(byte[] buffer, int offset, int len) {
	System.arraycopy(buffer, offset, buffer, 0, len);
    }

    static int searchMessages(UUID source, byte[] buffer, int searchOffset, int copyOffset, int rightEdge, Consumer<Message> consumer) {
	for (int i = searchOffset; i < rightEdge - 1; i++) {
	    if (buffer[i] == Constants.MESSAGE_TERMINATOR[0] && buffer[i + 1] == Constants.MESSAGE_TERMINATOR[1]) {
		final int len = i - copyOffset;
		if (len > 0) {
		    String msg = new String(buffer, copyOffset, len);
		    consumer.accept(new Message(source, msg));
		}
		//пропускаем разделители
		copyOffset = i + 2;
		i++;
	    }
	}

	return copyOffset;
    }
}
