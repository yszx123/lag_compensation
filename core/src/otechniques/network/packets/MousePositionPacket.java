package otechniques.network.packets;

import com.badlogic.gdx.math.Vector2;

public class MousePositionPacket extends Packet {
	
	public Vector2 inWorldMousePos;
	
	@SuppressWarnings("unused")
	private MousePositionPacket() {};

	public MousePositionPacket(int senderID, int playerId, long sequenceNumber, Vector2 inWorldMousePos) {
		super(senderID, playerId, sequenceNumber);
		this.inWorldMousePos = inWorldMousePos;
	}
}
