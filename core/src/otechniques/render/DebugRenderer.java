package otechniques.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class DebugRenderer implements IRenderer{
	private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();
	private final OrthographicCamera camera = new OrthographicCamera();
	private final World world;
	private static int VIEW_WIDTH = 480;
	private static int VIEW_HEIGHT = 320;
	
	public DebugRenderer(World world) {
        camera.position.set(0,0,0); 
		this.world = world;
		renderer.setDrawVelocities(true);
	}
	
	public void render() {
		renderer.render(world, camera.combined);
	}
	
	public void resize(final int width, final int height){
		float aspectRatio = (float) width / (float) height;
		VIEW_WIDTH = (int) (VIEW_HEIGHT * aspectRatio);
		camera.viewportHeight = VIEW_HEIGHT;
		camera.viewportWidth = VIEW_WIDTH; 
		camera.update();
	}
}
