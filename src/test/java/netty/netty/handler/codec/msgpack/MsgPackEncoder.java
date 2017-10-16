package netty.netty.handler.codec.msgpack;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgPackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf buf)
			throws Exception {
		MessagePack msg = new MessagePack();
		byte[] write = msg.write(obj);
		buf.writeBytes(write);
	}

}
