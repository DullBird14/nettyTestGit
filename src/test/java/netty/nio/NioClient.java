package netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {
	
	public static void main(String[] args) throws IOException {
		boolean stop = false;
		Selector selector = Selector.open();
		SocketChannel client = SocketChannel.open();
		client.configureBlocking(false);
		System.out.println("连接服务端");
		if(client.connect(new InetSocketAddress("127.0.0.1", 6666))){
			System.out.println("连接成功");
			client.register(selector, SelectionKey.OP_READ);
		}else{
			System.out.println("连接失败");
			client.register(selector, SelectionKey.OP_CONNECT);
		}
		System.out.println("启动客户端成功");
		while(!stop){
//			System.out.println("开始选择：");
			if(selector.select(1000) <=0)  continue;
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				iterator.remove();
				if(!key.isValid()) continue;
				SocketChannel channel = (SocketChannel)key.channel();
				if(key.isConnectable()){
					if(channel.finishConnect()){
						channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
						byte[] bytes = "query time".getBytes();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						//下面这种情况无法将缓存写入
//						ByteBuffer buffer = ByteBuffer.wrap("query time".getBytes());
						buffer.put(bytes);
						buffer.flip();
						channel.write(buffer);
						if(!buffer.hasRemaining()){
							System.out.println("写入结束");
						}else{
							System.out.println("写入失败");
						}
					}else{
						System.exit(1);
					}
				}
				if(key.isReadable()){
//					if(key.isWritable()){
//						try {
//							System.out.println("可写");
//							ByteBuffer buffer = ByteBuffer.allocate(1024);
//							buffer.put("test Connection".getBytes());
//							buffer.flip();
//							channel.write(buffer);
//						} catch (Exception e) {
//							
//						}
//					}
					System.out.println("client has response!");
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int read = channel.read(buffer);
					if(read > 0){
						buffer.flip();
						byte[] bytes = new byte[buffer.remaining()];
						buffer.get(bytes);
						System.out.println("time is :" + new String(bytes, "UTF-8"));
						stop = true;
					}else if(read < 0){
						key.cancel();
						channel.close();
					}else if(read == -1){
						System.out.println("断开了连接");
						System.exit(1);
					}
				}
			}
		}
	}
}
