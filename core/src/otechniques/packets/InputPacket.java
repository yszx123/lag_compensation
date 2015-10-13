package otechniques.packets;

public class InputPacket extends Packet {

	public Integer[] keysClicked;
	public float delta;
	
	@SuppressWarnings("unused")
	private InputPacket(){}
	
	public InputPacket(int senderID, long sequenceNumber, Integer[] keysPressed, float delta) {
		super(senderID, sequenceNumber);
		this.keysClicked = keysPressed;
		this.delta = delta;
	}

}
