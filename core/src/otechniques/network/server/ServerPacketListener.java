package otechniques.network.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import otechniques.network.packets.Packet;

public class ServerPacketListener extends Listener {	
	
	private final ConcurrentLinkedQueue<Packet> receivedPackets;
	
	public ServerPacketListener(ConcurrentLinkedQueue<Packet> receivedPackets) {
		super();
		this.receivedPackets = receivedPackets;	
	}	
	
	@Override
	public void received (Connection connection, Object object) {
		if(object.getClass().getSuperclass() != Packet.class){	//poprawic, nie zadziala w rpzypadku dlugiej linii dziedziczenia
			return;
		}
		receivedPackets.add((Packet)object);
    }
}
