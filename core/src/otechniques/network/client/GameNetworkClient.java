package otechniques.network.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import otechniques.ClientPart;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

public class GameNetworkClient {
	private Client client;
	private LinkedList<Packet> packetsToSend;// queue simulating ping, after
												// sending the packet, it's
												// moved to pending packets
												// queue
	private LinkedList<InputPacket>pendingInputPackets; // packets waiting for acknowledgment from the
	// server
	private ConcurrentLinkedQueue<Packet> receivedPackets;
	private int ping = 200; // TODO przeniesc
	private long lastSequenceNumber;

	public GameNetworkClient() {
		client = new Client();
		packetsToSend = new LinkedList<>();
		pendingInputPackets = new LinkedList<>();
		receivedPackets = new ConcurrentLinkedQueue<>();

		Packet.registerClasses(client);
		client.addListener(new ClientPacketListener(receivedPackets));
		client.start();
		tryToConnect();
	}

	public void addPacket(Packet p) {
		packetsToSend.add(p);
	}

	public void sendPackets() {	//TODO zmiana na zegar
		long currentTime = System.currentTimeMillis();

		while (packetsToSend.size() != 0
				&& currentTime - packetsToSend.getFirst().timestamp >= ping) {
			
			Packet packet = packetsToSend.removeFirst();
			client.sendTCP(packet);
		}
	}

	/**
	 * @param ping
	 *            - latency in packets sending, in milliseconds
	 */
	public void setSendingPing(int ping) {
		this.ping = ping;
	}

	/**
	 * Creates new packets and inserts them to the queue of packets, waiting for
	 * being sent to the server, basing on list of currently pressed keys. Sent
	 * packets are inserted into list of pending packets, wait for
	 * acknowledgment from server.
	 * 
	 * @param keysPressed
	 *            - list of keys currently pressed
	 */
	public void createInputPackets(Integer[] keysPressed) {
		if (keysPressed.length != 0) {
			InputPacket packet = new InputPacket(ClientPart.CLIENT_ID,
					++lastSequenceNumber, keysPressed, Gdx.graphics
					.getDeltaTime());
			packetsToSend.add(packet);
			pendingInputPackets.add(packet);
		}
	}

	public LinkedList<InputPacket> getPendingInputPackets() {
		return pendingInputPackets;
	}

	public ConcurrentLinkedQueue<Packet> getReceivedPackets() {
		return receivedPackets;
	}

	private void tryToConnect() { // TODO parametryzacja
		try {
			client.connect(5000, "localhost", 54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
