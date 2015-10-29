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
	private HashMap<Integer, ClientPart> nonControllableClients = new HashMap<>();
	private ClientPart controllableClient;
	private ServerPart serverPart;

	@Override
	public void create() {
		serverPart = new ServerPart();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverPart.addClient(1);	//TODO 1 to id kierowanego gracza
		createClientPart(1, true); // default client part for player-controller player
		
		serverPart.addClient(5);
		createClientPart(5, false);
		
	}

	@Override
	public void render() {
		serverPart.processServerSide();
		controllableClient.processClientSide();
		for (ClientPart part : nonControllableClients.values()) {
			part.processClientSide();
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / numOfScreenParts, Gdx.graphics.getHeight());
		controllableClient.renderGraphics(); // first render controllable client

		Gdx.gl.glViewport(Gdx.graphics.getWidth() / numOfScreenParts, 0, Gdx.graphics.getWidth() / numOfScreenParts,
				Gdx.graphics.getHeight());
		serverPart.renderGraphics(); // then render server

		int screenShiftFactor = 2;	//2 parts of the screen are reserved for controlled client and server
		for (ClientPart part : nonControllableClients.values()) {		
				Gdx.gl.glViewport((Gdx.graphics.getWidth() / numOfScreenParts) * (screenShiftFactor++), 0,
						Gdx.graphics.getWidth() / numOfScreenParts, Gdx.graphics.getHeight());
				part.renderGraphics();		
		}
	}

	@Override
	public void resize(int width, int height) {
		for (ClientPart part : nonControllableClients.values()) {
			part.resize(width, height);
		}
		serverPart.resize(width, height);
	}

	@Override
	public void dispose() {
		for (ClientPart part : nonControllableClients.values()) {
			part.dispose();
		}
		serverPart.dispose();
	}

	public void createClientPart(int clientId, boolean isClientControllable) {
		ClientPart newClientPart = new ClientPart(clientId, isClientControllable);	
		Gdx.graphics.setDisplayMode(++numOfScreenParts*400, 400, false);	//TODO magic numbers
		
		if(isClientControllable){
			this.controllableClient = newClientPart;
		}else{
			nonControllableClients.put(clientId, newClientPart);
		}
	}

	public static float getNumOfScreenParts() {
		return numOfScreenParts;
	}

}
