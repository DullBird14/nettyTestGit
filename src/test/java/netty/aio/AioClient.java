package netty.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;


public class AioClient implements CompletionHandler<Void, AioClient>{
	AsynchronousSocketChannel client ;
	CountDownLatch count ;
	void prepareClient(){
		try {
			client = AsynchronousSocketChannel.open();
//			client.bind(new InetSocketAddress("127.0.0.1", 6666));
			startClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startClient() {
		count = new CountDownLatch(1);
		client.connect(new InetSocketAddress("192.168.1.103", 6666), this, this);
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			client.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void completed(Void result, AioClient attachment) {
		ByteBuffer write = ByteBuffer.allocate(1024);
		write.put("query time".getBytes());
		write.flip();
		client.write(write, write, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				if(attachment.hasRemaining()){
					client.write(attachment, attachment, this);
				}else{
					ByteBuffer read = ByteBuffer.allocate(1024);
					client.read(read, read, new CompletionHandler<Integer, ByteBuffer>() {

						@Override
						public void completed(Integer result,ByteBuffer attachment) {
							attachment.flip();
							byte[] readBtyes = new byte[attachment.remaining()];
							attachment.get(readBtyes);
							try {
								String body = new String(readBtyes, "UTF-8");
								System.out.println("get Response :" + body);
								count.countDown();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							
							
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							
						}
					});
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					client.close();
					count.countDown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, AioClient attachment) {
		try {
			client.close();
			count.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		for(int i =0;i<=99; i++){
			AioClient client = new AioClient();
			client.prepareClient();
		}
	}
}
