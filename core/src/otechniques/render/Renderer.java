package otechniques.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

import otechniques.assets.Assets;
import otechniques.objects.GameWorld;

public class Renderer implements IRenderer{
	
	public final Camera camera = new OrthographicCamera();  
	private final GameWorld world;
	private static int VIEW_WIDTH = 480;
	private static int VIEW_HEIGHT = 320;
	
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
		BATCH.setProjectionMatrix(camera.combined);
		BATCH.begin();
		BATCH.draw(Assets.texture, world.getPlayer().getPosition().x, world.getPlayer().getPosition().y);
		BATCH.end();
	}
}

