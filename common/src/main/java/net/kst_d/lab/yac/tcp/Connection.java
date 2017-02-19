package net.kst_d.lab.yac.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Connection {
    public final SocketChannel channel;
    public final UUID uuid;
    private final InMessageTransformer in;
    private final OutMessageTransformer out;

    public Connection(SocketChannel channel, Function<Connection, InMessageTransformer> in, BiFunction<Connection, Selector, OutMessageTransformer> out, Selector writeSelector) {
	this.channel = channel;
	uuid = UUID.randomUUID();
	this.in = in.apply(this);
	this.out = out.apply(this, writeSelector);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null || getClass() != o.getClass()) {
	    return false;
	}

	Connection that = (Connection) o;

	return uuid.equals(that.uuid);
    }

    public void acceptReveivedBytes(ByteBuffer data) {
	in.addBytes(data);
    }

    public void write(ByteBuffer bb) {
	out.write(bb);
    }

    @Override
    public int hashCode() {
	return uuid.hashCode();
    }


    @Override
    public String toString() {
	return "Connection{" +
			"uuid=" + uuid +
			", channel=" + channel +
			'}';
    }
}
