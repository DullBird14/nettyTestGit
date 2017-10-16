package netty.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements
		CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel channel;
	
	ReadCompletionHandler(AsynchronousSocketChannel channel){
		if(this.channel == null ) this.channel = channel;
	}
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try {
			String time = new String(body, "UTF-8");
			System.out.println("get request:" + body);
			time = time.equalsIgnoreCase("query time")? new Date().toString():"bad request";
			doWrite(time);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	private void doWrite(String time) {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put(time.getBytes());
		buffer.flip();
		channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				if(attachment.hasRemaining()){
					channel.write(attachment, attachment, this);
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			exc.printStackTrace();
			e.printStackTrace();
		}
	}

}
