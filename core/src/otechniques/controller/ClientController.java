package otechniques.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sun.jmx.snmp.Timestamp;

import otechniques.ClientPart;
import otechniques.Config;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
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
		client.createInputPackets(inputHandler.getKeysPressed(), getPlayerBody().getPosition());
		
		//iterate over all received packets to update gamestate accordingly to server state
		while (client.getReceivedPackets().size() != 0){
			Packet packet = client.getReceivedPackets().remove();
			
			if(packet instanceof PlayerPositionPacket){
				calculatePlayerPosition((PlayerPositionPacket) packet);			
			}//else if(...)			
			
		}
			
		if(Config.CLIENT_SIDE_PREDICTION){
			applyRecentInput();
		}
		
		
		gameWorld.getWorld().step(timeStep, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);	
		//System.out.println("klient: " +getPlayerBody().getPosition().toString()); TODO
	} 
	
	/**
	 * applies player's input instantly, basing on currently pressed keys, not waiting for server acknowledgment
	 */
	private void applyRecentInput(){				
		getPlayerBody().setLinearVelocity(calculateMovementVector(inputHandler.getKeysPressed()));		
	}
	
	/**
	 * @return vector representing player's movement direction
	 */
	private Vector2 calculateMovementVector(Integer[] keysClicked){
		Vector2 playerMovement = new Vector2();
		for (int key : keysClicked) {
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
		return playerMovement;
	}
	
	private void calculatePlayerPosition(PlayerPositionPacket positionPacket){
		gameWorld.getPlayer().setPosition(positionPacket.x, positionPacket.y);
		long acknowledgedPacketTimestamp = positionPacket.clientTimestamp;
		
		if(Config.SERVER_RECONCILIATION){			
			for(int i = 0; i < client.getPendingInputPackets().size(); i++){	
				//removes all packets, which were already acknowledged and applied to client
				if(client.getPendingInputPackets().get(i).sequenceNumber <= positionPacket.sequenceNumber){
					client.getPendingInputPackets().remove(i);
				}
				else{
					InputPacket inputPacket = client.getPendingInputPackets().get(i);
					gameWorld.getPlayer().setPosition(inputPacket.playerPosition);
					/*Vector2 playerMovementVector = calculateMovementVector(inputPacket.keysClicked);
					getPlayerBody().setLinearVelocity(playerMovementVector);
					long timespanBetweenPackets = inputPacket.timestamp - acknowledgedPacketTimestamp;
					float timespanInSeconds = timespanBetweenPackets / 1000f;
					System.out.println(timespanInSeconds);
					gameWorld.getWorld().step(timespanInSeconds, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS); //TODO nieforutunne miejsce na krok
*/				}
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
