package otechniques;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

import otechniques.GUI.GUI;
import otechniques.config.Config;
import otechniques.input.InputHandler;
import otechniques.input.InputSupplier;
import otechniques.input.RandomInputSpoofer;

public class FastPaceGame extends ApplicationAdapter {
	/**
	 * number of parts which screen is divided into. Default 1(only server)
	 */
	private static int numOfScreenParts = 1;
	private final ArrayList<ClientPart> nonControllableClients = new ArrayList<>();
	private ClientPart controllableClient;
	private ServerPart serverPart;
	private GUI gui;
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();

	@Override
	public void create() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		createGUI();
		
		createServerPart();
		
		createClientPart(true);
		createClientPart(false);
	}

	@Override
	public void render() {
		serverPart.processServerSide();

		if (controllableClient != null) {
			controllableClient.processClientSide();
		}

		for (ClientPart part : nonControllableClients) {
			part.processClientSide();
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / numOfScreenParts, Gdx.graphics.getHeight());
		if (controllableClient != null) {
			controllableClient.renderGraphics();
		}

		Gdx.gl.glViewport(Gdx.graphics.getWidth() / numOfScreenParts, 0, Gdx.graphics.getWidth() / numOfScreenParts,
				Gdx.graphics.getHeight());
		serverPart.renderGraphics();

		// 2 parts of the screen are reserved for controlled client and server
		int screenShiftFactor = 2;
		for (ClientPart part : nonControllableClients) {
			Gdx.gl.glViewport((Gdx.graphics.getWidth() / numOfScreenParts) * (screenShiftFactor++), 0,
					Gdx.graphics.getWidth() / numOfScreenParts, Gdx.graphics.getHeight());
			part.renderGraphics();
		}
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gui.render();
		
	}

	@Override
	public void resize(int width, int height) {
		if (controllableClient != null) {
			controllableClient.resize(width, height);
		}
		
		for (ClientPart part : nonControllableClients) {
			part.resize(width, height);
		}
		
		serverPart.resize(width, height);
		
		gui.resize(width, height);
	}

	@Override
	public void dispose() {
		for (ClientPart part : nonControllableClients) {
			part.dispose();
		}
		serverPart.dispose();
		
	}

	public static float getNumOfScreenParts() {
		return numOfScreenParts;
	}
	
	private void createClientPart(boolean isClientControllable) {
		InputSupplier inputSupplier;
		if(isClientControllable){
			inputMultiplexer.addProcessor((InputProcessor) (inputSupplier = new InputHandler()));
		}
		else{
			inputSupplier = new RandomInputSpoofer();
		}
		
		ClientPart newClientPart = new ClientPart(isClientControllable, inputSupplier);
		
		Gdx.graphics.setDisplayMode(++numOfScreenParts * Config.RENDER_PART_SIZE_PX, Config.RENDER_PART_SIZE_PX, false);
		if (isClientControllable) {
			this.controllableClient = newClientPart;
		} else {
			nonControllableClients.add(newClientPart);
		}
	}

	private void createServerPart(){
		serverPart = new ServerPart();;
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void createGUI(){
		gui = new GUI();
		gui.create();
		inputMultiplexer.addProcessor(gui.getInputProcessor());
	}
	
}
