package otechniques.controller;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import otechniques.Config;
import otechniques.input.InputHandler;
import otechniques.input.InputSupplier;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.objects.Trash;
import otechniques.packets.Packet;
import otechniques.packets.PlayerStatePacket;
import otechniques.render.Renderer;
import otechniques.utils.DeltaMovement;

public class ClientController extends CommonController {
	private final int playerId;
	private final boolean isClientControllable;
	private final GameNetworkClient client;
	private final InputSupplier inputSupplier;
	private final ArrayList<DeltaMovement> deltaMovements;
	private final ArrayList<Trash> trashObjects;

	public ClientController(int playerId, boolean isClientControllable, GameWorld gameWorld, GameNetworkClient client,
			InputSupplier inputSupplier) {
		super(gameWorld);
		this.playerId = playerId;
		this.isClientControllable = isClientControllable;
		this.client = client;
		this.inputSupplier = inputSupplier;
		deltaMovements = new ArrayList<>();
		trashObjects = new ArrayList<>();
		createTrash();
	}

	// TODO usuwanie pakietow, jezeli sa nowsze danego typu
	public void updateGameState(float timeStep) {

		// iterate over all received packets to update gamestate accordingly to
		// server state
		while (client.getReceivedPackets().size() != 0) {
			Packet packet = client.getReceivedPackets().remove();
			if (packet instanceof PlayerStatePacket) { //TODO w ogole takiego pakietu nie powinien otrzymac
				refreshPlayerState((PlayerStatePacket) packet);
			} // else if(...)

		}
		
		
		client.createInputPackets(inputSupplier.getKeysPressed(), inputSupplier.getKeysReleased());
		if(isClientControllable){			
			client.createMousePositionPackets();
		}

		for (Trash trash : trashObjects) {
			trash.act(timeStep);
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

	private void createTrash() { // TODO magic numbers
		for (int i = 0; i < 10; i++) {
			float x = MathUtils.random(1, 19);
			float y = MathUtils.random(1, 19);
			Trash trash = new Trash(gameWorld.getWorld(), new Vector2(x, y));
			trashObjects.add(trash);
		}
	}
	
	
}
