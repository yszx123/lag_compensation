package otechniques.network;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import otechniques.packets.KeyPressedPacket;
import otechniques.packets.KeyReleasedPacket;
import otechniques.packets.Packet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameNetworkServer {
	Server server;
	private int ping = 200;
	private LinkedBlockingDeque<Packet> packetQueue;
	
	private Map<Integer, Boolean> keysClicked;
	
	public GameNetworkServer(Map<Integer, Boolean> keysClicked){
		packetQueue = new LinkedBlockingDeque<Packet>();
		this.keysClicked = keysClicked;
		
	    server = new Server();	    
	    Packet.registerClasses(server);
	    server.start();
	    
	    try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	        
	    server.addListener(new Listener() {
	        @Override
			public void received (Connection connection, Object object) {
	        	if(object instanceof KeyPressedPacket){
	        		KeyPressedPacket p = (KeyPressedPacket) object;
	        		keysClicked.put(p.key, true);
	        	}
	        	else if(object instanceof KeyReleasedPacket){
	        		KeyReleasedPacket p = (KeyReleasedPacket) object;
	        		keysClicked.put(p.key, false);
	        	}
	        }
	        
	     });
	}
	
	public void sendPackets(){	//TODO poprawic te metode na lepsza wydajnosc
		for (Packet packet : packetQueue) {
			long currentTime = System.currentTimeMillis(); 
			if(currentTime - packet.timestamp >= ping){			
				server.sendToAllTCP(packet);
				packetQueue.remove(packet);
			}
		}
	}
	
	public void addPacket(Packet p){
		packetQueue.add(p);
	}
	
}

