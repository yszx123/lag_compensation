package otechniques.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.ClientPart;
import otechniques.Config;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.PlayerPositionPacket;

public class ClientController {
	private GameWorld gameWorld;
	private GameNetworkClient client;
	private InputHandler inputHandler;

	public ClientController(GameWorld world, GameNetworkClient client, InputHandler inputHandler) {
		this.gameWorld = world;
		this.client = client;
		this.inputHandler = inputHandler;
	}

	//TODO usuwanie pakietow, jezeli sa nowsze danego typu
	public void updateGameState(float timeStep) {
		applyRecentInput();
		gameWorld.getWorld().step(timeStep, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);
		//gameWorld.getPlayer().body.applyLinearImpulse(new Vector2(10,10), getPlayerBody().getPosition(), true);
		
		
		
		
		
		
		
/*		tak bylo przed zmianami na sztywno
 * 		if(ClientPart.clientSidePrediction == true){
			applyRecentInput();
		}
		
		client.createInputPackets(inputHandler.getKeysPressed());
		
		while (client.getReceivedPackets().size() != 0){
			Packet packet = client.getReceivedPackets().remove();
			
			if(packet instanceof PlayerPositionPacket){
				calculatePlayerPosition((PlayerPositionPacket) packet);			
			}			
			
		}*/
		
	} 


	
	/**
	 * applies player's input instantly, not waiting for server acknowledgment
	 */
	private void applyRecentInput(){
		Vector2 playerMovement = new Vector2();
		
		for (int key : inputHandler.getKeysPressed()) {
			if(key == Keys.W){
				playerMovement.add(new Vector2(0, Config.PLAYER_SPEED));
			}
			else if (key == Keys.S){
				playerMovement.add(new Vector2(0,-Config.PLAYER_SPEED));
			}
			else if (key == Keys.A){
				playerMovement.add(new Vector2(-Config.PLAYER_SPEED,0));
			}
			else if (key == Keys.D){
				playerMovement.add(new Vector2(Config.PLAYER_SPEED,0));
			}			
		}
				
		getPlayerBody().setLinearVelocity(playerMovement);	
		
	}
	
	private void calculatePlayerPosition(PlayerPositionPacket positionPacket){
		gameWorld.getPlayer().setPosition(positionPacket.x, positionPacket.y);
		
		if(ClientPart.serverReconciliation == true){
			for(int i = 0; i < client.getPendingInputPackets().size(); i++){		
				if(client.getPendingInputPackets().get(i).sequenceNumber <= positionPacket.sequenceNumber){
					client.getPendingInputPackets().remove(i);
				}
				else{
					InputPacket inputPacket = client.getPendingInputPackets().get(i);
					for (int key : inputPacket.keysClicked) {
						if(key == Keys.W){
							gameWorld.getPlayer().y += 1;
						}
					}
				}
			}
		}
		else{
			client.getPendingInputPackets().clear();
		}	
	}
	
	private Body getPlayerBody(){
		return gameWorld.getPlayer().body;
	}
	
	
	
}
