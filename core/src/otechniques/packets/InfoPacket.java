package otechniques.packets;

public class InfoPacket extends Packet{
	public String message;
	
	public InfoPacket(String message) {
		this.message = message;
	}

}