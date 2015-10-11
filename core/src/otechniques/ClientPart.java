package otechniques;

import otechniques.controller.ClientController;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.render.DebugRenderer;
import otechniques.render.IRenderer;
import otechniques.render.Renderer;

public class ClientPart {
	private IRenderer renderer;
	private InputHandler inputHandler;
	private ClientController controller;
	private GameNetworkClient networkClient;
	private GameWorld gameWorld;
	
	public final int CLIENT_ID;
	
	public ClientPart(int clientId){
		this.CLIENT_ID = clientId;
		
		networkClient = new GameNetworkClient();
		networkClient.setClientId(clientId);
		
		gameWorld = new GameWorld();
		
		inputHandler = new InputHandler();
		inputHandler.setupInputHandler();
		
		controller = new ClientController(gameWorld, networkClient, inputHandler);
		
		renderer = Config.DEBUG_RENDER ? new DebugRenderer(gameWorld.getWorld()) : new Renderer(gameWorld);
	}
	
	public void processClientSide(){
		controller.updateGameState(Config.CLIENT_PHYSICIS_TIMESTEP);	//TODO timestep
		networkClient.sendPackets();
	}
	
	public void renderGraphics(){
		renderer.render();
	}
	
	public void resize(int width, int height){
		renderer.resize(width, height);
	}
}
 