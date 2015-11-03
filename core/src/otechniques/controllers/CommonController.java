package otechniques.controllers;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.Config;
import otechniques.ObjectsConfig;
import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.NewPlayerPacket;
import otechniques.objects.GameObject;
import otechniques.objects.GameWorld;

public abstract class CommonController {
	
	protected final GameWorld gameWorld;

	public CommonController(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	public void processControlPackets(ConcurrentLinkedQueue<ControlPacket> unprocessedControlPackets) {
		ControlPacket packet;
		while((packet = unprocessedControlPackets.poll()) != null){
			if (packet instanceof NewPlayerPacket) {
				NewPlayerPacket p = (NewPlayerPacket) packet;
				processNewPlayerPacket(p);
			}
		}
	}

	protected void updateCommonGameState(float timeStep) {

		for (Iterator<GameObject> iterator = gameWorld.getGameObjects().iterator(); iterator.hasNext();) {
			GameObject obj = iterator.next();
			if (obj.isFlaggedForDelete()) {
				iterator.remove();
			} else {
				obj.act(timeStep);
			}
		}
			
		gameWorld.getWorld().step(Config.PHYSICS_TIMESTEP, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);
	}

	/**
	 * @return vector representing player's movement direction
	 */
	protected Vector2 calculateMovementVector(Set<Integer> keysPressed) {
		Vector2 playerMovement = new Vector2();

		if (keysPressed.contains(Keys.W)) {
			playerMovement.add(new Vector2(0, ObjectsConfig.PLAYER_SPEED));
		}
		if (keysPressed.contains(Keys.S)) {
			playerMovement.add(new Vector2(0, -ObjectsConfig.PLAYER_SPEED));
		}
		if (keysPressed.contains(Keys.A)) {
			playerMovement.add(new Vector2(-ObjectsConfig.PLAYER_SPEED, 0));
		}
		if (keysPressed.contains(Keys.D)) {
			playerMovement.add(new Vector2(ObjectsConfig.PLAYER_SPEED, 0));
		}
		return playerMovement;
	}

	protected void throwGrenade(int playerId) {
		gameWorld.createGrenade(playerId).throwGrenade();
	}

	protected float calculateDesiredPlayerRotation(int clientId, Vector2 mousePos) {
		float playerY = gameWorld.getPlayer(clientId).body.getPosition().y;
		float playerX = gameWorld.getPlayer(clientId).body.getPosition().x;

		float desiredAngle = MathUtils.atan2(mousePos.y - playerY, mousePos.x - playerX);
		return desiredAngle;
	}

	protected Body getPlayerBody(int id) {
		return gameWorld.getPlayer(id).body;
	}
	
	protected void processNewPlayerPacket(NewPlayerPacket packet){
		gameWorld.createPlayer(packet.playerId);
	}


}
