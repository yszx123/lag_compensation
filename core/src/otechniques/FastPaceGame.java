package otechniques;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class FastPaceGame extends ApplicationAdapter {
	ClientPart clientPart;
	ServerPart serverPart;

	@Override
	public void create () {	
		serverPart = new ServerPart();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		clientPart = new ClientPart(1);	
	}

	@Override
	public void render () {
		serverPart.processServerSide();
		clientPart.processClientSide();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//left side of the screen
		Gdx.gl.glViewport( 0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() );
	    clientPart.renderGraphics(); 
	    //right side
	    Gdx.gl.glViewport( Gdx.graphics.getWidth()/2, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() );
	    serverPart.renderGraphics();	
	}
	
	@Override
	public void resize(int width, int height) {
		clientPart.resize(width, height);
		serverPart.resize(width, height);
	}

}
