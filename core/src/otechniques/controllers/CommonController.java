package otechniques.controllers;

import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import otechniques.config.Config;
import otechniques.config.ObjectsConfig;
import otechniques.network.ControlPacketObserver;
import otechniques.network.packets.ConfigurationControlPacket;
import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.NewPlayerPacket;
import otechniques.objects.GameObject;
import otechniques.objects.GameObject.Type;
import otechniques.objects.GameWorld;
import otechniques.objects.Player;

public abstract class CommonController implements ControlPacketObserver {

	protected final GameWorld gameWorld;
	protected long frameNumber;

	public CommonController(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}

	@Override
	public void processPacket(ControlPacket packet) {
		if (packet instanceof NewPlayerPacket) {
			processPacket((NewPlayerPacket) packet);
		} else if (packet instanceof ConfigurationControlPacket) {
			processPacket((ConfigurationControlPacket) packet);
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
		frameNumber++;
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

	protected Vector2 calculateMovementVector(Integer[] keysClicked) {
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

	protected void throwGrenade(int playerId) {
		gameWorld.createGrenade(playerId).throwGrenade();
	}

	protected void shootRay(int playerId) {
		if (!gameWorld.getPlayer(playerId).shoot()) {
			return;
		}

		RayCastCallback callback = (Fixture fixture, Vector2 point, Vector2 normal, float fraction) -> {
			Type collidedBodyType = (Type) fixture.getBody().getUserData();
			if(collidedBodyType == Type.PLAYER){
				gameWorld.createHitParticles(point);
				return 0;
			}
			return -1;
		};

		Vector2 p1 = getPlayerBody(playerId).getPosition().cpy();
		Vector2 p2 = new Vector2(getPlayerBody(playerId).getPosition().cpy()
				.add(getPlayer(playerId).getOrientationVector().scl(Config.RAYCAST_LEN)));

		gameWorld.getWorld().rayCast(callback, p1, p2);

	}

	protected void shootBullet(int playerId) {
		Player p = gameWorld.getPlayer(playerId);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		bodyDef.bullet = true;
		bodyDef.linearDamping = 10;
		bodyDef.position.set(p.getPosition());
		bodyDef.linearVelocity.set(p.getOrientationVector().scl(ObjectsConfig.BULLET_SPEED));
		Body body = gameWorld.getWorld().createBody(bodyDef);

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(0.05f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		body.createFixture(fixtureDef);
	}

	protected float calculateDesiredPlayerRotation(int clientId, Vector2 mousePos) {
		float playerY = gameWorld.getPlayer(clientId).getBody().getPosition().y;
		float playerX = gameWorld.getPlayer(clientId).getBody().getPosition().x;

		float desiredAngle = MathUtils.atan2(mousePos.y - playerY, mousePos.x - playerX);
		return desiredAngle;
	}

	protected Body getPlayerBody(int id) {
		return gameWorld.getPlayer(id).getBody();
	}

	protected Player getPlayer(int id) {
		return gameWorld.getPlayer(id);
	}

	protected void processPacket(NewPlayerPacket packet) {
		gameWorld.createPlayer(packet.playerId);
	}

	protected abstract void processPacket(ConfigurationControlPacket packet);
}
