package otechniques;

import otechniques.controller.ClientController;
import otechniques.input.InputHandler;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.render.DebugRenderer;
import otechniques.render.Renderer;
import otechniques.render.StandardRenderer;

public class ClientPart {
	private Renderer renderer;
	private InputHandler inputHandler;
	private ClientController controller;
	private GameNetworkClient networkClient;
	private GameWorld gameWorld;

	public final int clientId;
	public final boolean isClientControllable;

	public ClientPart(int clientId, boolean isClientControllable) {
		this.clientId = clientId;
		this.isClientControllable = isClientControllable;

		networkClient = new GameNetworkClient(clientId);

		gameWorld = new GameWorld();
		gameWorld.createPlayer(clientId);

		inputHandler = new InputHandler();
		if(isClientControllable){		
			inputHandler.setupInputHandler();
		}

		controller = new ClientController(clientId, isClientControllable, gameWorld, networkClient, inputHandler);

		renderer = Config.DEBUG_RENDER ? new DebugRenderer(gameWorld.getWorld()) : new StandardRenderer(gameWorld);
	}

	public void processClientSide() {
		controller.updateGameState(Config.PHYSICS_TIMESTEP);
		networkClient.sendPackets();
	}

	public void renderGraphics() {
		renderer.render();
	}

	public void resize(int width, int height) {
		renderer.resize(width, height);
	}

	public void dispose() {
		gameWorld.getWorld().dispose();
	}
}
