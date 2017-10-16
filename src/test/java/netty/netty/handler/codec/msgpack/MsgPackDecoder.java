package netty.netty.handler.codec.msgpack;


import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
 import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf,
			List<Object> arg2) throws Exception {
		final byte[] tem ;
		final int length = buf.readableBytes();
		tem = new byte[length];
		buf.getBytes(buf.readerIndex(), tem, 0, length);
		MessagePack pack = new MessagePack();
		arg2.add(pack.read(tem));
	}


}
