package otechniques;

import java.util.concurrent.ArrayBlockingQueue;

import otechniques.input.InputHandler;
import otechniques.network.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.Packet;
import otechniques.render.Renderer;

import com.badlogic.gdx.Gdx;

public class ClientPart {
	private GameWorld world;
	private Renderer renderer;
	private InputHandler inputHandler;
	private GameNetworkClient client;
	
	private boolean serverReconciliation;
	private boolean clientSidePrediction;
	public static final int CLIENT_ID = 1; //TODO przeniesc gdzie indziej
	
	private ArrayBlockingQueue<Packet> receivedPackets;
	
	public ClientPart(){
		receivedPackets = new ArrayBlockingQueue<Packet>(1900);
		client = new GameNetworkClient();
		world = new GameWorld();
		setupInputHandler();
		renderer = new Renderer(world);
	}
	
	public void processClientSide(){
		client.createInputPackets(inputHandler.getKeysPressed());
		sendPackets();
		renderGraphics();
	}
	
	private void setupInputHandler(){
		Gdx.input.setInputProcessor(inputHandler = new InputHandler());
	}
	
	public  void renderGraphics(){
		renderer.render();
	}
	
	private void sendPackets(){
		client.sendPackets();
	}
	
	

}
 