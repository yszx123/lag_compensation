package otechniques.network.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;

import otechniques.Config;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
import otechniques.render.Renderer;

public class GameNetworkClient {
	private int clientId;
	private Client client;
	/**
	 * queue simulating ping
	 */
	private LinkedList<Packet> packetsToSend;
	private ConcurrentLinkedQueue<Packet> receivedPackets;
	private long lastSequenceNumber;

	public GameNetworkClient() {
		client = new Client();
		packetsToSend = new LinkedList<>();
		receivedPackets = new ConcurrentLinkedQueue<>();

		Packet.registerClasses(client);
		client.addListener(new ClientPacketListener(receivedPackets));
		client.start();
		tryToConnect();
	}

	public void addPacket(Packet p) {
		packetsToSend.add(p);
	}

	public void sendPackets() { // TODO zmiana na zegar
		long currentTime = System.currentTimeMillis();

		while (packetsToSend.size() != 0 && currentTime - packetsToSend.getFirst().timestamp >= Config.CLIENT_PING) {
			Packet packet = packetsToSend.removeFirst();
			client.sendUDP(packet);
		}
	}

	/**
	 * Creates new packets and inserts them to the queue of packets, waiting for
	 * being sent to the server, basing on list of currently pressed keys. Sent
	 * packets wait for acknowledgment from server.
	 * 
	 * @param keysPressed
	 *            - list of keys currently pressed
	 */
	public void createInputPackets(Set<Integer> keysPressed, Set<Integer> keysReleased) {
		Integer[] keysPressedArray = keysPressed.toArray(new Integer[keysPressed.size()]);
		Integer[] keysReleasedArray = keysReleased.toArray(new Integer[keysReleased.size()]);
		Vector2 inWorldMousePos = Renderer.getInWorldMousePosition();
		InputPacket packet = new InputPacket(clientId, ++lastSequenceNumber, keysPressedArray, keysReleasedArray,
				inWorldMousePos, Gdx.graphics.getDeltaTime());
		packetsToSend.add(packet);
	}

	public ConcurrentLinkedQueue<Packet> getReceivedPackets() {
		return receivedPackets;
	}

	private void tryToConnect() {
		try {
			client.connect(Config.CLIENT_CONNECT_TIMEOUT, Config.SERVER_HOST, Config.TCP_PORT, Config.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public long getLastSequenceNumber() {
		return lastSequenceNumber;
	}

}
