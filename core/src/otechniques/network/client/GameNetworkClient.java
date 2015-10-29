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
import otechniques.packets.MousePositionPacket;
import otechniques.packets.Packet;
import otechniques.render.Renderer;

public class GameNetworkClient {
	private final int clientId;
	private Client client;
	/**
	 * queue simulating ping
	 */
	private LinkedList<Packet> packetsToSend;
	private ConcurrentLinkedQueue<Packet> receivedPackets;
	private long lastSequenceNumber;

	public GameNetworkClient(int clientId) {
		this.clientId = clientId;

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
	 */
	public void createInputPackets(Set<Integer> keysPressed, Set<Integer> keysReleased) {
		Integer[] keysPressedArray = keysPressed.toArray(new Integer[keysPressed.size()]);
		Integer[] keysReleasedArray = keysReleased.toArray(new Integer[keysReleased.size()]);
		InputPacket packet = new InputPacket(clientId, clientId, ++lastSequenceNumber, keysPressedArray,
				keysReleasedArray, Gdx.graphics.getDeltaTime());
		
		packetsToSend.add(packet);
	}

	public void createMousePositionPackets() {
		Vector2 inWorldMousePos = Renderer.getInWorldMousePosition();
		MousePositionPacket p = new MousePositionPacket(clientId, clientId, lastSequenceNumber, inWorldMousePos);
		packetsToSend.add(p);
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

}
