package otechniques.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {

	protected boolean flaggedForDelete;
	protected World world;
	protected Body body;
	protected Body parentBody;
	
	public static enum Type{
		PLAYER,
		TRASH,
		GRENADE,
		WALL
	}

	protected GameObject(World world) {
		this.world = world;
	}

	public abstract void act(float deltaTime);

	public boolean isFlaggedForDelete() {
		return flaggedForDelete;
	}
	
	protected void setBody(Body body, Type bodyType) {
		this.body = body;
		this.body.setUserData(bodyType);
	}

	public Body getParentBody() {
		return parentBody;
	}

	public void setParentBody(Body parentBody) {
		this.parentBody = parentBody;
	}
}
