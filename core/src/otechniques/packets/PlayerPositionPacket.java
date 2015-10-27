package otechniques.packets;

public class PlayerPositionPacket extends Packet {

	public float x, y;
	public long clientTimestamp;
	
	@SuppressWarnings("unused")
	private PlayerPositionPacket() {}

	public PlayerPositionPacket(int senderID, int playerId, long sequenceNumber, float x, float y) {
		super(senderID, playerId, sequenceNumber);
		this.x = x;
		this.y = y;
	}

}
