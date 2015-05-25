package otechniques.packets;

import otechniques.packets.Packet.InputPacket;

import com.badlogic.gdx.math.Vector3;

public class MousePositionPacket extends Packet implements InputPacket{
	
	public Vector3 position;
	
	public MousePositionPacket() {}
	
	public MousePositionPacket(Vector3 position) {	
		this.position = position;
	}
}
