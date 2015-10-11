package otechniques;

public class Config {
	public static float PLAYER_BODY_SIZE =5f; //TODO zmienic na 0.5 i przeskalowac kamere
	public static final int TCP_PORT = 54555;
	public static final int UDP_PORT = 54777;
	public static final long CLIENT_PING = 200;	//latency in packets sending, in milliseconds
	public static final int CLIENT_CONNECT_TIMEOUT = 5000;
	public static final String SERVER_HOST = "localhost";
	public static final int SERVER_PACKET_BUFFER_SIZE = 10;
	public static final boolean DEBUG_RENDER = true;
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;
	public static final float CLIENT_PHYSICIS_TIMESTEP = 1/60f;	
	public static final float SERVER_PHYSICS_TIMESTEP = 1/60f;
	public static long SERVER_PING = 200;
	public static float PLAYER_SPEED = 100;
	
	public static final boolean SERVER_RECONCILIATION = true;
	public static boolean CLIENT_SIDE_PREDICTION = true;
}
