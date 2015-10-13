package otechniques.render;

import otechniques.assets.Assets;
import otechniques.objects.GameWorld;

public class StandardRenderer extends Renderer{
	
	private final GameWorld world;
	
	public StandardRenderer(GameWorld world) {
		super();
		this.world = world;
	}
		
	public void render(){
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(Assets.texture, world.getPlayer().getPosition().x, world.getPlayer().getPosition().y);
		batch.end();
	}
}

