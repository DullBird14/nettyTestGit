package netty.netty.temp;

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

import java.util.Date;


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
			System.out.println("开始启动服务");
			ChannelFuture sync = server.bind(port).sync();
			sync.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		NettyServer server = new NettyServer();
		server.bind(6000);
	}
}

class ServerTimeHandler extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
//		ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
//		ch.pipeline().addLast("msgPack decoder", new MsgPackDecoder());
//		ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
//		ch.pipeline().addLast("msgPack encoder", new MsgPackEncoder());
		ch.pipeline().addLast(new EchoServerHandler());
//		ch.pipeline().addLast(new ServerTimeAdapter());
	}
}

class EchoServerHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("1111111111111");
		System.out.println("recive client request:" + msg);
		ctx.writeAndFlush(msg);
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
class ServerTimeAdapter extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("get request:" + body);
		String currentTime = "query time".equalsIgnoreCase(body)?
				new Date().toString() : "bad requset";
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


