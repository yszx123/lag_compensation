package otechniques.network.server;

import java.io.IOException;

import com.esotericsoftware.kryonet.Listener.LagListener;
import com.esotericsoftware.kryonet.Server;

import otechniques.Config;
import otechniques.network.ControlPacketListener;
import otechniques.network.PacketManager;
import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.Packet;

/**
 * Receives packets from clients, and then process them. Received packet are put
 * to corresponding deque by listener, and processed in the next timestep.
 * Acknowledgments of received packets and updates of server are put to another
 * queue with timestamp. However, they are sent after some time, depending on
 * ping parameter.
 *
 */
public class GameNetworkServer extends PacketManager {

	private final Server server = new Server();

	public GameNetworkServer() {
		Packet.registerClasses(server);

		server.start();
		server.addListener(new ControlPacketListener(receivedControlPackets));
		server.addListener(
				new LagListener(Config.SERVER_PING, Config.SERVER_PING, new ServerPacketListener(receivedPackets)));
		tryToBind();
	}

	public void sendPackets() {

		ControlPacket cp;
		while ((cp = controlPacketSendingQueue.poll()) != null) {
			server.sendToAllTCP(cp);
		}

		Packet p;
		while ((p = packetSendingQueue.poll()) != null) {
			server.sendToAllUDP(p);
		}

	}

	public void dispose() {
		try {
			server.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tryToBind() {
		try {
			server.bind(Config.TCP_PORT, Config.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
