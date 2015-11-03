package otechniques.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import otechniques.Config;
import otechniques.ObjectsConfig;
import otechniques.network.packets.InputPacket;
import otechniques.network.packets.MousePositionPacket;
import otechniques.network.packets.NewPlayerPacket;
import otechniques.network.packets.Packet;
import otechniques.network.packets.PlayerStatePacket;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.objects.Grenade;
import otechniques.objects.Player;

public class ServerController extends CommonController {
	private final GameNetworkServer server;
	private Map<Integer, Long> lastProcessedRequest = new HashMap<>();
	private ArrayList<Grenade> objects = new ArrayList<>();
	float delta;

	public ServerController(GameWorld gameWorld, GameNetworkServer server) {
		super(gameWorld);
		this.server = server;
		gameWorld.createWalls();
	}

	public void updateGamestate(ConcurrentLinkedQueue<Packet> receivedPackets, float timestep) {

		Packet packet;
		while((packet = receivedPackets.poll()) != null){
			if (!gameWorld.getPlayers().containsKey(packet.playerId)) {
				continue;
			}
			lastProcessedRequest.put(packet.playerId, packet.sequenceNumber);

			if (packet instanceof InputPacket) {
				InputPacket p = (InputPacket) packet;
				processInputPacket(p);
			} else if (packet instanceof MousePositionPacket) {
				MousePositionPacket p = (MousePositionPacket) packet;
				processMousePositionPacket(p);
			}

		}
		
		delta += Gdx.graphics.getDeltaTime();
		if (delta >= Config.PLAYER_STATE_SENDING_FREQUENCY) {
			delta -= Config.PLAYER_STATE_SENDING_FREQUENCY;

			for (Player player : gameWorld.getPlayers().values()) {
				PlayerStatePacket statePacket = new PlayerStatePacket(Config.SERVER_ID, player.getId(),
						lastProcessedRequest.get(player.getId()), player.getPosition(), player.getAngle());
				server.addPacket(statePacket);
			}
		}

		updateCommonGameState(timestep);
	}

	@Override
	protected void processNewPlayerPacket(NewPlayerPacket packet) {
		super.processNewPlayerPacket(packet);
		// inform other clients that new player has joined
		lastProcessedRequest.put(packet.playerId, 0L);
		server.addControlPacket(new NewPlayerPacket(Config.runId, packet.playerId));
	}

	private Vector2 calculateMovementVector(Integer[] keysClicked) {
		Vector2 playerMovement = new Vector2();
		for (int key : keysClicked) {
			if (key == Keys.W) {
				playerMovement.add(new Vector2(0, ObjectsConfig.PLAYER_SPEED));
			} else if (key == Keys.S) {
				playerMovement.add(new Vector2(0, -ObjectsConfig.PLAYER_SPEED));
			} else if (key == Keys.A) {
				playerMovement.add(new Vector2(-ObjectsConfig.PLAYER_SPEED, 0));
			} else if (key == Keys.D) {
				playerMovement.add(new Vector2(ObjectsConfig.PLAYER_SPEED, 0));
			}
		}
		return playerMovement;
	}

	private void processInputPacket(InputPacket p) {
		gameWorld.getPlayer(p.playerId).body.setLinearVelocity(calculateMovementVector(p.keysPressed));

		getPlayerBody(p.playerId).setTransform(getPlayerBody(p.playerId).getPosition(),
				getPlayerBody(p.playerId).getAngle());

		for (int key : p.keysReleased) {
			if (key == Keys.G) {
				throwGrenade(p.playerId);
			}
		}
	}

	private void processMousePositionPacket(MousePositionPacket p) {
		getPlayerBody(p.playerId).setTransform(getPlayerBody(p.playerId).getPosition(),
				calculateDesiredPlayerRotation(p.playerId, p.inWorldMousePos));
	}

}
