package otechniques.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

import otechniques.config.Config;
import otechniques.objects.GameObject;
import otechniques.objects.GameWorld;
import otechniques.objects.Grenade;
import otechniques.objects.Player;

public class StandardRenderer extends Renderer {

	private final GameWorld world;

	public static final Texture floorTexture = new Texture(Gdx.files.internal("textures/floor.png"));
	public static final Texture playerTexture = new Texture(Gdx.files.internal("textures/player.png"));
	public static final Texture wallTexture = new Texture(Gdx.files.internal("textures/wall.png"));
	public static final Texture grenadeTexture = new Texture(Gdx.files.internal("textures/grenade.png"));
	public static final Texture trashTexture = new Texture(Gdx.files.internal("textures/trash.png"));
	public static final Texture fragTexture = new Texture(Gdx.files.internal("textures/frag.png"));
	private TextureRegion playerRegion, wallRegion;

	public StandardRenderer(SpriteBatch batch, GameWorld world) {
		super(batch);
		this.world = world;
		camera = new OrthographicCamera(20 * Config.METERES_TO_PIXELS, 20 * Config.METERES_TO_PIXELS);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
		playerRegion = new TextureRegion(playerTexture);
		wallRegion = new TextureRegion(wallTexture);
	}

	public void render() {

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(floorTexture, 0, 0);

		for (GameObject object : world.getGameObjects()) {
			if (object.texture != null) {
				batch.draw(new TextureRegion(object.texture), object.getPosition().x * Config.METERES_TO_PIXELS,
						object.getPosition().y * Config.METERES_TO_PIXELS, object.texture.getWidth() / 2f,
						object.texture.getHeight() / 2f, object.texture.getWidth(), object.texture.getHeight(), 1f, 1f,
						object.getRotationRad() * MathUtils.radiansToDegrees);
			}
			if (object instanceof Grenade) {
				Grenade g = (Grenade) object;
				for (Body frag : g.getFrags()) {
					if (frag != null) {
						batch.draw(fragTexture, frag.getPosition().x * Config.METERES_TO_PIXELS,
								frag.getPosition().y * Config.METERES_TO_PIXELS);
					}
				}
			}
			
		}

		batch.draw(wallTexture, 0, -55);
		batch.draw(wallTexture, 0, 20 * Config.METERES_TO_PIXELS - 35);
		batch.draw(wallRegion, 35, 0, 0, 0, wallTexture.getWidth(), wallTexture.getHeight(), 1f, 1f, 90);
		batch.draw(wallRegion, 20 * Config.METERES_TO_PIXELS + 55, 0, 0, 0, wallTexture.getWidth(),
				wallTexture.getHeight(), 1f, 1f, 90);

		for (Player p : world.getPlayers().values()) {
			float x = p.getPosition().x * Config.METERES_TO_PIXELS;
			float y = p.getPosition().y * Config.METERES_TO_PIXELS;
			float rotation = p.getAngle();
			batch.draw(playerRegion, x, y, 30, 30, playerTexture.getWidth(), playerTexture.getHeight(), 1f, 1f,
					rotation * MathUtils.radiansToDegrees);
		}

		batch.end();

	}

	@Override
	public void resize(final int width, final int height) {
	}

}
