package otechniques.controller;

import java.util.ArrayList;
import java.util.HashMap;

import otechniques.ServerPart;
import otechniques.network.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.packets.KeysClickedPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.badlogic.gdx.Input.Keys;

public class ServerController {
	private final GameWorld world;
	private final GameNetworkServer server;
	private final HashMap<Integer, Boolean> keysClicked;
	
	
	public ServerController(GameWorld world, GameNetworkServer server){
		this.world = world;
		this.server = server;
		
		keysClicked = new HashMap<Integer, Boolean>();
	}
	
	public void processReceivedPackets(ArrayList<Packet> receivedPackets){
		
		for (Packet packet : receivedPackets) {
			if(packet instanceof KeysClickedPacket){
				updateClickedKeys((KeysClickedPacket) packet);
			}
		}
	}
	
	private void updateClickedKeys(KeysClickedPacket packet){	//TODO zrobic timestep zamiast updatu w kazdje kaltce
		for (int key : packet.keysClicked) {
			if(key == Keys.W){
				world.getPlayer().y += 1;
				server.addPacket(new PlayerPositionPacket(ServerPart.SERVER_ID, packet.sequenceNumber));
			}
		}
	}
}
