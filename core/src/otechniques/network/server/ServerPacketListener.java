package otechniques.network.server;

import java.util.concurrent.LinkedBlockingDeque;

import otechniques.packets.Packet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerPacketListener extends Listener {
	private LinkedBlockingDeque<Packet> receivedPackets;
	
	public ServerPacketListener(LinkedBlockingDeque<Packet> receivedPackets) {
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
