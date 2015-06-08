package otechniques;

import otechniques.controller.ClientController;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.render.Renderer;

public class ClientPart {
	private Renderer renderer;
	private InputHandler inputHandler;
	private ClientController controller;
	private GameNetworkClient client;
	
	public static boolean serverReconciliation = true;
	public static boolean clientSidePrediction = true;
	public static final int CLIENT_ID = 1; //TODO przeniesc gdzie indziej
	
	public ClientPart(){
		client = new GameNetworkClient();
		GameWorld world = new GameWorld();
		inputHandler = new InputHandler();
		inputHandler.setupInputHandler();
		controller = new ClientController(world, client, inputHandler);
		renderer = new Renderer(world);
	}
	
	public void processClientSide(){
		controller.updateGameState();
		client.sendPackets();
	}
	
	public void renderGraphics(){
		renderer.render();
	}
	
}
 