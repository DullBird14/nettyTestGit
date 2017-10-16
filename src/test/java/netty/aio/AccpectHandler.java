package netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AccpectHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {
	public static int number = 0;
	@Override
	public void failed(Throwable exc, AioServer attachment) {
		exc.printStackTrace();
		attachment.count.countDown();
	}

	@Override
	public void completed(AsynchronousSocketChannel result, AioServer attachment) {
		System.out.println("join a client "+ (number++) + ":" + result);
		attachment.server.accept(attachment, this);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}



}
