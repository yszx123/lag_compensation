package otechniques.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.Config;
import otechniques.ObjectsConfig;
import otechniques.objects.GameWorld;
import otechniques.objects.Grenade;

public abstract class CommonController {
	protected final GameWorld gameWorld;
	protected final ArrayList<Grenade> objects;

	public CommonController(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		objects = new ArrayList<>();
	}

	protected void updateCommonGameState(float timeStep) {

		for (Iterator<Grenade> iterator = objects.iterator(); iterator.hasNext();) {
			Grenade g = iterator.next();
			if (g.isFlaggedForDelete()) {
				iterator.remove();
			} else {
				g.act(timeStep);
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

	protected void throwGrenade(int playerId) { // TODO nie powinno tak byc-
												// powinno sie uzyc jedynie
												// funkcji granatu
		Grenade g = new Grenade(gameWorld.getWorld(), gameWorld.getPlayer(playerId).body, true);
		objects.add(g);
		g.throwGrenade();
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

}
