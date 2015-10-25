package otechniques.input;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {

	private Set<Integer> keysPressed;
	private Set<Integer> keysReleased;

	public InputHandler() {
		keysPressed = new HashSet<>();
		keysReleased = new HashSet<>();
	}

	@Override
	public boolean keyDown(int keycode) {
		keysPressed.add(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		keysPressed.remove(keycode);
		keysReleased.add(keycode);
		return true;
	}
	
	public Set<Integer> getKeysPressed() {
		return keysPressed;
	}

	public Set<Integer> getKeysReleased() {
		return keysReleased;
	}

	public void setupInputHandler() {
		Gdx.input.setInputProcessor(this);
	}

	public void refresh() {
		keysReleased.clear();
	}

}