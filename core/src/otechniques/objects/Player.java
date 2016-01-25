package otechniques.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.config.Config;
import otechniques.config.ObjectsConfig;

public class Player extends GameObject{

	private final int id;
	private long lastShootTime;

	public Player(int id, float x, float y, World world) {
		super(world, null);
		this.id = id;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = true;
		bodyDef.allowSleep = false;
		body = world.createBody(bodyDef);

		CircleShape shape = new CircleShape();
		shape.setRadius(ObjectsConfig.PLAYER_BODY_SIZE);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.filter.categoryBits = Config.COLLISION_CATEGORY_PLAYER;
		fixtureDef.filter.maskBits = Config.COLLISION_MASK_PLAYER;
		body.createFixture(fixtureDef);

		shape.dispose();

		
		setBody(body, Type.PLAYER);
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public int getId() {
		return id;
	}

	public float getAngle() {
		return body.getAngle();
	}

	public Body getBody() {
		return body;
	}

	public void reset() {
		body.setTransform(new Vector2(ObjectsConfig.PLAYER_STARTING_POS + id, ObjectsConfig.PLAYER_STARTING_POS - id),
				0);
	}

	/**
	 * @return normalized vector representing the direction player is rotated to
	 */
	public Vector2 getOrientationVector() {
		float angleRad = body.getAngle();
		return new Vector2(MathUtils.cos(angleRad), MathUtils.sin(angleRad));
	}

	@Override
	public void act(float deltaTime) {}
	
	public boolean shoot(){
		if(System.currentTimeMillis() - lastShootTime >= ObjectsConfig.SHOOTING_TIMESPAN_MS){
			lastShootTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
