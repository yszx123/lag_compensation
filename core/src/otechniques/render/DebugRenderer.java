package otechniques.render;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class DebugRenderer extends Renderer{
	private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();

	private final World world;
	
	public DebugRenderer(World world) {
		super();
        camera.position.set(0,0,0); 
		this.world = world;
		renderer.setDrawVelocities(true);
	}
	
	public void render() {
		renderer.render(world, camera.combined);
	}
	
}
