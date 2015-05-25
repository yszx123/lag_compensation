package otechniques;

import java.util.concurrent.ArrayBlockingQueue;

import otechniques.input.InputHandler;
import otechniques.network.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;
import otechniques.render.Renderer;

import com.badlogic.gdx.Gdx;

public class ClientPart {
	private GameWorld world;
	private Renderer renderer;
	private InputHandler inputHandler;
	private GameNetworkClient client;
	private ArrayBlockingQueue<Packet> receivedPackets;
	
	public ClientPart(){
		receivedPackets = new ArrayBlockingQueue<Packet>(1900);
		client = new GameNetworkClient(receivedPackets);
		world = new GameWorld();
		setupInputHandler();
		renderer = new Renderer(world);
	}
	
	private void setupInputHandler(){
		Gdx.input.setInputProcessor(inputHandler = new InputHandler(world, client));
	}
	
	public void render(){
		renderer.render();
	}
	
	public void sendPackets(){
		client.sendPackets();
		inputHandler.processInput();	//TODO przeneisc gdzie indziej
	}
	
	public void processReceivedPackets(){
		for (Packet packet : receivedPackets) {
			if(packet instanceof PlayerPositionPacket){
				PlayerPositionPacket p = (PlayerPositionPacket) packet;	
				world.getPlayer().x = p.x;
				world.getPlayer().y = p.y;
			}
			receivedPackets.remove(packet);
		}
	}
}
 