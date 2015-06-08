package otechniques.controller;

import otechniques.ClientPart;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.badlogic.gdx.Input.Keys;

public class ClientController {
	private GameWorld world;
	private GameNetworkClient client;
	private InputHandler inputHandler;

	public ClientController(GameWorld world, GameNetworkClient client, InputHandler inputHandler) {
		this.world = world;
		this.client = client;
		this.inputHandler = inputHandler;
	}

	//TODO usuwanie pakietow, jezeli sa nowsze danego typu
	public void updateGameState() {
		
		if(ClientPart.clientSidePrediction == true){
			applyRecentInput();
		}
		
		client.createInputPackets(inputHandler.getKeysPressed());
		
		while (client.getReceivedPackets().size() != 0){
			Packet packet = client.getReceivedPackets().remove();
			
			if(packet instanceof PlayerPositionPacket){
				calculatePlayerPosition((PlayerPositionPacket) packet);			
			}			
			
		}
	} 


	
	/**
	 * applies player's input instantly, not waiting for server acknowledgment
	 */
	private void applyRecentInput(){
		for (int key : inputHandler.getKeysPressed()) {
			if(key == Keys.W){
				world.getPlayer().y += 1;
			}
		}
	}
	
	private void calculatePlayerPosition(PlayerPositionPacket positionPacket){
		world.getPlayer().setPosition(positionPacket.x, positionPacket.y);
		
		if(ClientPart.serverReconciliation == true){
			for(int i = 0; i < client.getPendingInputPackets().size(); i++){		
				if(client.getPendingInputPackets().get(i).sequenceNumber <= positionPacket.sequenceNumber){
					client.getPendingInputPackets().remove(i);
				}
				else{
					InputPacket inputPacket = client.getPendingInputPackets().get(i);
					for (int key : inputPacket.keysClicked) {
						if(key == Keys.W){
							world.getPlayer().y += 1;
						}
					}
				}
			}
		}
		else{
			client.getPendingInputPackets().clear();
		}	
	}
	
	
}
