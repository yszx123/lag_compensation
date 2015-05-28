package otechniques.network;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import otechniques.ClientPart;
import otechniques.packets.KeysClickedPacket;
import otechniques.packets.Packet;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

public class GameNetworkClient {
	private Client client;
	private LinkedBlockingDeque<Packet> packetQueue;
	private int ping = 200; // TODO przeniesc
	private long lastSequenceNumber;

	public GameNetworkClient() {
		client = new Client();
		Packet.registerClasses(client);

		packetQueue = new LinkedBlockingDeque<Packet>();

		client.addListener(new ClientPacketListener(packetQueue));
		client.start();
		tryToConnect();
	}

	public void addPacket(Packet p) {
		packetQueue.add(p);
	}

	public void sendPackets() { // TODO poprawic te metode na lepsza wydajnosc
		for (Packet packet : packetQueue) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - packet.timestamp >= ping) {
				client.sendTCP(packet);
				packetQueue.remove(packet);
			}
		}
	}

	/**
	 * @param ping
	 *            - latency in packets sending, in milliseconds
	 */
	public void setSendingPing(int ping) {
		this.ping = ping;
	}

	private void tryToConnect() {
		try {
			client.connect(5000, "localhost", 54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates new packets and sends them to server, basing on list of currently
	 * pressed keys. Sent packets are inserted into list of pending packets,
	 * wait for acknowledgment from server.
	 * 
	 * @param keysPressed
	 *            - list of keys currently pressed
	 */
	public void createInputPackets(Integer[] keysPressed) {
		if(keysPressed.length !=0){
			packetQueue.add(new KeysClickedPacket(ClientPart.CLIENT_ID,
					++lastSequenceNumber, keysPressed, Gdx.graphics.getDeltaTime()));
		}
	}

}
