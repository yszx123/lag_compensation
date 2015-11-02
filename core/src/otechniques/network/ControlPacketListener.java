package otechniques.network;

import java.util.concurrent.LinkedBlockingDeque;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import otechniques.Config;
import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.NewPlayerPacket;


public class ControlPacketListener extends Listener{

	private final LinkedBlockingDeque<ControlPacket> receivedPackets;
	private int lastConnectionId = -1; //not conntected yet
	
	public ControlPacketListener(LinkedBlockingDeque<ControlPacket> receivedControlPackets) {
		super();
		this.receivedPackets = receivedControlPackets;	
	}	
	
	@Override
	public void received (Connection connection, Object object) {
		if(object.getClass().getSuperclass() != ControlPacket.class){	//poprawic, nie zadziala w rpzypadku dlugiej linii dziedziczenia
			return;
		}
		
		ControlPacket packet = (ControlPacket)object;
		if(!packet.runId.equals(Config.runId)){
			return;
		}
		
		receivedPackets.add(packet);
    }
	
	@Override
	public void connected(Connection connection){
		receivedPackets.add(new NewPlayerPacket(Config.runId, connection.getID()));
		lastConnectionId = connection.getID();
	}
	
	public int getLastConnectionId(){
		return lastConnectionId;
	}
}
