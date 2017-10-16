package netty.netty;

import java.nio.Buffer;

import netty.netty.entity.UserInfo;
import netty.netty.handler.codec.marshalling.MaeshallingFactory;
import netty.netty.handler.codec.msgpack.MsgPackDecoder;
import netty.netty.handler.codec.msgpack.MsgPackEncoder;
import netty.netty.handler.codec.protobuf.SubscribeReqProto;
import netty.netty.handler.codec.protobuf.SubscribeRespProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
	EchoClientHandler handler = new EchoClientHandler(10);
	void connect(String ip, int port) throws Exception{
		NioEventLoopGroup workThread = new NioEventLoopGroup();
		try {
			Bootstrap client = new Bootstrap();
			//配置文件容易漏
			client.group(workThread)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
//					//特定标识解码器
//					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,
//							Unpooled.copiedBuffer("_$".getBytes())));
					//行
//					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoClientHandler(10));
					ch.pipeline().addLast(new StringEncoder());
//					ch.pipeline().addLast(new TimeClientHandler());
//					ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
//					ch.pipeline().addLast("msgPack decoder", new MsgPackDecoder());
//					ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
//					ch.pipeline().addLast("msgPack encoder", new MsgPackEncoder());
//					forProtoBuf(ch);
//					forJboss(ch);
				}
				private void forProtoBuf(SocketChannel ch){
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new EchoClientHandler(10));
				}
				private void forJboss(SocketChannel ch){
					ch.pipeline().addLast(MaeshallingFactory.decode());
					ch.pipeline().addLast(MaeshallingFactory.encode());
					ch.pipeline().addLast(new EchoClientHandler(10));
				}
			});
			new Thread(new Runnable() {
				
				@Override
				public void run() {
//					while(!handler.getStatus()){ System.out.println(handler.getStatus());}
//					Channel channel = handler.getChannel();
					while(!handler.getStatus()){
						handler.getChannel();
						System.out.println("获取channel" + handler.getStatus());
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.doWrite();
					System.out.println("结束");
//					while(true){
//						System.out.println("判断channel" + channel.isWritable());
//						if(channel.isWritable()){
//							UserInfo info = new UserInfo();
//							info.setAge(14);
//							info.setName("测试>" + 14);
//							channel.writeAndFlush(info);
//							try {
//								Thread.sleep(50000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
				}
			}).start();
			ChannelFuture sync = client.connect(ip, port).sync();
			
			sync.channel().closeFuture().sync();
		} finally{
			workThread.shutdownGracefully();
		}
		
	}
	public static void main(String[] args) {
		NettyClient nettyClient = new NettyClient();
		try {
			nettyClient.connect("127.0.0.1", 60000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class EchoClientHandler extends ChannelHandlerAdapter{
	private final int sendNummber;
	
	private static Channel channel = null;
	
	EchoClientHandler(int number){
		this.sendNummber = number;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		UserInfo[] info = UserInfo();
//		for(UserInfo tem : info){
////			ByteBuf buf;
//			System.out.println("开始写入" + tem );
////			byte[] bytes = tem.toString().getBytes();
////			buf = Unpooled.buffer(bytes.length);
////			buf.writeBytes(bytes);
//			ctx.write(tem);
//		}
//		ctx.flush();
		channel = ctx.channel();
		System.out.println("读取" + channel);
//		protoBufActiveClient(ctx);
	}
	
	private void protoBufActiveClient(ChannelHandlerContext ctx){
		for(int i =0; i< sendNummber; i++){
			ctx.write(subReq(i));
		}
		ctx.flush();
	}
	
	public Channel getChannel(){
		return channel;
	}
	public boolean getStatus(){
		if(channel==null){
			System.out.println("空链路" );
			return false;
		}
		return channel.isActive();
	}
	public void doWrite(){
		System.out.println(channel);
		channel.writeAndFlush("发送");
	}
	private SubscribeReqProto.SubscribeReq subReq(int i){
		SubscribeReqProto.SubscribeReq.Builder build = SubscribeReqProto.SubscribeReq.newBuilder();
		build.setSubReqID(i);
		build.setUserName("cys");
		build.setProductName("Netty book");
//		List<String> address = new ArrayList<String>();
//		address.add("hangzhou");
//		address.add("wenzhou");
//		address.add("yueqing");
		build.setAddress("hangzhou");
		return build.build();
	}
	private UserInfo[] UserInfo() {
		UserInfo[] tem = new UserInfo[sendNummber];
		UserInfo info = null;
		for(int i = 0 ; i < sendNummber; i++){
			info = new UserInfo();
			info.setAge(i);
			info.setName("ABCD-------->" + i);
			tem[i] = info;
		}
		return tem;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		System.out.println("recive server request:" + msg);
//		ctx.write(msg);
		protoBufReadClient(ctx, msg);
	}

	private void protoBufReadClient(ChannelHandlerContext ctx, Object msg){
		System.out.println("recive server request:" + msg);
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
class TimeClientHandler extends ChannelHandlerAdapter{
	private ByteBuf firstMessage = null;
	byte[] tem = null;
	public TimeClientHandler() {
		//未添加解码器
//		tem = "query time".getBytes();
		//添加解码器
//		tem = ("query time" + System.getProperty("line.separator")).getBytes();
		tem = ("query time" + "_$").getBytes();
		
//		firstMessage = Unpooled.copiedBuffer("query time".getBytes());
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf buf = null;
		for(int i = 0 ; i < 100 ; i++){
			buf = Unpooled.buffer(tem.length);
			buf.writeBytes(tem);
			ctx.writeAndFlush(buf);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//未添加解码器
//		ByteBuf reBuf = (ByteBuf)msg;
//		byte[] reBytes = new byte[reBuf.readableBytes()];
//		reBuf.readBytes(reBytes);
//		String body = new String(reBytes, "UTF-8");
		//添加解码器
		String body = (String)msg;		
		System.out.println("get server request:" + body);
	}
	
}



