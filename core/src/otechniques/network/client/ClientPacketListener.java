package otechniques.network.client;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import otechniques.packets.Packet;
import otechniques.packets.PlayerStatePacket;

public class ClientPacketListener extends Listener {

	private ConcurrentLinkedQueue<Packet> receivedPackets;

	public ClientPacketListener(ConcurrentLinkedQueue<Packet> receivedPackets2) {
		super();
		this.receivedPackets = receivedPackets2;

	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof PlayerStatePacket) {
			PlayerStatePacket p = (PlayerStatePacket) object;
			receivedPackets.add(p);
		}
	}
}
