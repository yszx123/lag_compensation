package otechniques.packets;

public class InputPacket extends Packet {

	public Integer[] keysPressed;
	public Integer[] keysReleased;
	public float delta;
	
	@SuppressWarnings("unused")
	private InputPacket(){}
	

	public InputPacket(int senderID, int playerId, long sequenceNumber, Integer[] keysPressed, Integer[] keysReleased, float delta) {
		super(senderID, playerId, sequenceNumber);
		this.keysPressed = keysPressed;
		this.keysReleased = keysReleased;
		this.delta = delta;
	}

}
