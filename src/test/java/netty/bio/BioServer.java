package netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {

	public static void main(String[] args) throws IOException {
		ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
		int port = 8080;
		if(args != null && args.length >0){
			try {
				port = Integer.valueOf(args[0]).intValue();
			} catch (NumberFormatException e) {
				System.out.println("输入端口错误, 采用默认端口");
			}
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			while(true){
				Socket accept = server.accept();
				newSingleThreadExecutor.execute(new taskDealThread(accept));
			}
		}finally{
			if(server !=null){
				server.close();
				server = null;
			}
		}
		
	}	
}
