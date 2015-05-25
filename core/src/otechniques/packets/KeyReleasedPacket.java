package otechniques.packets;

import otechniques.packets.Packet.InputPacket;

public class KeyReleasedPacket extends Packet implements InputPacket{
	
	public int key;
	
	public KeyReleasedPacket(){}
	public KeyReleasedPacket(int key) {
		this.key = key;
	}

	
}
 