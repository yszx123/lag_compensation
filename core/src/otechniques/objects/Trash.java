package otechniques.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.config.Config;
import otechniques.config.ObjectsConfig;

public class Trash extends GameObject {

	public Trash(World world, Vector2 pos) {
		super(world);
		setBody(createBody(pos), Type.TRASH);
	}

	@Override
	public void act(float deltaTime) {
		if (flaggedForDeletion) {
			return;
		}

		if (body.getLinearVelocity().len() >= ObjectsConfig.TRASH_DESTROY_VELOCITY_THRESHOLD) {
			world.destroyBody(body);
			flaggedForDeletion = true;
		}

	}

	private Body createBody(Vector2 pos) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(pos);

		Body body = world.createBody(def);
		body.setAngularDamping(ObjectsConfig.TRASH_ANGULAR_DAMPING);
		float trashDampingVariation = MathUtils.random(-ObjectsConfig.TRASH_LINEAR_DAMPING_VARIATION_FACTOR,
				ObjectsConfig.TRASH_LINEAR_DAMPING_VARIATION_FACTOR);
		body.setLinearDamping(ObjectsConfig.TRASH_LINEAR_DAMPING + trashDampingVariation);

		CircleShape shape = new CircleShape();
		float trashSizeVariation = MathUtils.random(-ObjectsConfig.TRASH_SIZE_VARIATION_FACTOR,
				ObjectsConfig.TRASH_SIZE_VARIATION_FACTOR);
		shape.setRadius(ObjectsConfig.TRASH_SIZE + trashSizeVariation);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = ObjectsConfig.TRASH_DENSITY;
		;
		fixtureDef.restitution = ObjectsConfig.TRASH_RESTITUTION;
		fixtureDef.friction = ObjectsConfig.TRASH_FRICTION;
		fixtureDef.filter.categoryBits = Config.COLLISION_CATEGORY_TRASH;
		fixtureDef.filter.maskBits = Config.COLLISION_MASK_TRASH;

		body.createFixture(fixtureDef);
		shape.dispose();
		return body;
	}

}
