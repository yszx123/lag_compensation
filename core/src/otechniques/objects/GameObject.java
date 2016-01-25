package otechniques.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {

	protected boolean flaggedForDeletion;
	protected World world;
	protected Body body;
	protected Body parentBody;
	public Texture texture;
	
	public static enum Type{
		PLAYER,
		TRASH,
		GRENADE,
		BULLET,
		WALL;
	}

	protected GameObject(World world, Texture texture) {
		this.world = world;
		this.texture = texture;
	}

	public abstract void act(float deltaTime);

	public boolean isFlaggedForDelete() {
		return flaggedForDeletion;
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
	
	public Vector2 getPosition(){
		return new Vector2(body.getPosition());
	}
	public float getRotationRad(){
		return body.getAngle();
	}
}
