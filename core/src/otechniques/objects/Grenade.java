package otechniques.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.ObjectsConfig;

public class Grenade extends GameObject {

	private boolean isIgnited;
	private float timeLeftToExplosion;

	public Grenade(World world, Vector2 pos, boolean isIgnited) {
		super(world);
		setBody(createBody(pos));
		this.isIgnited = isIgnited;
		this.timeLeftToExplosion = ObjectsConfig.GRENADE_EXPLOSION_LATENCY;
	}

	public Grenade(World world, Body parentBody, boolean isIgnited) {
		super(world);
		setParentBody(parentBody);
		setBody(createBody(parentBody.getPosition()));
		this.isIgnited = isIgnited;
		this.timeLeftToExplosion = ObjectsConfig.GRENADE_EXPLOSION_LATENCY;
	}

	public void act(float delta) {
		if (!isAlive) {
			return;
		}

		if (isIgnited) {
			timeLeftToExplosion -= delta;
			if (timeLeftToExplosion <= 0) {
				explode();
			}
		}
	}

	public void explode() {
		if (!isAlive) {
			throw new IllegalStateException("Attempted to explode grenade, which isn't alive");
		}
		createBlastParticles();
		dispose();
	}

	public void throwGrenade(Vector2 impulseVector) {
		body.applyLinearImpulse(impulseVector, body.getPosition(), true);

	}

	public boolean isIgnited() {
		return isIgnited;
	}

	public void setIsIgnited(boolean ignited) {
		this.isIgnited = ignited;
	}

	public float getTimeLeftToExplosion() {
		return timeLeftToExplosion;
	}

	public void setTimeLeftToExplosion(float timeLeftToExplosion) {
		this.timeLeftToExplosion = timeLeftToExplosion;
	}

	private Body createBody(Vector2 pos) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(pos);
		def.linearDamping = ObjectsConfig.GRENADE_LINEAR_DAMPING;

		Body body = world.createBody(def);
		body.setFixedRotation(true);

		CircleShape shape = new CircleShape();
		shape.setRadius(ObjectsConfig.GRENADE_SIZE);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;

		body.createFixture(fixtureDef);
		shape.dispose();
		return body;
	}

	private void createBlastParticles() {
		int numRays = 120;
		for (int i = 0; i < numRays; i++) {
			float angle = (float) Math.toRadians(((double) i / (double) numRays) * 360);
			Vector2 rayDir = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.fixedRotation = true; // rotation not necessary
			bodyDef.bullet = true; // prevent tunneling at high speed
			bodyDef.linearDamping = 10; // drag due to moving through air
			bodyDef.gravityScale = 0; // ignore gravity
			bodyDef.position.set(body.getPosition()); // start at blast center
			bodyDef.linearVelocity.set(rayDir.scl(ObjectsConfig.GRENADE_EXPLOSION_POWER));
			Body body = world.createBody(bodyDef);

			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(0.05f);

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = circleShape;
			fixtureDef.density = 60 / (float) numRays; // very high - shared
														// across all particles
			fixtureDef.friction = 0; // friction not necessary
			fixtureDef.restitution = 0.99f; // high restitution to reflect off
											// obstacles
			fixtureDef.filter.categoryBits = -1;
			body.createFixture(fixtureDef);
		}
	}
}
