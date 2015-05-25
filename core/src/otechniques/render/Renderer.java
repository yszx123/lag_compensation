package otechniques.render;

import otechniques.assets.Assets;
import otechniques.objects.GameWorld;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Renderer {
	
	public final Camera camera = new OrthographicCamera();  
	private final GameWorld world;
	private static int VIEW_WIDTH = 480;
	private static int VIEW_HEIGHT = 320;
	private final static SpriteBatch batch = new SpriteBatch();
	
	public Renderer(GameWorld world) {
		resize(VIEW_WIDTH, VIEW_HEIGHT);
		this.world = world;
	}
	
	public void resize(final int width, final int height){
		float aspectRatio = (float) width / (float) height;
		VIEW_WIDTH = (int) (VIEW_HEIGHT * aspectRatio);
		camera.viewportHeight = VIEW_HEIGHT;
		camera.viewportWidth = VIEW_WIDTH; 
		camera.update();
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public void render(){
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(Assets.texture, world.getPlayer().x, world.getPlayer().y);
		batch.end();
	}
}

