package otechniques.network.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import com.esotericsoftware.kryonet.Server;

import otechniques.Config;
import otechniques.packets.Packet;

/**
 * Receives packets from clients, and then process them. Received packet are put
 * to corresponding deque by listener, and processed in the next timestep.
 * Acknowledgments of received packets and updates of server are put to another
 * queue with timestamp. However, they are sent after some time, depending on
 * ping parameter.
 *
 */
public class GameNetworkServer {
	Server server;

	private LinkedBlockingDeque<Packet> receivedPackets;
	private LinkedBlockingDeque<Packet> packetQueue;

	public GameNetworkServer() {
		receivedPackets = new LinkedBlockingDeque<>();
		server = new Server();
		packetQueue = new LinkedBlockingDeque<Packet>();

		Packet.registerClasses(server);
		server.start();
		server.addListener(new ServerPacketListener(receivedPackets));
		tryToBind();
	}

	public void sendPackets() { // TODO poprawic te metode na lepsza wydajnosc
		for (Packet packet : packetQueue) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - packet.timestamp >= Config.SERVER_PING) {
				server.sendToAllUDP(packet);
				packetQueue.remove(packet);
			}
		}
	}

	public void addPacket(Packet p) {
		packetQueue.add(p);
	}

	public ArrayList<Packet> getUnprocessedPackets() {
		Packet packet;
		ArrayList<Packet> unprocessedPackets = new ArrayList<>(
				// its assumed, that some (possibly not more than buffer size)
				// packets may arrive during copying
				receivedPackets.size() + Config.SERVER_PACKET_BUFFER_SIZE);
		
		while ((packet = receivedPackets.poll()) != null) {
			unprocessedPackets.add(packet);
		}

		return unprocessedPackets;
	}

	private void tryToBind() {
		try {
			server.bind(Config.TCP_PORT, Config.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		try {
			server.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
