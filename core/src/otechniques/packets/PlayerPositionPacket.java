package otechniques.packets;

public class PlayerPositionPacket extends Packet {
	public int x, y;
	
	public PlayerPositionPacket(){}
	
	public PlayerPositionPacket(int x, int y){
		super();
		this.x = x;
		this.y = y;
	}
}
