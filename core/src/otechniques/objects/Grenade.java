package otechniques.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.config.ObjectsConfig;
import otechniques.render.StandardRenderer;

public class Grenade extends GameObject {

	private boolean isIgnited;
	private boolean hasAlreadyExploded;
	private float timeLeftToExplosion;
	private float timeAfterExplosion;
	private final Body[] grenadeParticles = new Body[ObjectsConfig.GRENADE_FRAGS_COUNT];;

	public Grenade(World world, Body parentBody, boolean isIgnited) {
		super(world, StandardRenderer.grenadeTexture);
		setParentBody(parentBody);
		setBody(createBody(parentBody.getPosition()), Type.GRENADE);
		this.isIgnited = isIgnited;
		this.timeLeftToExplosion = ObjectsConfig.GRENADE_EXPLOSION_LATENCY;
	}

	public void act(float delta) {
		if (flaggedForDeletion) {
			return;
		} else if (hasAlreadyExploded) {
			timeAfterExplosion += delta;
			if (timeAfterExplosion >= ObjectsConfig.GRENADE_PARTICLE_LIVING_TIME) {
				for (Body body : grenadeParticles) {
					world.destroyBody(body);
				}
				flaggedForDeletion = true;
			}
		} else if (isIgnited) {
			timeLeftToExplosion -= delta;
			if (timeLeftToExplosion <= 0) {
				explode();
			}
		}

	}

	public void explode() {
		if (flaggedForDeletion) {
			throw new IllegalStateException("Attempted to explode grenade, which is marked to be deleted");
		}
		world.destroyBody(body);
		hasAlreadyExploded = true;
		texture = null;
		createBlastParticles();
	}

	public void throwGrenade() {
		if (flaggedForDeletion || hasAlreadyExploded) {
			throw new IllegalStateException(
					"Attempted to throw grenade, which is marked to be deleted or has already exploded");
		}
		
		Vector2 directionVector = new Vector2(MathUtils.cos(parentBody.getAngle()), MathUtils.sin(parentBody.getAngle()));
		body.applyLinearImpulse(directionVector.scl(ObjectsConfig.GRENADE_THROW_POWER), parentBody.getPosition(), true);
	}

	private Body createBody(Vector2 pos) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(pos);
		def.linearDamping = ObjectsConfig.GRENADE_LINEAR_DAMPING;
		Body body = world.createBody(def);
		body.setFixedRotation(false);
		body.setAngularDamping(1);
		body.setAngularVelocity(6);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(ObjectsConfig.GRENADE_SIZE);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;

		body.createFixture(fixtureDef);
		shape.dispose();
		return body;
	}

	private void createBlastParticles() {
		float numRays = ObjectsConfig.GRENADE_FRAGS_COUNT;
		for (int i = 0; i < numRays; i++) {
			float angle = (i / numRays) * MathUtils.PI2;
			Vector2 rayDirection = new Vector2(MathUtils.sin(angle), MathUtils.cos(angle));

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.fixedRotation = true;
			bodyDef.bullet = true;
			bodyDef.linearDamping = 10;
			bodyDef.position.set(body.getPosition());
			bodyDef.linearVelocity.set(rayDirection.scl(ObjectsConfig.GRENADE_EXPLOSION_POWER));
			Body body = world.createBody(bodyDef);

			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(0.05f);

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = circleShape;
			fixtureDef.density = 60f / numRays;
			fixtureDef.restitution = 0.99f;
			fixtureDef.filter.categoryBits = -1;
			body.createFixture(fixtureDef);

			grenadeParticles[i] = body;
		}
	}
	public Body[] getFrags(){
		return grenadeParticles;
	}
	
}
