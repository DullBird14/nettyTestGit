package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException, InterruptedException {
		ServerSocket server = new ServerSocket(6666);
		while(true){
			Socket accept = server.accept();
			System.out.println(accept);
			Thread.sleep(2000);
		}
	}
}
