package otechniques.network.client;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import otechniques.network.packets.Packet;
import otechniques.network.packets.PlayerStatePacket;

public class ClientPacketListener extends Listener {

	private final ConcurrentLinkedQueue<Packet> receivedPackets;

	public ClientPacketListener(ConcurrentLinkedQueue<Packet> receivedPackets) {
		super();
		this.receivedPackets = receivedPackets;

	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof PlayerStatePacket) {
			PlayerStatePacket p = (PlayerStatePacket) object;
			receivedPackets.add(p);
		}
	}
}
