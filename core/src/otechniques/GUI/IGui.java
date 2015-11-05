package otechniques.GUI;

import com.badlogic.gdx.InputProcessor;

public interface IGui {
	
	public InputProcessor getInputProcessor();

	public boolean isClientSidePrediction();

	public boolean isServerReconciliation();

	public int getMinPing();

	public int getMaxPing();

	public boolean isDrawStats();

}
