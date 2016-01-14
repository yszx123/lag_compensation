package otechniques.GUI;

import com.badlogic.gdx.InputProcessor;

public interface IGui {
	
	public InputProcessor getInputProcessor();

	public boolean isClientSidePrediction();

	public boolean isServerReconciliation();
	
	public boolean isAutoAim();
	
	public int getAutoAimTargetId();

	public int getMinPing();

	public int getMaxPing();

	public boolean isDrawStats();
	
	public void setDrawStats(boolean drawStats);

}
