package otechniques.packets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Packet {
	
	public int senderID;
	public final long timestamp;
	
	public Packet(){
		timestamp = System.currentTimeMillis();
	}
	
	public static void registerClasses(EndPoint endpoint){
		Kryo kryo = endpoint.getKryo();
		kryo.register(MapPacket.class);
		
		kryo.register(KeyPressedPacket.class);
		kryo.register(KeyReleasedPacket.class);
		kryo.register(MousePositionPacket.class);
		kryo.register(MouseClickedPacket.class);
		kryo.register(MouseReleasedPacket.class);
		
		kryo.register(CameraPositionPacket.class);
		
		kryo.register(PlayerPositionPacket.class);
		
		kryo.register(byte[].class);
		kryo.register(Vector3.class);
		kryo.register(Vector2.class);
		kryo.register(InfoPacket.class);
	}
	
	public interface InputPacket{};
}
