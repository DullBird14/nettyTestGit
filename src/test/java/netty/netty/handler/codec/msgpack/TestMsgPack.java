package netty.netty.handler.codec.msgpack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

public class TestMsgPack {
	public static void main(String[] args) throws IOException {
		List<String> src = new ArrayList<String>();
		src.add("AAAA");
		src.add("BBBB");
		src.add("CCCC");
		MessagePack msgpack = new MessagePack();
		byte[] raw = msgpack.write(src);
		List<String> src2 = msgpack.read(raw, Templates.tList(Templates.TString));
		System.out.println(src2.get(0));
		System.out.println(src2.get(1));
		System.out.println(src2.get(2));
		
	}
}
