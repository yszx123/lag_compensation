package otechniques.packets;

import com.badlogic.gdx.math.Vector2;

public class InputPacket extends Packet {

	public Integer[] keysPressed;
	public Integer[] keysReleased;
	public Vector2 inWorldMousePos;
	public float delta;
	
	@SuppressWarnings("unused")
	private InputPacket(){}
	
	public InputPacket(int senderID, long sequenceNumber, Integer[] keysPressed, Integer[] keysReleased, Vector2 inWorldMousePos, float delta) {
		super(senderID, sequenceNumber);
		this.keysPressed = keysPressed;
		this.keysReleased = keysReleased;
		this.inWorldMousePos = inWorldMousePos;
		this.delta = delta;
	}

}
