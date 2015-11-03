package otechniques.controllers;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;

import otechniques.Config;
import otechniques.input.InputSupplier;
import otechniques.network.client.GameNetworkClient;
import otechniques.network.packets.Packet;
import otechniques.network.packets.PlayerStatePacket;
import otechniques.objects.GameWorld;
import otechniques.render.Renderer;
import otechniques.utils.DeltaMovement;

public class ClientController extends CommonController {
	private final int playerId;
	private final boolean isClientControllable;
	private final GameNetworkClient client;
	private final InputSupplier inputSupplier;
	private final ArrayList<DeltaMovement> deltaMovements = new ArrayList<>();

	public ClientController(int playerId, boolean isClientControllable, GameWorld gameWorld, GameNetworkClient client,
			InputSupplier inputSupplier) {
		super(gameWorld);
		gameWorld.createWalls();
		gameWorld.createTrash();
		this.playerId = playerId;
		this.isClientControllable = isClientControllable;
		this.client = client;
		this.inputSupplier = inputSupplier;
	}

	// TODO usuwanie pakietow, jezeli sa nowsze danego typu
	public void updateGameState(float timeStep) {

		// iterate over all received packets to update gamestate accordingly to
		// server state
		while (client.getReceivedPackets().size() != 0) {
			Packet packet = client.getReceivedPackets().remove();
			if (packet instanceof PlayerStatePacket) {
				refreshPlayerState((PlayerStatePacket) packet);
			} // else if(...)

		}
		
		client.createInputPacket(inputSupplier.getKeysPressed(), inputSupplier.getKeysReleased());
		if(isClientControllable){			
			client.createMousePositionPacket();
		}

		if (Config.CLIENT_SIDE_PREDICTION) {
			// movement
			appplyRecentMovementInput();
			DeltaMovement d = new DeltaMovement();
			d.deltaMovement = calculateMovementVector(inputSupplier.getKeysPressed()).scl(Config.PHYSICS_TIMESTEP);
			d.sequenceNumber = client.getLastSequenceNumber();
			deltaMovements.add(d);

			// grenades
			if (inputSupplier.getKeysReleased().contains(Keys.G)) {
				throwGrenade(playerId);
			}
			
			
			if (isClientControllable) { // apply rotation only for controllable player
				getPlayerBody(playerId).setTransform(getPlayerBody(playerId).getPosition(),
						calculateDesiredPlayerRotation(playerId, Renderer.getInWorldMousePosition()));
			}

			updateCommonGameState(timeStep);
		}
	}

	/**
	 * applies player's input instantly, basing on currently pressed keys, not
	 * waiting for server acknowledgment
	 */
	private void appplyRecentMovementInput() {
		getPlayerBody(playerId).setLinearVelocity(calculateMovementVector(inputSupplier.getKeysPressed()));
	}

	private void refreshPlayerState(PlayerStatePacket statePacket) {
		getPlayerBody(statePacket.playerId).setTransform(statePacket.position, statePacket.rotation);
		
		if (Config.SERVER_RECONCILIATION) {

			for (int i = 0; i < deltaMovements.size(); i++) {
				// removes all packets, which were already acknowledged and
				// applied to client
				if (deltaMovements.get(i).sequenceNumber <= statePacket.sequenceNumber) {
					deltaMovements.remove(i);
				} else {
					DeltaMovement d = deltaMovements.get(i);
					getPlayerBody(playerId).setTransform(getPlayerBody(playerId).getPosition().add(d.deltaMovement),
							getPlayerBody(playerId).getAngle());
				}
			}
		} else {
			deltaMovements.clear();
		}
	}	
}
