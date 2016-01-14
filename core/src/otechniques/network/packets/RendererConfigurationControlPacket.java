package otechniques.network.packets;

public class RendererConfigurationControlPacket extends ControlPacket {

	public boolean drawStats;

	@SuppressWarnings("unused")
	private RendererConfigurationControlPacket() {
	}

	public RendererConfigurationControlPacket(String runId) {
		super(runId);
	}
	
	
}
