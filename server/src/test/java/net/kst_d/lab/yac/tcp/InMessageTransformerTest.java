package net.kst_d.lab.yac.tcp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import net.kst_d.lab.yac.Message;

public class InMessageTransformerTest {

    public static final byte[] DATA0 = new byte[] {'a', 'b', 'c', '\r', '\n'};
    public static final byte[] DATA1 = new byte[] {'a', 'b', 'c', '\r', '\n', 'd', 'e', 'f', 'g', '\r', '\n'};
    public static final byte[] DATA2 = new byte[] {'a', 'b', 'c'};
    public static final byte[] DATA3 = new byte[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', '\r', '\n'};
    public static final byte[] DATA4 = new byte[] {'a', 'b', 'c', '\r', '\n', '\r', '\n'};

    @Test
    public void testSearchMessages() throws Exception {

	List<Message> messages = new ArrayList<>();
	int startCopyPosition = 0;

	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA0, 0, startCopyPosition, 2, messages::add);
	Assert.assertEquals(messages.size(), 0);
	Assert.assertEquals(startCopyPosition, 0);
	messages.clear();

	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA0, 2, 0, 5, messages::add);
	Assert.assertEquals(messages.size(), 1);
	Assert.assertEquals(startCopyPosition, 5);
	Assert.assertEquals(messages.get(0).getData(), "abc");
	messages.clear();

	startCopyPosition = 0;
	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA1, 0, startCopyPosition, 6, messages::add);
	Assert.assertEquals(messages.size(), 1);
	Assert.assertEquals(startCopyPosition, 5);
	Assert.assertEquals(messages.get(0).getData(), "abc");
	messages.clear();

	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA1, 0, 0, DATA1.length, messages::add);
	Assert.assertEquals(messages.size(), 2);
	Assert.assertEquals(startCopyPosition, DATA1.length);
	Assert.assertEquals(messages.get(0).getData(), "abc");
	Assert.assertEquals(messages.get(1).getData(), "defg");
	messages.clear();

	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA2, 0, 0, DATA2.length, messages::add);
	Assert.assertEquals(messages.size(), 0);
	Assert.assertEquals(startCopyPosition, 0);
	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA3, DATA2.length, 0, DATA3.length, messages::add);
	Assert.assertEquals(messages.size(), 1);
	Assert.assertEquals(messages.get(0).getData(), "abcdefg");
	Assert.assertEquals(startCopyPosition, DATA3.length);
	messages.clear();

	startCopyPosition = InMessageTransformer.searchMessages(UUID.randomUUID(), DATA4, 0, 0, DATA4.length + 1, messages::add);
	Assert.assertEquals(messages.size(), 1);
	Assert.assertEquals(startCopyPosition, DATA4.length);


    }


    @Test
    public void testShiftToLeftEdge() {
	byte[] buffer = new byte[] {'a', 'b', 'c', 'd', 'e', 0};
	int offset = 3;
	InMessageTransformer.shiftToLeftEdge(buffer, offset, buffer.length - offset);
	ArrayAsserts.assertArrayEquals(buffer, new byte[] {'d', 'e', 0, 'd', 'e', 0});
    }
}