package otechniques.network.packets;

public class ConfigurationControlPacket extends ControlPacket{
	
	public int minPing, maxPing;
	public boolean clientSidePrediction, serverReconciliation;
	public int packetLossRate;
	
	@SuppressWarnings("unused")
	private ConfigurationControlPacket(){}
	
	public ConfigurationControlPacket(String runId){
		super(runId);
	}
}
