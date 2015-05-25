package otechniques.objects;

public class GameWorld {
	private Player player;
	
	public GameWorld() {
		player = new Player();
	}
	
	public Player getPlayer(){
		return player;
	}
}
