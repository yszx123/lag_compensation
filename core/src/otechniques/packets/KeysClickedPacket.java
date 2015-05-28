package otechniques.packets;

public class KeysClickedPacket extends Packet {

	public Integer[] keysClicked;
	public float delta;
	
	@SuppressWarnings("unused")
	private KeysClickedPacket(){}
	
	public KeysClickedPacket(int senderID, long sequenceNumber, Integer[] keysPressed, float delta) {
		super(senderID, sequenceNumber);
		this.keysClicked = keysPressed;
		this.delta = delta;
	}

}
