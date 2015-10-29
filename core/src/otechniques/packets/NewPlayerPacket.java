package otechniques.packets;

public class NewPlayerPacket {
	
	public int playerId;
	
	@SuppressWarnings("unused")
	private NewPlayerPacket(){};
	
	public NewPlayerPacket(int playerId){
		this.playerId = playerId;
	}
}	
