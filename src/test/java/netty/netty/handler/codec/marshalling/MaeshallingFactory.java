package netty.netty.handler.codec.marshalling;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

public class MaeshallingFactory {
	
	public static MarshallingDecoder decode(){
		final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration config = new MarshallingConfiguration();
		config.setVersion(5);
		DefaultUnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, config);
		return new MarshallingDecoder(provider, 2048);
	}
	
	public static MarshallingEncoder encode(){
		final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration config = new MarshallingConfiguration();
		config.setVersion(5);
		DefaultMarshallerProvider provider = new DefaultMarshallerProvider(factory, config);
		return new MarshallingEncoder(provider);
	}
}
