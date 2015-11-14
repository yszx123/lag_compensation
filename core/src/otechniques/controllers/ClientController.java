package otechniques.controllers;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Buttons;
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
		gameWorld.createTrash();
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

			// shooting
			if (inputSupplier.isButtonPressed(Buttons.LEFT)) {
				shootRay(playerId);
			}
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

		Vector2 oldPos = new Vector2(getPlayerBody(playerId).getPosition());
		updateCommonGameState(timeStep);
		Vector2 newPos = new Vector2(getPlayerBody(playerId).getPosition());
		DeltaMovement d = new DeltaMovement();
		d.deltaMovement = newPos.sub(oldPos);
		d.sequenceNumber = client.getLastSequenceNumber();
		deltaMovements.add(d);
	}

	/**
	 * applies player's input instantly, basing on currently pressed keys, not
	 * waiting for server acknowledgment
	 */
	private void appplyRecentMovementInput() {
		getPlayerBody(playerId).setLinearVelocity(calculateMovementVector(inputSupplier.getKeysPressed()));
	}

	private void refreshPlayerState(PlayerStatePacket statePacket) {
		//TODO cos zamula przy ustawianiu rotacji, dlatego ustawia ja tylko botom
		getPlayerBody(statePacket.playerId).setTransform(statePacket.position,
				statePacket.playerId == playerId && isClientControllable ? getPlayer(playerId).getAngle() : statePacket.rotation);

		if (statePacket.playerId != playerId) {
			return; // below techniques can be only applied to controlled player
		}

		if (serverReconciliation) {
			Vector2 cummulatedDeltaMovement = new Vector2();

			for (int i = 0; i < deltaMovements.size(); i++) {
				// removes all packets, which were already acknowledged and
				// applied to client
				if (deltaMovements.get(i).sequenceNumber <= statePacket.sequenceNumber) {
					deltaMovements.remove(i);
				} else {
					DeltaMovement d = deltaMovements.get(i);
					cummulatedDeltaMovement.add(d.deltaMovement);
				}
			}

			getPlayerBody(statePacket.playerId).setTransform(statePacket.position.add(cummulatedDeltaMovement),
					getPlayerBody(statePacket.playerId).getAngle());
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
