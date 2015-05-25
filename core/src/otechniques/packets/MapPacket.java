package otechniques.packets;

public class MapPacket extends Packet {
	public final byte[] streamedMap;
	

	public MapPacket(){
		this.streamedMap = null;
	}
	
	public MapPacket(byte[] tiledMap){
		this.streamedMap = tiledMap;
	}
}
