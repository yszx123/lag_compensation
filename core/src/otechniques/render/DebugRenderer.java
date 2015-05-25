package otechniques.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class DebugRenderer {
	private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();
	private final OrthographicCamera camera = new OrthographicCamera();
	private final World world;
	private float VIEW_WIDTH = 16;
	private float VIEW_HEIGHT = 9f;
	
	public DebugRenderer(World world) {
        camera.position.set(0,0,0); 
		this.world = world;
	}
	
	public void render() {
		Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
		renderer.setDrawVelocities(true);
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
