package otechniques.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import otechniques.objects.GameWorld;

public class StandardRenderer extends Renderer {

	private final GameWorld world;

	public StandardRenderer(SpriteBatch batch, GameWorld world) {
		super(batch);
		this.world = world;
	}

	public void render() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.end();
	}
}
