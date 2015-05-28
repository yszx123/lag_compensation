package otechniques.packets;

public class PlayerPositionPacket extends Packet {

	float x, y;
	
	@SuppressWarnings("unused")
	private PlayerPositionPacket() {}

	public PlayerPositionPacket(int senderID, long sequenceNumber) {
		super(senderID, sequenceNumber);
	}

}
