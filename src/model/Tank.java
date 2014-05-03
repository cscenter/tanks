package model;

public class Tank extends MovableObject {

    public static final int SIZE = GameModel.DISCRETE_FACTOR - 2;
    public static final int DEFAULT_START_HEALTH = 5;
    private static final int DEFAULT_FIRE_MOVE_RATE = 10;
    private static final int DEFAULT_DAMAGE = 1;
    
    private int damage;
    private TurnDelay fireDelay;
    
    public enum Difficulty {
    	EASY (100, 8, 1, DEFAULT_DAMAGE), MEDIUM(100, 4, 2, DEFAULT_DAMAGE), HARD(50, 3, 3, DEFAULT_DAMAGE), INSANE(20, 2, 5, DEFAULT_DAMAGE), BOSS(40, 3, 7, 3);
    	
    	public int fireDelay;
    	public int moveDelay;
    	public int startHealth;
    	public int damage;
		
    	private Difficulty(int fireDelay, int moveDelay, int startHealth, int damage) {
			this.fireDelay = fireDelay;
			this.moveDelay = moveDelay;
			this.startHealth = startHealth;
			this.damage = damage;
		}
    }
    
    public Tank(int id, Vector2D p, Team team, Difficulty difficulty) {
        super(id, p, GameObjectDescription.TANK, team);
        health = difficulty.startHealth;
        speed.setDelay(difficulty.moveDelay);
        fireDelay = new TurnDelay(difficulty.fireDelay);
        this.damage = difficulty.damage;
    }
    
    public Tank(int id, Vector2D p, Team team, int delay) {
        super(id, p, GameObjectDescription.TANK, team);
        health = DEFAULT_START_HEALTH;
        speed.setDelay(delay);
        fireDelay = new TurnDelay(DEFAULT_FIRE_MOVE_RATE * delay);
        this.damage = DEFAULT_DAMAGE;
    }
    
    public Tank(int id, Vector2D p, Team team, int delay, int shootDelay) {
        super(id, p, GameObjectDescription.TANK, team);
        health = DEFAULT_START_HEALTH;
        speed.setDelay(delay);
        fireDelay = new TurnDelay(shootDelay);
        this.damage = DEFAULT_DAMAGE;
    }
    
    public boolean canShoot(boolean change) {
        return change ? fireDelay.makeTurn() : fireDelay.canMakeTurn();
    }
    
    public Projectile shoot(int freeID) {
        Vector2D pos = new Vector2D(position.getX(), position.getY());
        Vector2D or = orientation.getMove();
        pos.setX(pos.getX() + SIZE / 2 + (SIZE / 2 + 1) * or.getX());
        pos.setY(pos.getY() + SIZE / 2 + (SIZE / 2 + 1) * or.getY());
        Projectile p = new Projectile(freeID, pos, getTeam(), orientation, damage);
        return p;
    }    

    
    @Override
    public int getWidth() {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE;
    }
}
