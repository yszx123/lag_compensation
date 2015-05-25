package otechniques;

import java.util.HashMap;
import java.util.Map;

import otechniques.network.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.packets.PlayerPositionPacket;
import otechniques.render.Renderer;

import com.badlogic.gdx.Input.Keys;

public class ServerPart {
	GameNetworkServer server;
	GameWorld world;
	Renderer renderer;
	
	private Map<Integer, Boolean> keysClicked;
	
	public ServerPart(){
		keysClicked = new HashMap<Integer, Boolean>();
		
		server = new GameNetworkServer(keysClicked);
		world = new GameWorld();
		renderer = new Renderer(world);
	}
	
	public void render(){
		renderer.render();
	}
	
	public void sendPackets(){
		server.sendPackets();
	}
	
	public void processInput() {
		if(keysClicked.get(Keys.W) != null && keysClicked.get(Keys.W) == true){
			world.getPlayer().y += 1;
			server.addPacket(new PlayerPositionPacket(world.getPlayer().x, world.getPlayer().y));
		}
	}
	
	//TODO krok czasu brany pod uwage przy kalkulacji fizyki
	public void setTimestep(int timestep){
		
	}
}
