package otechniques.input;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import otechniques.config.Config;

public class RandomInputSpoofer implements InputSupplier{

	private float timeSinceInputChange;
	private int[] keysPossible;

	private Set<Integer> keysPressed = new HashSet<>();
	private Set<Integer> keysReleased = new HashSet<>();

	private Random random = new Random();

	public enum InputType{
		RANDOM,
		UP_DOWN
	}
	
	public RandomInputSpoofer() {
		setInputType(InputType.RANDOM);
	}

	@Override
	public Set<Integer> getKeysPressed() {
		return keysPressed;
	}

	@Override
	public Set<Integer> getKeysReleased() {
		return keysReleased;
	}

	@Override
	public boolean isButtonPressed(int button) {
		return false;
	}
	
	@Override
	public void refresh() {
		timeSinceInputChange += Gdx.graphics.getDeltaTime();

		if (timeSinceInputChange >= Config.INPUT_SPOOFING_CHANGE_FREQ) {
			timeSinceInputChange = 0;
			keysPressed.clear();
			keysPressed.add(keysPossible[random.nextInt(keysPossible.length)]);
		}
	}
	
	public void setInputType(InputType type){
		if(type == InputType.RANDOM){
			keysPossible = new int[] { -1, -1, -1, -1, Keys.A, Keys.D, Keys.W, Keys.S };
		}
		else if(type == InputType.UP_DOWN){
			keysPossible = new int[] {Keys.W, Keys.S };
		}
		else{
			throw new IllegalArgumentException();
		}
	}


}
