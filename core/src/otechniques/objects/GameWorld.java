package otechniques.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld {
	private Player player;
	private World world;
	
	public GameWorld() {
		world = new World(new Vector2(), true);
		player = new Player(0, 0, world);
		createWalls();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public World getWorld() {
		return world;
	}
	
	private void createWalls(){
	    
		BodyDef wallDef = new BodyDef();
	    wallDef.type = BodyType.StaticBody;
	    wallDef.position.set(new Vector2());
	    Body wallBody = world.createBody(wallDef);
	    
	    //add four walls to the static body
	    PolygonShape wallShape = new PolygonShape();	      
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = wallShape;
		fixtureDef.density = 1f;
		
		wallShape.setAsBox(10, 0.1f, new Vector2(0, -10f), 0); //TODO wspolrzedne zalezne od kamery?
		wallBody.createFixture(fixtureDef);	
		wallShape.setAsBox(10, 0.1f, new Vector2(0, 10f), 0); 
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(0.1f, 10, new Vector2(10, 0), 0); 
		wallBody.createFixture(fixtureDef);
		wallShape.setAsBox(0.1f, 10, new Vector2(-10, 0), 0); 
		wallBody.createFixture(fixtureDef);
		
		wallShape.dispose();
	}
}