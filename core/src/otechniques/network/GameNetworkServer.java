package otechniques.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import otechniques.packets.Packet;

import com.esotericsoftware.kryonet.Server;

/**
 * Receives packets from clients, and then process them. Received packet are put
 * to corresponding deque by listener, and instantly processed. Acknowledgments
 * of received packets and updates of server are put to another queue with
 * timestamp. However, they are sent after some time, depending on ping parameter.
 *
 */
public class GameNetworkServer {
	Server server;
	
	private int ping = 200;
	
	private LinkedBlockingDeque<Packet> receivedPackets;
	private LinkedBlockingDeque<Packet> packetQueue;
	private final int PACKET_BUFFER_SIZE = 10;	//TODO przeniesc gdzies
	
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
			if (currentTime - packet.timestamp >= ping) {
				server.sendToAllTCP(packet);
				packetQueue.remove(packet);
			}
		}
	}

	public void addPacket(Packet p) {
		packetQueue.add(p);
	}
	
	public ArrayList<Packet> getUnprocessedPackets(){ 
		Packet packet;
		ArrayList<Packet> unprocessedPackets = new ArrayList<>(receivedPackets.size() + PACKET_BUFFER_SIZE);	//its assumed, that some (possibly not more than buffer size) packets may arrive during copying
		while( (packet = receivedPackets.poll()) != null){
			unprocessedPackets.add(packet);
		}
		
		return unprocessedPackets;
	}
	
	public void setPing(int ping){
		this.ping = ping;	
	}
	
	private void tryToBind(){
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
