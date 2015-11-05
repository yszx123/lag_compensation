package otechniques.network;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.LagListener;

public class SelectiveLagListener extends LagListener {

	private final int packetLossRate;
	
	public SelectiveLagListener(int lagMillisMin, int lagMillisMax, int lossRatePercent, Listener listener) {
		super(lagMillisMin, lagMillisMax, listener);
		if (lossRatePercent > 100 || lossRatePercent < 0) {
			throw new IllegalArgumentException("loss rate value must be between 0 and 100%");
		}
		this.packetLossRate = lossRatePercent;
	}

	@Override
	public void queue(Runnable runnable) {
		if (MathUtils.random(100) > packetLossRate) {
			super.queue(runnable);
		}
	}
}
