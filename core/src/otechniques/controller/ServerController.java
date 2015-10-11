package otechniques.controller;

import java.util.ArrayList;

import otechniques.Config;
import otechniques.ServerPart;
import otechniques.network.server.GameNetworkServer;
import otechniques.objects.GameWorld;
import otechniques.packets.InputPacket;
import otechniques.packets.Packet;
import otechniques.packets.PlayerPositionPacket;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class ServerController {
	private final GameWorld world;
	private final GameNetworkServer server;
	private long lastProcessedRequest;
	
	public ServerController(GameWorld world, GameNetworkServer server){
		this.world = world;
		this.server = server;
	}
	
	public void updateGamestate(ArrayList<Packet> receivedPackets){
		//resets velocity of player
		world.getPlayer().body.setLinearVelocity(new Vector2());
		
		//process all new packets
		for (Packet packet : receivedPackets) {	//tak naprawde powinien dokonywac apdejtu stanu gry zaraz po przeczytaniu pakietu, a nie wtedy, gdy przeczyta WSZYSTKIE pakiety. Zaklada to, ze serwer jest w stanie przetworzyc wszystkie pakiety na czas
			lastProcessedRequest = packet.sequenceNumber;
			
			if(packet instanceof InputPacket){
				InputPacket p = (InputPacket)packet;
				world.getPlayer().body.setLinearVelocity(calculateMovementVector(p.keysClicked));	
				PlayerPositionPacket positionPacket = new PlayerPositionPacket(ServerPart.SERVER_ID, lastProcessedRequest, world.getPlayer().getPosition().x, world.getPlayer().getPosition().y);
				positionPacket.clientTimestamp = p.timestamp;
				server.addPacket(positionPacket);
			}
		}
		
		//server.addPacket(new PlayerPositionPacket(ServerPart.SERVER_ID, lastProcessedRequest, world.getPlayer().getPosition().x, world.getPlayer().getPosition().y));		
		world.getWorld().step(Config.SERVER_PHYSICS_TIMESTEP, Config.VELOCITY_ITERATIONS, Config.POSITION_ITERATIONS);
	

		//TODO System.out.println("server: "+world.getPlayer().getPosition().toString());
	}
	
	private Vector2 calculateMovementVector(Integer[] keysClicked){
		Vector2 playerMovement = new Vector2();
		for (int key : keysClicked) {
			if(key == Keys.W){
				playerMovement.add(new Vector2(0, Config.PLAYER_SPEED));
			}
			else if (key == Keys.S){
				playerMovement.add(new Vector2(0,-Config.PLAYER_SPEED));
			}
			else if (key == Keys.A){
				playerMovement.add(new Vector2(-Config.PLAYER_SPEED,0));
			}
			else if (key == Keys.D){
				playerMovement.add(new Vector2(Config.PLAYER_SPEED,0));
			}			
		}
		return playerMovement;
	}
}
