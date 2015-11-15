package otechniques.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.config.Config;
import otechniques.config.ObjectsConfig;
import otechniques.objects.GameObject.Type;

public class GameWorld {

	private Map<Integer, Player> players = new HashMap<>();
	private World world;
	protected ArrayList<GameObject> gameObjects = new ArrayList<>();

	public GameWorld() {
		world = new World(new Vector2(), true);
	}

	public Player getPlayer(int id) {
		return players.get(id);
	}

	public Map<Integer, Player> getPlayers() {
		return players;
	}

	public World getWorld() {
		return world;
	}

	public void createPlayer(int playerId) {
		if (!players.containsKey(playerId)) {
			Player p = new Player(playerId, ObjectsConfig.PLAYER_STARTING_POS + playerId,
					ObjectsConfig.PLAYER_STARTING_POS - playerId, world);
			players.put(playerId, p);
			gameObjects.add(p);
		}
	}

	public void createTrash() {
		for (int i = 0; i < ObjectsConfig.TRASH_NUM; i++) {
			float x = MathUtils.random(1, 2 * ObjectsConfig.WALL_SIZE - 1);
			float y = MathUtils.random(1, 2 * ObjectsConfig.WALL_SIZE - 1);
			Trash trash = new Trash(world, new Vector2(x, y));
			gameObjects.add(trash);
		}
	}

	public ArrayList<GameObject> getGameObjects() {
		return gameObjects;
	}

	public Grenade createGrenade(int playerId) {
		Grenade g = new Grenade(world, players.get(playerId).getBody(), true);
		gameObjects.add(g);
		return g;
	}

	public void createWalls() {

		BodyDef wallDef = new BodyDef();
		wallDef.type = BodyType.StaticBody;
		wallDef.position.set(new Vector2());
		Body wallBody = world.createBody(wallDef);
		wallBody.setUserData(Type.WALL);
		
		PolygonShape wallShape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = wallShape;
		fixtureDef.density = 1f;
		fixtureDef.filter.categoryBits = Config.COLLISION_CATEGORY_SCENERY;
		fixtureDef.filter.maskBits = Config.COLLISION_MASK_SCENERY;

		float wallLen = ObjectsConfig.WALL_SIZE;
		wallShape.setAsBox(wallLen, 0.1f, new Vector2(wallLen, 2 * wallLen), 0);
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(wallLen, 0.1f, new Vector2(wallLen, 0f), 0);
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(wallLen, 0.1f, new Vector2(0, wallLen), MathUtils.PI / 2f);
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(wallLen, 0.1f, new Vector2(2 * wallLen, wallLen), MathUtils.PI / 2f);
		wallBody.createFixture(fixtureDef);

		wallShape.dispose();
	}
	
	public void createHitParticles(Vector2 pos){
		float numRays = 4;
		for (int i = 0; i < numRays; i++) {
			float angle = (i / numRays) * MathUtils.PI2;
			Vector2 rayDirection = new Vector2(MathUtils.sin(angle), MathUtils.cos(angle));

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.fixedRotation = true;
			bodyDef.bullet = true;
			bodyDef.linearDamping = 10;
			bodyDef.position.set(pos);
			bodyDef.linearVelocity.set(rayDirection.scl(10));
			Body body = world.createBody(bodyDef);

			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(0.1f);

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = circleShape;
			fixtureDef.density = 60f / numRays;
			fixtureDef.restitution = 0.1f;
			fixtureDef.filter.categoryBits = -1;
			body.createFixture(fixtureDef);
		}
	}
	
}