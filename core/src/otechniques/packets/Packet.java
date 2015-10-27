package otechniques.packets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public abstract class Packet {
	
	public final int senderID;
	public final long sequenceNumber;
	public final long timestamp;
	public int playerId;
	
	protected Packet(){
		sequenceNumber = -1;
		senderID = -1;
		timestamp = -1;
	}
	
	public Packet(int senderID, int playerId, long sequenceNumber){
		timestamp = System.currentTimeMillis();
		this.senderID = senderID;
		this.playerId = playerId;
		this.sequenceNumber = sequenceNumber;
	}
	
	
	public static void registerClasses(EndPoint endpoint){
		Kryo kryo = endpoint.getKryo();
		
		kryo.register(InputPacket.class);
		kryo.register(PlayerPositionPacket.class);
		kryo.register(MousePositionPacket.class);
		
		
		kryo.register(Integer[].class);
		kryo.register(Vector3.class);
		kryo.register(Vector2.class);
	}
	
}
