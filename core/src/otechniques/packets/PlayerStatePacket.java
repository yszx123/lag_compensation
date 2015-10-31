package otechniques.packets;

import com.badlogic.gdx.math.Vector2;

import javafx.css.PseudoClass;

public class PlayerStatePacket extends Packet {

	public Vector2 position;
	public float rotation;
	public long clientTimestamp;
	
	@SuppressWarnings("unused")
	private PlayerStatePacket() {}

	public PlayerStatePacket(int senderID, int playerId, long sequenceNumber, Vector2 position, float rotation) {
		super(senderID, playerId, sequenceNumber);
		this.position = position;
		this.rotation = rotation;
	}

}
