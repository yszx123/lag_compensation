package otechniques;

import otechniques.controller.ServerController;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.render.DebugRenderer;
import otechniques.render.Renderer;
import otechniques.render.StandardRenderer;

public class ServerPart {
	GameNetworkServer server;
	ServerController controller;
	Renderer renderer;
	GameWorld gameWorld;

	public ServerPart() {
		server = new GameNetworkServer();
		gameWorld = new GameWorld();
		controller = new ServerController(gameWorld, server);
		renderer = Config.DEBUG_RENDER ? new DebugRenderer(gameWorld.getWorld()) : new StandardRenderer(gameWorld);
	}

	public void renderGraphics() {
		renderer.render();
	}

	public void processServerSide() {
		controller.processControlPackets(server.getUnprocessedControlPackets());
		controller.updateGamestate(server.getUnprocessedGamestatePackets(), Config.PHYSICS_TIMESTEP);
		server.sendPackets();
	}

	public void resize(int width, int height) {
		renderer.resize(width, height);
	}

	public void dispose() {
		gameWorld.getWorld().dispose();
	}

}
