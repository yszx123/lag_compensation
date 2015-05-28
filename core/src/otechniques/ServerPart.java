package otechniques;

import otechniques.controller.ServerController;
import otechniques.network.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.render.Renderer;

public class ServerPart {
	GameNetworkServer server;
	ServerController controller;
	Renderer renderer;
	
	public static final int SERVER_ID = 0; //TODO przeniesc
	
	public ServerPart(){		
		server = new GameNetworkServer();
		GameWorld world = new GameWorld();
		controller = new ServerController(world, server);
		renderer = new Renderer(world);
	}
	
	public void renderGraphics(){
		renderer.render();
	}
	
	public void processServerSide(){
		controller.processReceivedPackets(server.getUnprocessedPackets());
		server.sendPackets();
	}
			
	//TODO krok czasu brany pod uwage przy kalkulacji fizyki
	public void setTimestep(int timestep){
		
	}
}
