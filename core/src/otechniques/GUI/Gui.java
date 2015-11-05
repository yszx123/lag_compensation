package otechniques.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import otechniques.config.Config;

public class Gui implements IGui {
	Skin skin = new Skin(Gdx.files.internal("uiskin.json"));;
	Stage stage;

	boolean drawStats = true;

	CheckBox clientSidePredictionCB;
	CheckBox serverReconciliationCB;
	CheckBox drawStatsCB;

	Slider minPingSlider, maxPingSlider;
	Slider packetLossRateSlider;

	Label minPingValueLabel, maxPingValueLabel, packetLossRateValueLabel;
	Label minPingLabel, maxPingLabel;
	Label packetLossLabel;

	public void create(SpriteBatch batch) {
		Viewport viewport = new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				new OrthographicCamera());
		stage = new Stage(viewport, batch);

		Table mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);

		clientSidePredictionCB = new CheckBox("Client side prediction", skin);
		clientSidePredictionCB.setChecked(Config.CLIENT_SIDE_PREDICTION);
		clientSidePredictionCB.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				boolean isChecked = clientSidePredictionCB.isChecked();
				Config.CLIENT_SIDE_PREDICTION = isChecked;
				if (!isChecked) {
					serverReconciliationCB.setChecked(false);
				}
			}
		});

		serverReconciliationCB = new CheckBox("Server reconciliation", skin);
		serverReconciliationCB.setChecked(Config.SERVER_RECONCILIATION);
		serverReconciliationCB.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Config.SERVER_RECONCILIATION = serverReconciliationCB.isChecked();
			}
		});

		drawStatsCB = new CheckBox("Draw stats?", skin);
		drawStatsCB.setChecked(drawStats);
		drawStatsCB.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				drawStats = drawStatsCB.isChecked();
			}
		});

		minPingLabel = new Label("Min ping", skin);
		maxPingLabel = new Label("Max ping", skin);
		packetLossLabel = new Label("Packet Loss in percents", skin);
		minPingValueLabel = new Label(Integer.toString(Config.MIN_PING), skin);
		maxPingValueLabel = new Label(Integer.toString(Config.MAX_PING), skin);
		packetLossRateValueLabel = new Label(Integer.toString(Config.PACKET_LOSS_PERCENT), skin);

		minPingSlider = new Slider(0, Config.MAX_PING, 25, false, skin);
		minPingSlider.setValue(Config.MIN_PING);
		minPingSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (minPingSlider.getValue() > maxPingSlider.getValue()) {
					maxPingSlider.setValue(minPingSlider.getValue());
				}
				minPingValueLabel.setText(Integer.toString((int) minPingSlider.getValue()));
			}
		});
		maxPingSlider = new Slider(0, Config.MAX_PING, 25, false, skin);
		maxPingSlider.setValue(Config.MAX_PING);
		maxPingSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (maxPingSlider.getValue() < minPingSlider.getValue()) {
					minPingSlider.setValue(maxPingSlider.getValue());
				}
				maxPingValueLabel.setText(Integer.toString((int) maxPingSlider.getValue()));
			}
		});

		packetLossRateSlider = new Slider(0, 100, 1, false, skin);
		packetLossRateSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				packetLossRateValueLabel.setText(Integer.toString((int) packetLossRateSlider.getValue()));
			}
		});

		mainTable.add(minPingValueLabel);
		mainTable.add(minPingSlider);
		mainTable.add(minPingLabel);
		mainTable.row();

		mainTable.add(maxPingValueLabel);
		mainTable.add(maxPingSlider);
		mainTable.add(maxPingLabel);
		mainTable.row();

		mainTable.add(packetLossRateValueLabel);
		mainTable.add(packetLossRateSlider);
		mainTable.add(packetLossLabel);
		mainTable.row();

		mainTable.add(drawStatsCB);
		mainTable.row();

		mainTable.add(clientSidePredictionCB);
		mainTable.row();

		mainTable.add(serverReconciliationCB);

	}

	public void render() {
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	public InputProcessor getInputProcessor() {
		return stage;
	}

	public boolean isClientSidePrediction() {
		return clientSidePredictionCB.isChecked();
	}

	public boolean isServerReconciliation() {
		return serverReconciliationCB.isChecked();
	}

	public int getMinPing() {
		return (int) minPingSlider.getValue();
	}

	public int getMaxPing() {
		return (int) maxPingSlider.getValue();
	}

	public boolean isDrawStats() {
		return drawStats;
	}

	public int getPacketLossRate() {
		return (int) packetLossRateSlider.getValue();
	}
}
