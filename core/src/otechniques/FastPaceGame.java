package otechniques;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import otechniques.GUI.Gui;
import otechniques.config.Config;
import otechniques.input.InputHandler;
import otechniques.input.InputSupplier;
import otechniques.input.RandomInputSpoofer;
import otechniques.network.packets.ConfigurationControlPacket;
import otechniques.network.packets.RendererConfigurationControlPacket;

public final class FastPaceGame extends ApplicationAdapter {
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
	private SpriteBatch batch;
	private final ArrayList<ClientPart> nonControllableClients = new ArrayList<>();
	private ClientPart controllableClient;
	private ServerPart serverPart;
	private Gui gui;
	private static int numOfScreenParts = 1;
	private boolean isGuiOpened;

	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(inputMultiplexer);

		createServerPart();

		createGUI();
		addControlInputHandler();

		createClientPart(true);
		createClientPart(false);

		configureSubParts();
		configureRenderer();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (isGuiOpened) {
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			gui.render();
			return;
		}

		serverPart.processServerSide();

		if (controllableClient != null) {
			controllableClient.processClientSide();
		}

		for (ClientPart part : nonControllableClients) {
			part.processClientSide();
		}

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

	public Integer[] getNonControllableIds() {
		Integer[] ids = new Integer[nonControllableClients.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = nonControllableClients.get(i).getClientId();
		}
		return ids;
	}

	private void createClientPart(boolean isClientControllable) {
		InputSupplier inputSupplier;
		if (isClientControllable) {
			inputMultiplexer.addProcessor((InputProcessor) (inputSupplier = new InputHandler()));
		} else {
			inputSupplier = new RandomInputSpoofer();
		}

		ClientPart newClientPart = new ClientPart(isClientControllable, inputSupplier, batch);

		Gdx.graphics.setDisplayMode(++numOfScreenParts * Config.RENDER_PART_SIZE_PX, Config.RENDER_PART_SIZE_PX, false);
		if (isClientControllable) {
			this.controllableClient = newClientPart;
		} else {
			nonControllableClients.add(newClientPart);
		}
	}

	private void createServerPart() {
		serverPart = new ServerPart(batch);
		;
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void createGUI() {
		gui = new Gui(this);
		gui.create(batch);
		inputMultiplexer.addProcessor(gui.getInputProcessor());
	}

	private void addControlInputHandler() {
		inputMultiplexer.addProcessor(new InputAdapter() {
			public boolean keyUp(int key) {
				if (key == Keys.F1) {
					isGuiOpened = !isGuiOpened;
					configureSubParts();
					configureRenderer();
				}else if (key == Keys.F2){
					gui.setDrawStats(!gui.isDrawStats());
					configureRenderer();
				}
				return false;
			}
		});
	}

	private void configureSubParts() {
		ConfigurationControlPacket p = new ConfigurationControlPacket(Config.runId);
		p.autoAim = gui.isAutoAim();
		p.autoAimTargetId = gui.getAutoAimTargetId();
		p.clientSidePrediction = gui.isClientSidePrediction();
		p.serverReconciliation = gui.isServerReconciliation();
		p.minPing = gui.getMinPing();
		p.maxPing = gui.getMaxPing();
		p.packetLossRate = gui.getPacketLossRate();
		p.wasResetIssued = gui.wasResetIssued();
		serverPart.addReceivedControlPacket(p);
	}
	
	private void configureRenderer(){
		RendererConfigurationControlPacket rendererPacket = new RendererConfigurationControlPacket(Config.runId);
		rendererPacket.drawStats = gui.isDrawStats();
		serverPart.addReceivedControlPacket(rendererPacket);
	}
}
