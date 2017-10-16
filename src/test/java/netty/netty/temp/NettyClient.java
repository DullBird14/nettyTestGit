package netty.netty.temp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import netty.netty.entity.UserInfo;
import netty.netty.handler.codec.msgpack.MsgPackDecoder;
import netty.netty.handler.codec.msgpack.MsgPackEncoder;


public class NettyClient {
	void connect(String ip, int port) throws InterruptedException{
		Bootstrap client = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			client.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
						ch.pipeline().addLast("msgPack decoder", new MsgPackDecoder());
						ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
						ch.pipeline().addLast("msgPack encoder", new MsgPackEncoder());
						ch.pipeline().addLast(new EchoClientHandler(10));
					}
				});
			System.out.println("开始启动客户端");
			ChannelFuture sync = client.connect(ip, port).sync();
			sync.channel().closeFuture().sync();
		}finally{
			group.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		NettyClient client = new NettyClient();
		client.connect("192.168.1.103", 6000);
	}
}

class EchoClientHandler extends ChannelHandlerAdapter{
	private final int sendNummber;
	
	EchoClientHandler(int number){
		this.sendNummber = number;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		UserInfo[] info = UserInfo();
		for(UserInfo tem : info){
			System.out.println("开始写入" + tem );
			ctx.write(tem);
		}
		ctx.flush();
	}

	private UserInfo[] UserInfo() {
		UserInfo[] tem = new UserInfo[sendNummber];
		for(int i = 0 ; i < sendNummber; i++){
			UserInfo info = new UserInfo();
			info.setAge(i);
			info.setName("ABCD-------->" + i);
			tem[i] = info;
		}
		return tem;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("recive server request:" + msg);
		ctx.writeAndFlush(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
	
}
