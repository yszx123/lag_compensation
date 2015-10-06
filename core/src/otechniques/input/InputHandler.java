package otechniques.input;



import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;



public class InputHandler extends InputAdapter {
	
	private Set<Integer> keysPressed;
	
	public InputHandler() {
		keysPressed = new HashSet<>();
	}

	@Override
	public boolean keyDown(int keycode) {
		keysPressed.add(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {	
		keysPressed.remove(keycode);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		//Vector3 mousePosition = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		//scheduler.addPacket(new MousePositionPacket(mousePosition));
		return false;
	}

	public Integer[] getKeysPressed() {
		return keysPressed.toArray(new Integer[keysPressed.size()]);
	}
	
	public void setupInputHandler(){
		Gdx.input.setInputProcessor(this);
	}
	
	
}