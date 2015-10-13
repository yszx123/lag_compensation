package otechniques.controller;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.Config;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;
import otechniques.utils.DeltaMovement;

public class ClientController {
	private GameWorld gameWorld;
	private GameNetworkClient client;
	private InputHandler inputHandler;
	private ArrayList<DeltaMovement> deltaMovements;

	public ClientController(GameWorld world, GameNetworkClient client, InputHandler inputHandler) {
		this.gameWorld = world;
		this.client = client;
		this.inputHandler = inputHandler;
		deltaMovements = new ArrayList<>();
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
		
		if (Config.CLIENT_SIDE_PREDICTION) {
			applyRecentInput();
			DeltaMovement d = new DeltaMovement();
			d.deltaMovement = calculateMovementVector(inputHandler.getKeysPressed()).scl(Config.CLIENT_PHYSICIS_TIMESTEP);
			d.sequenceNumber = client.getLastSequenceNumber();
			deltaMovements.add(d);
		}

		gameWorld.getWorld().step(timeStep, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);

	}

	/**
	 * applies player's input instantly, basing on currently pressed keys, not
	 * waiting for server acknowledgment
	 */
	private void applyRecentInput() {
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
	private Vector2 calculateMovementVector(Integer[] keysClicked) {
		Vector2 playerMovement = new Vector2();
		for (int key : keysClicked) {
			if (key == Keys.W) {
				playerMovement.add(new Vector2(0, Config.PLAYER_SPEED));
			} else if (key == Keys.S) {
				playerMovement.add(new Vector2(0, -Config.PLAYER_SPEED));
			} else if (key == Keys.A) {
				playerMovement.add(new Vector2(-Config.PLAYER_SPEED, 0));
			} else if (key == Keys.D) {
				playerMovement.add(new Vector2(Config.PLAYER_SPEED, 0));
			}
		}
		return playerMovement;
	}

}
