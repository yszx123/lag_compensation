package otechniques.packets;

public class PlayerPositionPacket extends Packet {

	public float x, y;
	
	@SuppressWarnings("unused")
	private PlayerPositionPacket() {}

	public PlayerPositionPacket(int senderID, long sequenceNumber) {
		super(senderID, sequenceNumber);
	}

}
