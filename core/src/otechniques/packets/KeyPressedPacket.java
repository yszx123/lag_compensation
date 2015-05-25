package otechniques.packets;

import otechniques.packets.Packet.InputPacket;




public class KeyPressedPacket extends Packet implements InputPacket{	
	public int key;
	
	public KeyPressedPacket() {}
	
	public KeyPressedPacket(int key){
		this.key = key;
	}
	
}	
