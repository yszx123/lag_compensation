package otechniques.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import otechniques.FastPaceGame;
import otechniques.config.Config;
import otechniques.network.ControlPacketObserver;
import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.RendererConfigurationControlPacket;

public abstract class Renderer implements ControlPacketObserver{

	protected SpriteBatch batch;
	protected static OrthographicCamera camera = new OrthographicCamera();
	protected BitmapFont font = new BitmapFont();
	protected boolean drawStats;
	protected String name;

	public Renderer(SpriteBatch batch) {
		this.batch = batch;
		font.setColor(Color.WHITE);
		font.getData().setScale(4.5f, 1.5f);	
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
		//if theres standard renderer attached, it's requried to map pixels to meters
		if(!Config.DEBUG_RENDER){
			inWorldMousePos.scl(1f/Config.METERES_TO_PIXELS);
		}
		// screen(and also x coordinates) is divided into
		// parts, so x must be multiplied by number of these parts
		return new Vector2(inWorldMousePos.x * FastPaceGame.getNumOfScreenParts(), inWorldMousePos.y);
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	public void setDrawStats(boolean drawStats) {
		this.drawStats = drawStats;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void processPacket(ControlPacket packet) {
		if (packet instanceof RendererConfigurationControlPacket) {
			processPacket((RendererConfigurationControlPacket) packet);
		}
	}

	protected void processPacket(RendererConfigurationControlPacket packet) {
		this.drawStats = packet.drawStats;
	}
}