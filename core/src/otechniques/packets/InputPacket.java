package otechniques.packets;

import com.badlogic.gdx.math.Vector2;

public class InputPacket extends Packet {

	public Integer[] keysClicked;
	public float delta;
	public Vector2 playerPosition; //TODO tego nie powinno byc, roziwazanie do testu
	
	@SuppressWarnings("unused")
	private InputPacket(){}
	
	public InputPacket(int senderID, long sequenceNumber, Integer[] keysPressed, float delta) {
		super(senderID, sequenceNumber);
		this.keysClicked = keysPressed;
		this.delta = delta;
	}

}
