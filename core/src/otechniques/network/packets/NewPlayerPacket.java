package otechniques.network.packets;

import java.util.UUID;

public class NewPlayerPacket extends ControlPacket{
	
	public int playerId;
	
	@SuppressWarnings("unused")
	private NewPlayerPacket(){}
	
	public NewPlayerPacket(String programRunId, int playerId){
		super(programRunId);
		this.playerId = playerId;
	}
}	
