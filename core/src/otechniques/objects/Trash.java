package otechniques.objects;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.Config;
import otechniques.ObjectsConfig;

public class Trash extends GameObject {

	public Trash(World world, Vector2 pos) {
		super(world);
		setBody(createBody(pos));
	}

	@Override
	public void act(float deltaTime) {
		if(!isAlive)
			return;
		
		if (body.getLinearVelocity().len() >= ObjectsConfig.TRASH_DESTROY_VELOCITY_THRESHOLD) {
			dispose();
		}

	}

	private Body createBody(Vector2 pos) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(pos);

		Body body = world.createBody(def);
		Random random = new Random();
		float trashDampingVariation = random.nextFloat() * 2 * ObjectsConfig.TRASH_LINEAR_DAMPING_VARIATION_FACTOR;
		trashDampingVariation -= ObjectsConfig.TRASH_LINEAR_DAMPING_VARIATION_FACTOR;
		body.setLinearDamping(ObjectsConfig.TRASH_LINEAR_DAMPING + trashDampingVariation);

		CircleShape shape = new CircleShape();
		float trashSizeVariation = random.nextFloat() * 2 * ObjectsConfig.TRASH_SIZE_VARIATION_FACTOR;
		trashSizeVariation -= ObjectsConfig.TRASH_SIZE_VARIATION_FACTOR;
		shape.setRadius(ObjectsConfig.TRASH_SIZE + trashSizeVariation);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = ObjectsConfig.TRASH_RESTITUTION;
		fixtureDef.friction = ObjectsConfig.TRASH_FRICTION;
		fixtureDef.filter.categoryBits = Config.COLLISION_CATEGORY_TRASH;
		fixtureDef.filter.maskBits = Config.COLLISION_MASK_TRASH;

		body.createFixture(fixtureDef);
		shape.dispose();
		return body;
	}

}
