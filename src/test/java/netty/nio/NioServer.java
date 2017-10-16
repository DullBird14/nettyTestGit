package netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NioServer implements Runnable{
	private Selector selector = null;
	private boolean stop = false ;
	private int port;
	public NioServer(int port) {
		this.port = port;
	}
	
	void startServer(){
//		Selector selector = null;
//		ServerSocketChannel server = null;
		try(Selector selector = Selector.open();
			ServerSocketChannel server = ServerSocketChannel.open()) {
			this.selector = selector;
//			selector = Selector.open();
//			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));
			server.register(selector, SelectionKey.OP_ACCEPT);
			handlerSelector(selector);
		} catch (IOException e) {
			//传统做法
//			if(selector != null){
//				try {
//					selector.close();
//				} catch (IOException e1) {
//				}
//			}
//			if(server != null){
//				try {
//					server.close();
//				} catch (IOException e1) {
//				}
//			}
		}
		
	}
	//异常处理，向外抛异常还是自己处理异常
	private void handlerSelector(Selector selector){
		while(!stop){
			try {
				if(selector.select(1000) <=0) continue;
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()){
					SelectionKey key = iterator.next();
					iterator.remove();
					handlerSelectKey(key);
				}
			} catch (IOException e) {
				if(selector != null){
					try {
						selector.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	private void handlerSelectKey(SelectionKey key){
		if(!key.isValid()) return ;
		try {
			if(key.isAcceptable()){
				ServerSocketChannel serChannel = (ServerSocketChannel)key.channel();
				SocketChannel channel = serChannel.accept();
				System.out.println("join a client" + channel);
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ);
			}else if(key.isReadable()){
				System.out.println("have client ready to read!!");
				SocketChannel channel = (SocketChannel)key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int read = channel.read(buffer);
				System.out.println("Request : " + buffer);
				if(read > 0){
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("Request : " + body);
					String currentTime = body.equalsIgnoreCase("query Time")? 
							new Date(System.nanoTime()).toString() : "bad query";
					buffer.clear();
					doReponse(channel, currentTime);
				}else if(read < 0){
					key.cancel();
					channel.close();
				}
			}
		} catch (Exception e) {
			if (key != null){
				key.cancel();
				if(key.channel() !=  null){
					try {
						key.channel().close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	private void doReponse(SocketChannel channel, String currentTime) throws IOException  {
//		ByteBuffer buffer = ByteBuffer.wrap(currentTime.getBytes());
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put(currentTime.getBytes());
		buffer.flip();
		channel.write(buffer);
		if(!buffer.hasRemaining()){
			System.out.println("response over!");
		}
		
	}
	
	public static void main(String[] args) {
		NioServer nioServer = new NioServer(6666);
		nioServer.run();
	}
	
	public void run() {
		startServer();
	}

}
