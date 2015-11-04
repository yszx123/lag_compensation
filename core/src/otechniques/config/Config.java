package otechniques.config;

import java.util.UUID;

public class Config {
	public static final String runId = UUID.randomUUID().toString();

	public static final int SERVER_ID = 0;

	public static final int TCP_PORT = 54555;
	public static final int UDP_PORT = 54777;

	public static final int CLIENT_CONNECT_TIMEOUT = 5000;
	public static final String SERVER_HOST = "localhost";
	public static final int SERVER_PACKET_BUFFER_SIZE = 10;
	public static final boolean DEBUG_RENDER = true;

	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;

	public static final float PHYSICS_TIMESTEP = 1 / 60f;
	public static final float PHYSICS_TIMESTEP_MS = PHYSICS_TIMESTEP * 1000;

	public static final short COLLISION_CATEGORY_PLAYER = 0x0001;
	public static final short COLLISION_CATEGORY_TRASH = 0x0002;
	public static final short COLLISION_CATEGORY_SCENERY = 0x0004;
	public static final short COLLISION_MASK_TRASH = COLLISION_CATEGORY_TRASH | COLLISION_CATEGORY_SCENERY;
	public static final short COLLISION_MASK_SCENERY = -1;
	public static final short COLLISION_MASK_PLAYER = COLLISION_CATEGORY_SCENERY;

	public static final float INPUT_SPOOFING_CHANGE_FREQ = 0.5f;

	public static final int CLIENT_PING = 200; 
	public static int SERVER_PING = 200;
	public static final float PLAYER_STATE_SENDING_FREQUENCY = 1 / 60f;

	public static boolean SERVER_RECONCILIATION = true;
	public static boolean CLIENT_SIDE_PREDICTION = true;
		
	public static final int RENDER_PART_SIZE_PX = 400;
	public static int CAMERA_VIEW_WIDTH = 20;
	public static int CAMERA_VIEW_HEIGHT = 20;

}
