package otechniques.input;



import java.util.HashMap;
import java.util.Map;

import otechniques.network.GameNetworkClient;
import otechniques.objects.GameWorld;
import otechniques.packets.KeyPressedPacket;
import otechniques.packets.KeyReleasedPacket;
import otechniques.packets.MouseClickedPacket;
import otechniques.packets.MouseReleasedPacket;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;



public class InputHandler extends InputAdapter {

	GameNetworkClient client;
	GameWorld gameWorld;
	
	private boolean clientPrediction;
	private Map<Integer, Boolean> keysClicked;
	
	public InputHandler(GameWorld gameWorld, GameNetworkClient client) {
		this.gameWorld = gameWorld;
		this.client = client;
		clientPrediction = false;
		keysClicked = new HashMap<Integer, Boolean>();
	}

	@Override
	public boolean keyDown(int keycode) {
		keysClicked.put(keycode, true);
		client.addPacket(new KeyPressedPacket(keycode));
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {	
		keysClicked.put(keycode, false);
        client.addPacket(new KeyReleasedPacket(keycode));
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		//Vector3 mousePosition = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		//scheduler.addPacket(new MousePositionPacket(mousePosition));
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		client.addPacket(new MouseClickedPacket(button));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		client.addPacket(new MouseReleasedPacket(button));
		return true;
	}

	public void processInput() {
		if(clientPrediction == false){
			return;
		}
		
		if(keysClicked.get(Keys.W) != null && keysClicked.get(Keys.W) == true){
			gameWorld.getPlayer().y += 1;
		}
	}
}