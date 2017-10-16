package netty.netty;

import java.util.Date;

import netty.netty.handler.codec.marshalling.MaeshallingFactory;
import netty.netty.handler.codec.msgpack.MsgPackDecoder;
import netty.netty.handler.codec.msgpack.MsgPackEncoder;
import netty.netty.handler.codec.protobuf.SubscribeReqProto;
import netty.netty.handler.codec.protobuf.SubscribeRespProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class NettyServer {
	void bind(int port) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new ServerTimeHandler());
			System.out.println("绑定端口" + port + "启动服务");
			ChannelFuture sync = server.bind(port).sync();
			sync.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		NettyServer nettyServer = new NettyServer();
		try {
			nettyServer.bind(60000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ServerTimeHandler extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		//行解码器
//		ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
		//特定标识解码器
//		ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,
//				Unpooled.copiedBuffer("_$".getBytes())));
		//字符解码器
		ch.pipeline().addLast(new StringDecoder());
		ch.pipeline().addLast(new EchoServerHandler());
		ch.pipeline().addLast(new StringEncoder());
//		ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
//		ch.pipeline().addLast("msgPack decoder", new MsgPackDecoder());
//		ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
//		ch.pipeline().addLast("msgPack encoder", new MsgPackEncoder());
//		forProtobuf(ch);
//		forJboss(ch);
//		ch.pipeline().addLast(new ServerTimeAdapter());
	}
	private void forJboss(SocketChannel ch){
		ch.pipeline().addLast(MaeshallingFactory.decode());
		ch.pipeline().addLast(MaeshallingFactory.encode());
		ch.pipeline().addLast(new EchoServerHandler());
	}
	private void forProtobuf(SocketChannel ch){
		ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
		ch.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
		ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
		ch.pipeline().addLast(new ProtobufEncoder());
		ch.pipeline().addLast(new EchoServerHandler());
	}
}
class EchoServerHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		System.out.println("1111111111111");
		System.out.println("recive client request:" + msg);
//		ctx.write(msg);
//		protoBufRead(ctx, msg);
	}
	//protoBuf
	private void protoBufRead(ChannelHandlerContext ctx, Object msg){
		SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq)msg;
		if("cys".equals(req.getUserName())){
			System.out.println("server get :" + req);
			ctx.writeAndFlush(resp(req.getSubReqID()));
		}
	}
	
	private SubscribeRespProto.SubscribeResp resp(int subReqID) {
		SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
		builder.setSubReqID(subReqID);
		builder.setRespCode(0);
		builder.setDesc("order by success");
		return builder.build();
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
}
class ServerTimeAdapter extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("读取请求端的信息！");
		//没有添加解码器
//		ByteBuf buf = (ByteBuf)msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req, "UTF-8");
		//添加字符解码器
		String body = (String)msg;
		System.out.println("get request:" + body);
		String currentTime = "query time".equalsIgnoreCase(body)?
				new Date().toString() : "bad requset";
//		currentTime = currentTime + System.getProperty("line.separator");
		currentTime = currentTime + "_$";
		//写入请求
		ByteBuf reBuf = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.write(reBuf);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
}


