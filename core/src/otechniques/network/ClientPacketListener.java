package otechniques.network;

import java.util.concurrent.LinkedBlockingDeque;

import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientPacketListener extends Listener{
	
	private LinkedBlockingDeque<Packet> receivedPackets;
	
	public  ClientPacketListener(LinkedBlockingDeque<Packet> receivedPackets) {
		super();
		this.receivedPackets = receivedPackets;
		
	}	
	
	@Override
	public void received (Connection connection, Object object) {
    	if (object instanceof PlayerPositionPacket){
    		PlayerPositionPacket p = (PlayerPositionPacket)object;
    		receivedPackets.add(p);
    	}
    }
}
