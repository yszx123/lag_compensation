package otechniques;

import otechniques.controllers.ClientController;
import otechniques.input.InputHandler;
import otechniques.input.InputSupplier;
import otechniques.input.RandomInputSpoofer;
import otechniques.network.client.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.render.DebugRenderer;
import otechniques.render.Renderer;
import otechniques.render.StandardRenderer;

public class ClientPart {
	private Renderer renderer;
	private InputSupplier inputSupplier;
	private ClientController controller;
	private final GameNetworkClient networkClient;
	private final GameWorld gameWorld;

	public final int clientId;
	public final boolean isClientControllable;

	public ClientPart(boolean isClientControllable) {
		this.isClientControllable = isClientControllable;

		networkClient = new GameNetworkClient();
		clientId = networkClient.getClientId();
		
		gameWorld = new GameWorld();
		gameWorld.createPlayer(clientId);

		
		if(isClientControllable){	
			InputHandler inputHandler = new InputHandler();
			inputHandler.setupInputHandler();
			inputSupplier = inputHandler;
		}else{
			inputSupplier = new RandomInputSpoofer();
		}

		controller = new ClientController(clientId, isClientControllable, gameWorld, networkClient, inputSupplier);

		renderer = Config.DEBUG_RENDER ? new DebugRenderer(gameWorld.getWorld()) : new StandardRenderer(gameWorld);
	}

	public void processClientSide() {
		controller.processControlPackets(networkClient.getReceivedControlPackets());
		controller.updateGameState(Config.PHYSICS_TIMESTEP);
		networkClient.sendPackets();
		inputSupplier.refresh();
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
