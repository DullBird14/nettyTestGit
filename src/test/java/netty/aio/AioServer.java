package netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;
//未对异常做出处理
public class AioServer implements Runnable {
	AsynchronousServerSocketChannel server ;
	CountDownLatch count ;
	
	void prepareServer(int port){
		try {
			server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress(port));
			System.out.println("server bind in port " + port);
		} catch (IOException e) {}
		
	}
	
	void startServer(){
		count = new CountDownLatch(1);
		doAccpect();
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void doAccpect() {
		server.accept(this, new AccpectHandler());
	}

	@Override
	public void run() {
	}
	
	public static void main(String[] args) {
		AioServer aioServer = new AioServer();
		aioServer.prepareServer(6666);
		aioServer.startServer();
	}
}