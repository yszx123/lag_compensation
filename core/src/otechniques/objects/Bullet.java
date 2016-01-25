package otechniques.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import otechniques.render.StandardRenderer;

public class Bullet extends GameObject {

	public Bullet(World world) {
		super(world, StandardRenderer.fragTexture);
		
	}

	@Override
	public void act(float deltaTime) {
		// TODO Auto-generated method stub

	}
	
	public void setBody(Body body){
		this.body = body;
	}

}
