package otechniques.network;

import otechniques.network.packets.ControlPacket;

public interface ControlPacketObserver {
	
	public void processPacket(ControlPacket packet);
}
