package otechniques.network.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

import otechniques.Config;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;

public class GameNetworkClient {
	private int clientId;
	private Client client;
	private LinkedList<Packet> packetsToSend;// queue simulating ping, after
												// sending the packet, it's
												// moved to pending packets
												// queue
	private LinkedList<InputPacket>pendingInputPackets; // packets waiting for acknowledgment from the
	// server
	private ConcurrentLinkedQueue<Packet> receivedPackets;
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
				&& currentTime - packetsToSend.getFirst().timestamp >= Config.CLIENT_PING) {
			
			Packet packet = packetsToSend.removeFirst();
			client.sendTCP(packet);
		}
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
			InputPacket packet = new InputPacket(clientId,
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

	private void tryToConnect() {
		try {
			client.connect(Config.CLIENT_CONNECT_TIMEOUT, Config.SERVER_HOST, Config.TCP_PORT, Config.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setClientId(int clientId){
		this.clientId = clientId;
	}
	
}
