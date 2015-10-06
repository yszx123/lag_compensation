package otechniques.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld {
	private Player player;
	private World world;
	
	public GameWorld() {
		world = new World(new Vector2(), true);
		player = new Player(0, 0, world);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public World getWorld() {
		return world;
	}
	
	
}