package otechniques.input;

import java.util.Set;

public interface InputSupplier {
	
	public Set<Integer> getKeysPressed();

	public Set<Integer> getKeysReleased();
	
	public void refresh();
}
