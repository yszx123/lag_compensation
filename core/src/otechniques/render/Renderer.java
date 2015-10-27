package otechniques.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import otechniques.Config;
import otechniques.FastPaceGame;

public abstract class Renderer {

	protected final static SpriteBatch batch = new SpriteBatch();
	protected final static OrthographicCamera camera = new OrthographicCamera();

	public Renderer() {
		resize(Config.CAMERA_VIEW_WIDTH, Config.CAMERA_VIEW_HEIGHT);
	}

	public abstract void render();

	public void resize(final int width, final int height) {
		camera.viewportHeight = Config.CAMERA_VIEW_HEIGHT;
		camera.viewportWidth = Config.CAMERA_VIEW_WIDTH;
		camera.update();
	}

	public Camera getCamera() {
		return camera;
	}

	public static Vector2 getInWorldMousePosition() {
		Vector3 absoluteMousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		Vector3 inWorldMousePos = camera.unproject(absoluteMousePos);
		// screen(and also x coordinates) is divided into
		// parts, so x must be multiplied by number of these parts
		return new Vector2(inWorldMousePos.x * FastPaceGame.getNumOfScreenParts(), inWorldMousePos.y);
	}

	public void dispose() {
		batch.dispose();
	}
}
