package otechniques.input;

import java.util.Set;

public interface InputSupplier {
	
	public Set<Integer> getKeysPressed();

	public Set<Integer> getKeysReleased();
	
	public boolean isButtonPressed(int button);
	
	public void refresh();
	
}
