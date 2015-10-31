package otechniques.objects;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.Config;

public class GameWorld {
	private Map<Integer, Player> players = new HashMap<>();
	private World world;

	public GameWorld() {
		world = new World(new Vector2(), true);
		createWalls();
	}

	public Player getPlayer(int id) {
		return players.get(id);
	}

	public Map<Integer, Player> getPlayers(){
		return players;
	}
	
	public World getWorld() {
		return world;
	}

	private void createWalls() { // TODO przeniesc

		BodyDef wallDef = new BodyDef();
		wallDef.type = BodyType.StaticBody;
		wallDef.position.set(new Vector2());
		Body wallBody = world.createBody(wallDef);

		PolygonShape wallShape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = wallShape;
		fixtureDef.density = 1f;
		fixtureDef.filter.categoryBits = Config.COLLISION_CATEGORY_SCENERY;
		fixtureDef.filter.maskBits = Config.COLLISION_MASK_SCENERY;

		wallShape.setAsBox(10, 0.1f, new Vector2(10, 20f), 0);
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(10, 0.1f, new Vector2(10, 0f), 0);
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(10, 0.1f, new Vector2(0, 10f), MathUtils.PI / 2f);
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(10, 0.1f, new Vector2(20f, 10f), MathUtils.PI / 2f);
		wallBody.createFixture(fixtureDef);

		wallShape.dispose();
	}
	
	public void createPlayer(int playerId){
		if(!players.containsKey(playerId)){
			players.put(playerId, new Player(playerId, 10+playerId, 10-playerId, world)); //TODO dwoch playerow w jednym miejscu
		}
	}

}