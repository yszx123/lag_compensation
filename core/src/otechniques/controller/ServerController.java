package otechniques.controller;

import java.util.ArrayList;

import otechniques.ServerPart;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.badlogic.gdx.Input.Keys;

public class ServerController {
	private final GameWorld world;
	private final GameNetworkServer server;

	
	public ServerController(GameWorld world, GameNetworkServer server){
		this.world = world;
		this.server = server;
	}
	
	public void processReceivedPackets(ArrayList<Packet> receivedPackets){
		
		for (Packet packet : receivedPackets) {
			if(packet instanceof InputPacket){
				updateClickedKeys((InputPacket) packet);
			}
		}
	}
	
	private void updateClickedKeys(InputPacket packet){	//TODO zrobic timestep zamiast updatu w kazdje kaltce
		for (int key : packet.keysClicked) {
			if(key == Keys.W){
				world.getPlayer().y += 1;
				server.addPacket(new PlayerPositionPacket(ServerPart.SERVER_ID, packet.sequenceNumber));
			}
		}
	}
}
