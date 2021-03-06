package otechniques.network.packets;

public class ConfigurationControlPacket extends ControlPacket{
	
	public int minPing, maxPing;
	public boolean autoAim, clientSidePrediction, serverReconciliation;
	public int autoAimTargetId;
	public int packetLossRate;
	public boolean wasResetIssued;
	
	@SuppressWarnings("unused")
	private ConfigurationControlPacket(){}
	
	public ConfigurationControlPacket(String runId){
		super(runId);
	}
}
