package otechniques.controllers;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import otechniques.input.InputSupplier;
import otechniques.network.ControlPacketObserver;
import otechniques.network.client.GameNetworkClient;
import otechniques.network.packets.ConfigurationControlPacket;
import otechniques.network.packets.Packet;
import otechniques.network.packets.PlayerStatePacket;
import otechniques.objects.GameWorld;
import otechniques.render.Renderer;
import otechniques.utils.DeltaMovement;

public final class ClientController extends CommonController implements ControlPacketObserver {
	private final int playerId;
	private final boolean isClientControllable;
	private boolean clientSidePrediction;
	private boolean serverReconciliation;
	private final GameNetworkClient client;
	private final InputSupplier inputSupplier;
	private final ArrayList<DeltaMovement> deltaMovements = new ArrayList<>();
	
	public ClientController(int playerId, boolean isClientControllable, GameWorld gameWorld, GameNetworkClient client,
			InputSupplier inputSupplier) {
		super(gameWorld);
		gameWorld.createWalls();
		//gameWorld.createTrash();
		this.playerId = playerId;
		this.isClientControllable = isClientControllable;
		this.client = client;
		this.inputSupplier = inputSupplier;
	}

	// TODO usuwanie pakietow, jezeli sa nowsze danego typu
	public void updateGameState(float timeStep) {

		client.createInputPacket(inputSupplier.getKeysPressed(), inputSupplier.getKeysReleased());
		if (isClientControllable) {
			client.createMousePositionPacket();
		}
		
		// iterate over all received packets to update gamestate accordingly to
		// server state
		while (client.getReceivedPackets().size() != 0) {
			Packet packet = client.getReceivedPackets().remove();
			if (packet instanceof PlayerStatePacket) {
				refreshPlayerState((PlayerStatePacket) packet);
			} // else if(...)

		}
		
		if (clientSidePrediction) {
			// movement
			appplyRecentMovementInput();
			
			// grenades
			if (inputSupplier.getKeysReleased().contains(Keys.G)) {
				throwGrenade(playerId);
			}
			// apply rotation only for controllable player
			if (isClientControllable) { 
				getPlayerBody(playerId).setTransform(getPlayerBody(playerId).getPosition(),
						calculateDesiredPlayerRotation(playerId, Renderer.getInWorldMousePosition()));
			}
		}
		
		updateCommonGameState(timeStep);
		
	}

	/**
	 * applies player's input instantly, basing on currently pressed keys, not
	 * waiting for server acknowledgment
	 */
	private void appplyRecentMovementInput() {
		getPlayerBody(playerId).setLinearVelocity(calculateMovementVector(inputSupplier.getKeysPressed()));
	}

	private void refreshPlayerState(PlayerStatePacket statePacket) {
		//System.out.print("last ack: " + statePacket.position.toString());
		getPlayerBody(statePacket.playerId).setTransform(statePacket.position, statePacket.rotation);
		
		if (serverReconciliation) {

			for (int i = 0; i < deltaMovements.size(); i++) {
				// removes all packets, which were already acknowledged and
				// applied to client
				Vector2 cummulatedDeltaMovement = new Vector2();
				//System.out.println("    cumulated: " +cummulatedDeltaMovement.toString());
				if (deltaMovements.get(i).sequenceNumber <= statePacket.sequenceNumber) {
					deltaMovements.remove(i);
				} else {
					DeltaMovement d = deltaMovements.get(i);
					cummulatedDeltaMovement.add(d.deltaMovement);
				}
				getPlayerBody(statePacket.playerId).setTransform(statePacket.position.add(cummulatedDeltaMovement),
						getPlayerBody(statePacket.playerId).getAngle());
			}
		} else {
			getPlayerBody(statePacket.playerId).setTransform(statePacket.position, statePacket.rotation);
			deltaMovements.clear();
		}
	}

	@Override
	protected void processPacket(ConfigurationControlPacket packet) {
		clientSidePrediction = packet.clientSidePrediction;
		serverReconciliation = packet.serverReconciliation;
	}
}
