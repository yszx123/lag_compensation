package otechniques.controller;

import java.util.LinkedList;

import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.badlogic.gdx.Input.Keys;

public class ClientController {
	private GameWorld world;
	private GameNetworkClient client;

	public ClientController(GameWorld world, GameNetworkClient client) {
		this.world = world;
		this.client = client;
	}

	public void updateGameState() {
		
		for (Packet packet : client.getReceivedPackets()) {	//TODO usuwanie starych pakietow, jesli sa dostepne nowsze
			if (packet instanceof PlayerPositionPacket) {
				// set position of player arbitrally, basing on position packet.
				// Then reapply input from pending packets.
				PlayerPositionPacket positionPacket = (PlayerPositionPacket) packet;
				world.getPlayer().x = positionPacket.x;
				world.getPlayer().y = positionPacket.y;
				//delete acknowledged packets
				LinkedList<Packet> pendingInputPackets = client.getPendingInputPackets();
				for(int i=0; i < pendingInputPackets.size(); i++){
					if (pendingInputPackets.get(i) instanceof InputPacket ){
						if(pendingInputPackets.get(i).sequenceNumber <= packet.sequenceNumber){
							pendingInputPackets.remove(i);
						}
						else{
							break;
						}
					}
				}
				for (Packet packetToReapply : pendingInputPackets) {
					if (packetToReapply instanceof InputPacket ){
						InputPacket inputPacketToReapply = (InputPacket) packetToReapply;
						for (int key : ((InputPacket) packetToReapply).keysClicked) {
							if (key == Keys.W){
								world.getPlayer().y += 1;
							}
						}
					}
				}
				
			}
		}
	}
	
	
}
