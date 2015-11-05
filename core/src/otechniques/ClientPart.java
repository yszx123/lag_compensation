package otechniques;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import otechniques.config.Config;
import otechniques.controllers.ClientController;
import otechniques.input.InputSupplier;
import otechniques.network.ControlPacketObserver;
import otechniques.network.client.GameNetworkClient;
import otechniques.network.packets.ControlPacket;
import otechniques.objects.GameWorld;
import otechniques.render.DebugRenderer;
import otechniques.render.Renderer;
import otechniques.render.StandardRenderer;

public class ClientPart {
	private final Renderer renderer;
	private InputSupplier inputSupplier;
	private ClientController controller;
	private final GameNetworkClient networkClient;
	private final GameWorld gameWorld;
	private final LinkedList<ControlPacketObserver> controlPacketObservers = new LinkedList<>();


	public ClientPart(boolean isClientControllable, InputSupplier inputSupplier, SpriteBatch batch) {
		this.inputSupplier = inputSupplier;

		networkClient = new GameNetworkClient();
		int clientId = networkClient.getClientId();
		controlPacketObservers.add(networkClient);

		gameWorld = new GameWorld();
		gameWorld.createPlayer(clientId);

		controller = new ClientController(clientId, isClientControllable, gameWorld, networkClient, inputSupplier);
		controlPacketObservers.add(controller);

		renderer = Config.DEBUG_RENDER ? new DebugRenderer(batch, gameWorld.getWorld())
				: new StandardRenderer(batch, gameWorld);
	}

	public void processClientSide() {
		ControlPacket controlPacket;
		while((controlPacket = networkClient.getReceivedControlPackets().poll()) != null){
			for (ControlPacketObserver controlPacketObserver : controlPacketObservers) {
				controlPacketObserver.processPacket(controlPacket);
			}
		}
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
