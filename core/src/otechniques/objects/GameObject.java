package otechniques.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {

	protected boolean flaggedForDelete;
	protected World world;
	protected Body body;
	protected Body parentBody;

	protected GameObject(World world) {
		this.world = world;
	}

	protected void setBody(Body body) {
		this.body = body;
	}

	public abstract void act(float deltaTime);

	public boolean isFlaggedForDelete() {
		return flaggedForDelete;
	}

	public Body getParentBody() {
		return parentBody;
	}

	public void setParentBody(Body parentBody) {
		this.parentBody = parentBody;
	}
}
