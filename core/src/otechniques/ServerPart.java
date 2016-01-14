package otechniques;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import otechniques.config.Config;
import otechniques.controllers.ServerController;
import otechniques.network.ControlPacketObserver;
import otechniques.network.packets.ControlPacket;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.render.DebugRenderer;
import otechniques.render.Renderer;
import otechniques.render.StandardRenderer;

public class ServerPart {
	private final GameNetworkServer networkServer;
	private final ServerController controller;
	private Renderer renderer;
	private final GameWorld gameWorld;
	private final LinkedList<ControlPacketObserver> controlPacketObservers = new LinkedList<>();

	public ServerPart(SpriteBatch batch) {
		networkServer = new GameNetworkServer();
		controlPacketObservers.add(networkServer);
		gameWorld = new GameWorld();
		controller = new ServerController(gameWorld, networkServer);
		controlPacketObservers.add(controller);
		renderer = Config.DEBUG_RENDER ? new DebugRenderer(batch, gameWorld) : new StandardRenderer(batch, gameWorld);
		renderer.setName("Server");
	}

	public void renderGraphics() {
		renderer.render();
	}

	public void processServerSide() {
		ControlPacket cp;
		while ((cp = networkServer.getReceivedControlPackets().poll()) != null) {
			networkServer.processPacket(cp);
			controller.processPacket(cp);
			renderer.processPacket(cp);
		}

		controller.updateGamestate(networkServer.getReceivedPackets(), Config.PHYSICS_TIMESTEP);
		networkServer.sendPackets();
	}

	public void addReceivedControlPacket(ControlPacket packet) {
		networkServer.addReceivedControlPacket(packet);
	}

	public void resize(int width, int height) {
		renderer.resize(width, height);
	}

	public void dispose() {
		gameWorld.getWorld().dispose();
	}

	public void serRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

}
