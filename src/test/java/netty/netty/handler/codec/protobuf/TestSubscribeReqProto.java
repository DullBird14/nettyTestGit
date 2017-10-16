package netty.netty.handler.codec.protobuf;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestSubscribeReqProto {
	private static byte[] encode(SubscribeReqProto.SubscribeReq req){
		return req.toByteArray();
	}
	
	private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException{
		return SubscribeReqProto.SubscribeReq.parseFrom(body);
	}
	
	private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
		SubscribeReqProto.SubscribeReq.Builder build = SubscribeReqProto.SubscribeReq.newBuilder();
		build.setSubReqID(1);
		build.setUserName("cys");
		build.setProductName("Netty book");
//		List<String> address = new ArrayList<String>();
//		address.add("hangzhou");
//		address.add("wenzhou");
//		address.add("yueqing");
		build.setAddress("hangzhou");
		return build.build();
	}
	
	public static void main(String[] args) throws InvalidProtocolBufferException {
		SubscribeReqProto.SubscribeReq req = TestSubscribeReqProto.createSubscribeReq();
		System.out.println("before:" + req.toString());
		SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
		System.out.println("after:" + req2);
		System.out.println(req.equals(req2));
	}
}
