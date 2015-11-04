package otechniques.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


public class GUI {
	Skin skin;
	Stage stage;
	SpriteBatch batch;	//TODO spritebatch z rendera

	public void create () {
		batch = new SpriteBatch();
		stage = new Stage();
		
		// Create a table that fills the screen. Everything else will go inside this table.
		Table mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		
		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));

/*		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		skin.add("default", new BitmapFont());

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton button = new TextButton("Click me!", skin);
		mainTable.add(button);

		// Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
		// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
		// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
		// revert the checked state.
		button.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				System.out.println("Clicked! Is checked: " + button.isChecked());
				button.setText("Good job!");
			}
		});*/

		// Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
/*		  final CheckBoxStyle t = new CheckBoxStyle();
          t.font = skin.getFont("default");
          t.fontColor =new Color(0, 0, 0, 1f);
          t.disabledFontColor = new Color(0, 0, 0, 0.4f);
          t.checkboxOff = skin.getDrawable( "checkbox_off");
          t.checkboxOn = skin.getDrawable( "checkbox_on");
          skin.add("default", t);*/
		
		CheckBox cb = new CheckBox("", skin);
		cb.addListener(new ChangeListener() {

			public void changed(ChangeEvent event, Actor actor) {
				System.out.println(event.toString());
			}
		});
		mainTable.add(cb);
	}
	
	

	public void render () {
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void dispose () {
		stage.dispose();
		skin.dispose();
	}
	
	public InputProcessor getInputProcessor(){
		return stage;
	}
}
