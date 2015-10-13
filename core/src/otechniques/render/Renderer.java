package otechniques.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import otechniques.Config;

public abstract class Renderer{
	
	protected static SpriteBatch batch = new SpriteBatch();
	protected OrthographicCamera camera = new OrthographicCamera();
	
	public Renderer(){
		resize(Config.CAMERA_VIEW_WIDTH, Config.CAMERA_VIEW_HEIGHT);
	}
	
	public abstract void render();
	
	public void resize(final int width, final int height){
		float aspectRatio = (float) width / (float) height;
		Config.CAMERA_VIEW_WIDTH = (int) (Config.CAMERA_VIEW_HEIGHT * aspectRatio);
		camera.viewportHeight = Config.CAMERA_VIEW_HEIGHT;
		Config.CAMERA_VIEW_WIDTH = Config.CAMERA_VIEW_WIDTH / 2;
		camera.viewportWidth = Config.CAMERA_VIEW_WIDTH; 
		camera.update();
	}
	
	public Camera getCamera(){
		return camera;
	}
}

