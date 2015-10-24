package otechniques.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {

	protected boolean isAlive;
	protected World world;
	protected Body body;
	protected Body parentBody;

	protected GameObject(World world) {
		this.world = world;
		this.isAlive = true;
	}

	protected void dispose() {
		world.destroyBody(body);
		isAlive = false;
	}

	protected void setBody(Body body) {
		this.body = body;
	}

	public abstract void act(float deltaTime);

	public boolean isAlive() {
		return isAlive;
	}

	public Body getParentBody() {
		return parentBody;
	}

	public void setParentBody(Body parentBody) {
		this.parentBody = parentBody;
	}
}
