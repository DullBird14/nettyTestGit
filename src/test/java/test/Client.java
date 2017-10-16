package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws IOException, InterruptedException {
		Socket client = new Socket("127.0.0.1",6666);
		while(true){
			System.out.println("isConnected:" + client.isConnected());
			System.out.println("isClosed:" + client.isClosed());
			Thread.sleep(2000);
		}
	}
}
