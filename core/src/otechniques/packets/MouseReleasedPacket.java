package otechniques.packets;

import otechniques.packets.Packet.InputPacket;


public class MouseReleasedPacket extends Packet implements InputPacket{
	public int button;
	
	public MouseReleasedPacket(){}
	public MouseReleasedPacket(int button) {
		this.button = button; 
	}

}
