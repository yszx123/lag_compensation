package otechniques.render;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class DebugRenderer extends Renderer{
	private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();

	private final World world;
	
	public DebugRenderer(World world) {
		super();
		this.world = world;
		renderer.setDrawVelocities(true);
	}
	
	public void render() {
		camera.position.set(-5, -5, 0);
		camera.update();
		renderer.render(world, camera.combined);		
	}
	
}
