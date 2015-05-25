package otechniques.packets;

import com.badlogic.gdx.math.Vector2;

public class CameraPositionPacket extends Packet {

	public Vector2 position;
	
	
	public CameraPositionPacket(){}
	public CameraPositionPacket(Vector2 position) {
		this.position = position;
	}

}
