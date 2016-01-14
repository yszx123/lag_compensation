package otechniques.network.client;

import java.io.IOException;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

import otechniques.config.Config;
import otechniques.network.ControlPacketListener;
import otechniques.network.ControlPacketObserver;
import otechniques.network.PacketManager;
import otechniques.network.SelectiveLagListener;
import otechniques.network.packets.ConfigurationControlPacket;
import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.InputPacket;
import otechniques.network.packets.MousePositionPacket;
import otechniques.network.packets.Packet;

public class GameNetworkClient extends PacketManager implements ControlPacketObserver {

	private int clientId;
	private final Client client = new Client();
	private Listener currentLagListener;

	private long lastSequenceNumber;

	public GameNetworkClient() {
		Packet.registerClasses(client);

		ControlPacketListener controlPacketListener = new ControlPacketListener(receivedControlPackets);
		client.addListener(controlPacketListener);
		client.start();
		tryToConnect();
		while (controlPacketListener.getLastConnectionId() == -1) {
			; // busy waiting for client to connect
		}
		clientId = controlPacketListener.getLastConnectionId();
	}

	public void sendPackets() { // TODO zmiana na zegar

		ControlPacket cp;
		while ((cp = controlPacketSendingQueue.poll()) != null) {
			client.sendTCP(cp);
		}

		Packet p;
		while ((p = packetSendingQueue.poll()) != null) {
			client.sendUDP(p);
		}
	}

	/**
	 * Creates new packets and inserts them to the queue of packets, waiting for
	 * being sent to the server, basing on list of currently pressed keys.
	 */
	public void createInputPacket(Set<Integer> keysPressed, Set<Integer> keysReleased) {
		Integer[] keysPressedArray = keysPressed.toArray(new Integer[keysPressed.size()]);
		Integer[] keysReleasedArray = keysReleased.toArray(new Integer[keysReleased.size()]);
		InputPacket packet = new InputPacket(clientId, clientId, ++lastSequenceNumber, keysPressedArray,
				keysReleasedArray, Gdx.graphics.getDeltaTime());
		packetSendingQueue.add(packet);
	}

	public void createMousePositionPacket(Vector2 inWorldMousePos) {
		MousePositionPacket p = new MousePositionPacket(clientId, clientId, ++lastSequenceNumber, inWorldMousePos);
		packetSendingQueue.add(p);
	}

	public long getLastSequenceNumber() {
		return lastSequenceNumber;
	}

	public void dispose() {
		try {
			client.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getClientId() {
		return clientId;
	}

	private void tryToConnect() {
		try {
			client.connect(Config.CLIENT_CONNECT_TIMEOUT, Config.SERVER_HOST, Config.TCP_PORT, Config.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processPacket(ControlPacket packet) {
		if (packet instanceof ConfigurationControlPacket) {
			ConfigurationControlPacket p = (ConfigurationControlPacket) packet;
			if (currentLagListener != null) {
				client.removeListener(currentLagListener);
			}
			currentLagListener = new SelectiveLagListener(p.minPing, p.maxPing, p.packetLossRate,
					new ClientPacketListener(receivedPackets));
			client.addListener(currentLagListener);
		}

	}
}
