package otechniques;

import otechniques.controller.ServerController;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.render.IRenderer;
import otechniques.render.Renderer;

public class ServerPart {
	GameNetworkServer server;
	ServerController controller;
	IRenderer renderer;
	
	public static final int SERVER_ID = 0; //TODO przeniesc
	
	public ServerPart(){		
		server = new GameNetworkServer();
		GameWorld gameWorld = new GameWorld(); 
		controller = new ServerController(gameWorld, server);
		renderer = new Renderer(gameWorld);
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
	
	public void resize(int width, int height){
		renderer.resize(width, height);
	}
}
