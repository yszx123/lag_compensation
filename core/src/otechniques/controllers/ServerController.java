package otechniques.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import otechniques.config.Config;
import otechniques.network.ControlPacketObserver;
import otechniques.network.packets.ConfigurationControlPacket;
import otechniques.network.packets.InputPacket;
import otechniques.network.packets.MousePositionPacket;
import otechniques.network.packets.NewPlayerPacket;
import otechniques.network.packets.Packet;
import otechniques.network.packets.PlayerStatePacket;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.objects.Player;
import otechniques.utils.CircularBuffer;
import otechniques.utils.PositionSnapshot;

public final class ServerController extends CommonController implements ControlPacketObserver {
	private final GameNetworkServer server;
	private Map<Integer, Long> lastProcessedRequest = new HashMap<>();
	private CircularBuffer<PositionSnapshot> positionSnapshots = new CircularBuffer<>(Config.NUMBER_OF_SNAPSHOTS_SAVED);
	private float timeSincePacketSending;

	public ServerController(GameWorld gameWorld, GameNetworkServer server) {
		super(gameWorld);
		this.server = server;
		gameWorld.createWalls();
	}

	public void updateGamestate(ConcurrentLinkedQueue<Packet> receivedPackets, float timestep) {

		Packet packet;
		while ((packet = receivedPackets.poll()) != null) {
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

		timeSincePacketSending += Gdx.graphics.getDeltaTime();
		if (timeSincePacketSending >= Config.PLAYER_STATE_SENDING_FREQUENCY) {
			timeSincePacketSending -= Config.PLAYER_STATE_SENDING_FREQUENCY;

			for (Player player : gameWorld.getPlayers().values()) {
				PlayerStatePacket statePacket = new PlayerStatePacket(Config.SERVER_ID, player.getId(),
						lastProcessedRequest.get(player.getId()), player.getPosition(), player.getAngle());
				server.addPacket(statePacket);
			}
		}

		updateCommonGameState(timestep);
		savePositionSnapshots();
	}

	@Override
	protected void processPacket(NewPlayerPacket packet) {
		super.processPacket(packet);
		// inform other clients that new player has joined
		lastProcessedRequest.put(packet.playerId, 0L);
		server.addControlPacketToSend(new NewPlayerPacket(Config.runId, packet.playerId));
	}


	protected void processPacket(ConfigurationControlPacket packet) {
		if (packet.wasResetIssued) {
			for (Player player : gameWorld.getPlayers().values()) {
				player.reset();
			}
		}
	}

	private void processInputPacket(InputPacket p) {
		gameWorld.getPlayer(p.playerId).getBody().setLinearVelocity(calculateMovementVector(p.keysPressed));

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

	private void savePositionSnapshots() {
		if ((frameNumber % 2) == 0) {
			return;
		}

		PositionSnapshot snapshot = new PositionSnapshot();
		snapshot.timestamp = System.currentTimeMillis();
		for (Player player : gameWorld.getPlayers().values()) {
			snapshot.playerPositionSnapshots.put(player.getId(), player.getPosition().cpy());
		}

		positionSnapshots.put(snapshot);
	}

}
