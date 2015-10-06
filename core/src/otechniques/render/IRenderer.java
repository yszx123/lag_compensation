package otechniques.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IRenderer {
	public void resize(final int width, final int height);
	public void render();
	
	SpriteBatch BATCH = new SpriteBatch();
}
