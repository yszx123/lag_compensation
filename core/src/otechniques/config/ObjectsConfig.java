package otechniques.config;

public class ObjectsConfig {

	public static final float GRENADE_SIZE = 0.25f;
	public static final float GRENADE_LINEAR_DAMPING = 1.5f;
	public static final float GRENADE_EXPLOSION_LATENCY = 2.5f;
	public static final float GRENADE_EXPLOSION_POWER = 180f;
	public static final int GRENADE_FRAGS_COUNT = 60;
	public static final float GRENADE_PARTICLE_LIVING_TIME = 1.1f;
	public static final float GRENADE_THROW_POWER = 10f;
	
	private static final int ROUNDS_PER_MINUTE = 600;
	public static final int SHOOTING_TIMESPAN_MS = (60*1000) / ROUNDS_PER_MINUTE;
	public static final float BULLET_SPEED = 320f;
	
	public static final int PLAYER_STARTING_POS = 10;
	public static float PLAYER_BODY_SIZE = 0.75f; 
	public static float PLAYER_SPEED = 10;
	
	
	
	public static final float TRASH_SIZE = 0.12f;
	public static final float TRASH_SIZE_VARIATION_FACTOR = 0.07f;
	public static final float TRASH_RESTITUTION = 0.1f;
	public static final float TRASH_FRICTION = 1f;	
	public static final float TRASH_DESTROY_VELOCITY_THRESHOLD = 80f;
	public static final float TRASH_LINEAR_DAMPING = 6f;
	public static final float TRASH_LINEAR_DAMPING_VARIATION_FACTOR = 3.8f;
	public static final float TRASH_DENSITY = 0.1f;
	public static final float TRASH_ANGULAR_DAMPING = 0.6f;
	public static final int TRASH_NUM = 15;
	
	public static int WALL_SIZE = 10;

	
	
}
