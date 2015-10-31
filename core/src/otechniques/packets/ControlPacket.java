package otechniques.packets;

import java.util.UUID;

public abstract class ControlPacket {

	/**
	 * Each program run has unique id, which prevents interpreting packets from
	 * another program run, in case of quick program restart
	 */
	public String runId;
	
	protected ControlPacket(){}
	
	public ControlPacket(String programRunId) {
		this.runId = programRunId;
	}
}
