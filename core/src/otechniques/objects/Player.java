package otechniques.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.Config;
import otechniques.ObjectsConfig;

public class Player {
	public Body body;

	public Player(float x, float y, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = true;
		body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(ObjectsConfig.PLAYER_BODY_SIZE, ObjectsConfig.PLAYER_BODY_SIZE);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.filter.categoryBits = Config.COLLISION_CATEGORY_PLAYER;
		fixtureDef.filter.maskBits = Config.COLLISION_MASK_PLAYER;
		body.createFixture(fixtureDef);

		shape.dispose();

	}

	public void setPosition(Vector2 pos) {
		body.setTransform(pos, body.getAngle());
	}

	public void setPosition(float x, float y) {
		body.setTransform(new Vector2(x, y), body.getAngle());
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

}
