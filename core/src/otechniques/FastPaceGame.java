package otechniques;

import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class FastPaceGame extends ApplicationAdapter {
	/**
	 * number of parts which screen is divided into. Default 1(only server)
	 */
	private static int numOfScreenParts = 1;
	private HashMap<Integer, ClientPart> clientParts = new HashMap<>();
	private ServerPart serverPart;

	@Override
	public void create() {
		serverPart = new ServerPart();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverPart.addClient(Config.CONTROLLABLE_PLAYER_ID);
		createClientPart(Config.CONTROLLABLE_PLAYER_ID); // default client part for player-controller player
	
	}

	@Override
	public void render() {
		serverPart.processServerSide();
		for (ClientPart part : clientParts.values()) {
			part.processClientSide();
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / numOfScreenParts, Gdx.graphics.getHeight());
		clientParts.get(Config.CONTROLLABLE_PLAYER_ID).renderGraphics(); // first render controllable client

		Gdx.gl.glViewport(Gdx.graphics.getWidth() / numOfScreenParts, 0, Gdx.graphics.getWidth() / numOfScreenParts,
				Gdx.graphics.getHeight());
		serverPart.renderGraphics(); // then render server

		int screenShiftFactor = 2;	//2 parts of the screen are reserved for controlled client and server
		for (ClientPart part : clientParts.values()) {
			if(part.clientId != Config.CONTROLLABLE_PLAYER_ID){
				Gdx.gl.glViewport((Gdx.graphics.getWidth() / numOfScreenParts) * (screenShiftFactor++), 0,
						Gdx.graphics.getWidth() / numOfScreenParts, Gdx.graphics.getHeight());
				part.renderGraphics();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		for (ClientPart part : clientParts.values()) {
			part.resize(width, height);
		}
		serverPart.resize(width, height);
	}

	@Override
	public void dispose() {
		for (ClientPart part : clientParts.values()) {
			part.dispose();
		}
		serverPart.dispose();
	}

	public void createClientPart(int clientId) {
		clientParts.put(clientId, new ClientPart(clientId));
		Gdx.graphics.setDisplayMode(++numOfScreenParts*400, 400, false);	//TODO magic numbers
	}

	public static float getNumOfScreenParts() {
		return numOfScreenParts;
	}

}
