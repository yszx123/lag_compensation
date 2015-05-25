package otechniques.packets;

import otechniques.packets.Packet.InputPacket;

public class MouseClickedPacket extends Packet implements InputPacket{
	public int button;
	
	public MouseClickedPacket(){}
	public MouseClickedPacket(int button) {
		this.button = button;
	}

}
