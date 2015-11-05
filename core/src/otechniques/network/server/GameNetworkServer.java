package otechniques.network.server;

import java.io.IOException;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import otechniques.config.Config;
import otechniques.network.ControlPacketListener;
import otechniques.network.ControlPacketObserver;
import otechniques.network.PacketManager;
import otechniques.network.SelectiveLagListener;
import otechniques.network.packets.ConfigurationControlPacket;
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
public class GameNetworkServer extends PacketManager implements ControlPacketObserver {

	private final Server server = new Server();
	private Listener currentLagListener;

	public GameNetworkServer() {
		Packet.registerClasses(server);

		server.start();
		server.addListener(new ControlPacketListener(receivedControlPackets));
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

	public void processPacket(ControlPacket packet) {
		if (packet instanceof ConfigurationControlPacket) {
			ConfigurationControlPacket p = (ConfigurationControlPacket) packet;
			if (currentLagListener != null) {
				server.removeListener(currentLagListener);
			}
			currentLagListener = new SelectiveLagListener(p.minPing, p.maxPing, p.packetLossRate,
					new ServerPacketListener(receivedPackets));
			server.addListener(currentLagListener);

			controlPacketSendingQueue.add(p);
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
