package otechniques.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import otechniques.config.Config;
import otechniques.objects.GameWorld;
import otechniques.objects.Player;

public class DebugRenderer extends Renderer{
	
	private final Box2DDebugRenderer renderer = new Box2DDebugRenderer();
	private final ShapeRenderer shapeRenderer = new ShapeRenderer();
	private final GameWorld world;
	
	
	public DebugRenderer(SpriteBatch batch, GameWorld world) {
		super(batch);
		this.world = world;
		renderer.setDrawVelocities(false);
	}
	
	public void render() {
		System.out.println(getInWorldMousePosition());
		camera.position.set(10, 10, 0);
		camera.update();
		renderer.render(world.getWorld(), camera.combined);		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		for (Player player : world.getPlayers().values()) {
			shapeRenderer.line(player.getPosition().cpy(), 
					player.getPosition().cpy().add(player.getOrientationVector().scl(Config.RAYCAST_LEN)));
		}
		
		shapeRenderer.end();
		
		if(drawStats){
			batch.begin();	
			font.draw(batch, name, 30, 23);
			batch.end();
		}
	}
	
}
