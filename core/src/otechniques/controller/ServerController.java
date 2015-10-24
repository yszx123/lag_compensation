package otechniques.controller;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import otechniques.Config;
import otechniques.ObjectsConfig;
import otechniques.ServerPart;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

public class ServerController {
	private final GameWorld world;
	private final GameNetworkServer server;
	private long lastProcessedRequest;
	float delta;

	public ServerController(GameWorld world, GameNetworkServer server) {
		this.world = world;
		this.server = server;
	}

	public void updateGamestate(ArrayList<Packet> receivedPackets) {

		// process all new packets
		for (Packet packet : receivedPackets) {
			lastProcessedRequest = packet.sequenceNumber;

			if (packet instanceof InputPacket) {
				InputPacket p = (InputPacket) packet;
				world.getPlayer().body.setLinearVelocity(calculateMovementVector(p.keysClicked));
			} // else if...

		}
		delta += Gdx.graphics.getDeltaTime();
		if (delta >= Config.POSITION_SENDING_FREQUENCY) {
			delta -= Config.POSITION_SENDING_FREQUENCY;
			PlayerPositionPacket positionPacket = new PlayerPositionPacket(ServerPart.SERVER_ID, lastProcessedRequest,
					world.getPlayer().getPosition().x, world.getPlayer().getPosition().y);
			server.addPacket(positionPacket);
		}

		world.getWorld().step(Config.SERVER_PHYSICS_TIMESTEP, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);
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
}
