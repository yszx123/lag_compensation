package otechniques.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.Config;
import otechniques.ObjectsConfig;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.objects.Grenade;
import otechniques.objects.Trash;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;
import otechniques.utils.DeltaMovement;

public class ClientController {
	private GameWorld gameWorld;
	private GameNetworkClient client;
	private InputHandler inputHandler;
	private ArrayList<DeltaMovement> deltaMovements;
	private ArrayList<Grenade> objects;
	private ArrayList<Trash> trashObjects;

	public ClientController(GameWorld world, GameNetworkClient client, InputHandler inputHandler) {
		this.gameWorld = world;
		this.client = client;
		this.inputHandler = inputHandler;
		deltaMovements = new ArrayList<>();
		objects = new ArrayList<>();
		trashObjects = new ArrayList<>();
		createTrash();
	}

	// TODO usuwanie pakietow, jezeli sa nowsze danego typu
	public void updateGameState(float timeStep) {

		// iterate over all received packets to update gamestate accordingly to
		// server state
		while (client.getReceivedPackets().size() != 0) {
			Packet packet = client.getReceivedPackets().remove();
			if (packet instanceof PlayerPositionPacket) {
				calculatePlayerPosition((PlayerPositionPacket) packet);
			} // else if(...)

		}

		client.createInputPackets(inputHandler.getKeysPressed());
		
		for (Trash trash : trashObjects) {
			trash.act(timeStep);
		}
		
		if (Config.CLIENT_SIDE_PREDICTION) {
			//movement
			appplyRecentMovementInput();
			DeltaMovement d = new DeltaMovement();
			d.deltaMovement = calculateMovementVector(inputHandler.getKeysPressed()).scl(Config.CLIENT_PHYSICIS_TIMESTEP);
			d.sequenceNumber = client.getLastSequenceNumber();
			deltaMovements.add(d);
			
			//grenades
			if(inputHandler.getKeysReleased().contains(Keys.G)){
				//Grenade g = new Grenade(gameWorld.getWorld(), gameWorld.getPlayer().getPosition(), true);
				Grenade g = new Grenade(gameWorld.getWorld(), gameWorld.getPlayer().body, true);
				objects.add(g);
				g.throwGrenade(new Vector2(10,10));
			}
		}
		
		for (Iterator<Grenade> iterator = objects.iterator(); iterator.hasNext();) {
			Grenade g = iterator.next();
			if(!g.isAlive()){
				iterator.remove();
			}else{
				g.act(timeStep);
			}
		}
			
		
		gameWorld.getWorld().step(timeStep, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);
		inputHandler.refresh();	//TODO clears released keys
	}

	/**
	 * applies player's input instantly, basing on currently pressed keys, not
	 * waiting for server acknowledgment
	 */
	private void appplyRecentMovementInput() {
		getPlayerBody().setLinearVelocity(calculateMovementVector(inputHandler.getKeysPressed()));
	}

	private void calculatePlayerPosition(PlayerPositionPacket positionPacket) {
		gameWorld.getPlayer().setPosition(positionPacket.x, positionPacket.y);
		
		if (Config.SERVER_RECONCILIATION) {
			
			for (int i = 0; i < deltaMovements.size(); i++) {
				// removes all packets, which were already acknowledged and
				// applied to client
				if (deltaMovements.get(i).sequenceNumber <= positionPacket.sequenceNumber) {
					deltaMovements.remove(i);
				} else {
					DeltaMovement d = deltaMovements.get(i);
					getPlayerBody().setTransform(getPlayerBody().getPosition().add(d.deltaMovement),
							getPlayerBody().getAngle());
				}
			}
		} else {
			deltaMovements.clear();
		}
	}

	private Body getPlayerBody() {
		return gameWorld.getPlayer().body;
	}
	
	/**
	 * @return vector representing player's movement direction
	 */
	private Vector2 calculateMovementVector(Set<Integer> keysPressed) {
		Vector2 playerMovement = new Vector2();
		
		if(keysPressed.contains(Keys.W)){
			playerMovement.add(new Vector2(0, ObjectsConfig.PLAYER_SPEED));
		}
		if(keysPressed.contains(Keys.S)){
			playerMovement.add(new Vector2(0, -ObjectsConfig.PLAYER_SPEED));
		}
		if(keysPressed.contains(Keys.A)){
			playerMovement.add(new Vector2(-ObjectsConfig.PLAYER_SPEED, 0));
		}
		if(keysPressed.contains(Keys.D)){
			playerMovement.add(new Vector2(ObjectsConfig.PLAYER_SPEED, 0));
		}
		
		return playerMovement;
	}

	private void createTrash(){
		Random random = new Random();
		for(int i=0; i<10; i++){
			float x = (random.nextFloat() * 19) - 9;
			float y = (random.nextFloat() * 19) - 9;
			Trash trash = new Trash(gameWorld.getWorld(), new Vector2(x, y)); 
			trashObjects.add(trash);
		}
	}
}
