package otechniques.controller;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import otechniques.Config;
import otechniques.ObjectsConfig;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.objects.Grenade;
import otechniques.objects.Player;
import otechniques.packets.InputPacket;
import otechniques.packets.MousePositionPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

public class ServerController extends CommonController {
	private final GameNetworkServer server;
	private long lastProcessedRequest;
	private ArrayList<Grenade> objects = new ArrayList<>();
	float delta;

	public ServerController(GameWorld gameWorld, GameNetworkServer server) {
		super(gameWorld);
		this.server = server;
	}

	public void updateGamestate(ArrayList<Packet> receivedPackets, float timestep) {

		// process all new packets
		for (Packet packet : receivedPackets) {
			lastProcessedRequest = packet.sequenceNumber;

			if (packet instanceof InputPacket) {
				InputPacket p = (InputPacket) packet;
				processInputPacket(p);
			} else if (packet instanceof MousePositionPacket) {
				MousePositionPacket p = (MousePositionPacket) packet;
				processMousePositionPacket(p);
			}

		}
		delta += Gdx.graphics.getDeltaTime();
		if (delta >= Config.POSITION_SENDING_FREQUENCY) {
			delta -= Config.POSITION_SENDING_FREQUENCY;
			
			//TODO tu moze byc blad
			for (Player player : gameWorld.getPlayers().values()) {
				PlayerPositionPacket positionPacket = new PlayerPositionPacket(Config.SERVER_ID, player.getId(),
						lastProcessedRequest, player.getPosition().x,
						player.getPosition().y);
				server.addPacket(positionPacket);
			}
		}

		updateCommonGameState(timestep);
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
