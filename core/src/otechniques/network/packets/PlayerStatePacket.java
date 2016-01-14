package otechniques.network.packets;

import com.badlogic.gdx.math.Vector2;

public class PlayerStatePacket extends Packet {

	public Vector2 position;
	public float rotation;
	
	@SuppressWarnings("unused")
	private PlayerStatePacket() {}

	public PlayerStatePacket(int senderID, int playerId, long sequenceNumber, Vector2 position, float rotation) {
		super(senderID, playerId, sequenceNumber);
		this.position = position;
		this.rotation = rotation;
	}

}
