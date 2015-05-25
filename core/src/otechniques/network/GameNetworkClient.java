package otechniques.network;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GameNetworkClient {
	private Client client;
	private LinkedBlockingDeque<Packet> packetQueue;
	private int ping = 200;
	private ArrayBlockingQueue<Packet> receivedPackets;
	
	
	public GameNetworkClient(ArrayBlockingQueue<Packet> receivedPackets){
		this.receivedPackets = receivedPackets;
		
		client = new Client();
		Packet.registerClasses(client);
		packetQueue = new LinkedBlockingDeque<Packet>();
		
		client.start();
		client.addListener(new Listener(){
			@Override
			public void received (Connection connection, Object object) {
	        	if (object instanceof PlayerPositionPacket){
	        		PlayerPositionPacket p = (PlayerPositionPacket)object;
	        		receivedPackets.add(p);
	        	}
	        }
		});
		
		try {
			client.connect(5000, "localhost", 54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Client getKryoClient(){
		return client;
	}
	
	public void addPacket(Packet p){
		packetQueue.add(p);
	}
	
	public void sendPackets(){	//TODO poprawic te metode na lepsza wydajnosc
		for (Packet packet : packetQueue) {
			long currentTime = System.currentTimeMillis(); 
			if(currentTime - packet.timestamp >= ping){			
				client.sendTCP(packet);
				packetQueue.remove(packet);
			}
		}
	}
	
	/**
	 * @param ping - latency in sending packets, in milliseconds
	 */
	public void setSendingPing(int ping){
		this.ping = ping;
	}
}
