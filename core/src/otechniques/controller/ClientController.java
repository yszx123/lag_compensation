package otechniques.controller;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.Config;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.objects.Trash;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;
import otechniques.render.Renderer;
import otechniques.utils.DeltaMovement;

public class ClientController extends CommonController {
	private final GameNetworkClient client;
	private final InputHandler inputHandler;
	private final ArrayList<DeltaMovement> deltaMovements;
	private final ArrayList<Trash> trashObjects;

	public ClientController(GameWorld gameWorld, GameNetworkClient client, InputHandler inputHandler) {
		super(gameWorld);
		this.client = client;
		this.inputHandler = inputHandler;
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
			if (packet instanceof PlayerPositionPacket) {
				calculatePlayerPosition((PlayerPositionPacket) packet);
			} // else if(...)

		}

		client.createInputPackets(inputHandler.getKeysPressed(), inputHandler.getKeysReleased());

		for (Trash trash : trashObjects) {
			trash.act(timeStep);
		}

		if (Config.CLIENT_SIDE_PREDICTION) {
			// movement
			appplyRecentMovementInput();
			DeltaMovement d = new DeltaMovement();
			d.deltaMovement = calculateMovementVector(inputHandler.getKeysPressed()).scl(Config.PHYSICS_TIMESTEP);
			d.sequenceNumber = client.getLastSequenceNumber();
			deltaMovements.add(d);

			// grenades
			if (inputHandler.getKeysReleased().contains(Keys.G)) {
				throwGrenade();
			}

			getPlayerBody().setTransform(getPlayerBody().getPosition(),
					calculateDesiredPlayerRotation(Renderer.getInWorldMousePosition()));
			System.out.println(Renderer.getInWorldMousePosition().toString());
		}

		updateCommonGameState(timeStep);
		inputHandler.refresh(); // TODO clears released keys
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

	private void createTrash() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			float x = (random.nextFloat() * 19) - 9;
			float y = (random.nextFloat() * 19) - 9;
			Trash trash = new Trash(gameWorld.getWorld(), new Vector2(x, y));
			trashObjects.add(trash);
		}
	}
}
