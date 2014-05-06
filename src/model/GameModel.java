package model;

import java.util.*;

import model.MovableObject.Team;
import model.Tank.Difficulty;

public class GameModel {
    
    public static final int DISCRETE_FACTOR = 21;
    private static final int SCORE_PER_TICK = 0;
    private static final int DEFAULT_DELETED_TANKS_LIFETIME = 50;
    private EnumMap<Difficulty, Integer> pointsForBotKill;
    public static final int MAX_DIST_FOR_SEARCH = 100;
    protected static final Random GENERATOR = new Random();
    
    private DiscreteMap map;
    private Map<Integer, ImmovableObject> immovableObjects;
    private Map<Integer, Projectile> projectiles;
    private Map<Integer, Tank> tanks;
    
    private int freeID;
    private int width;
    private int height;
    protected int score;
    
    private int playerID;
    protected Map<Integer, Bot> bots;
    
    public class deletedTank {
		public Vector2D position;
    	public int timeToDisappear;
    	public deletedTank(Vector2D position, int timeToDisappear) {
			this.position = position;
			this.timeToDisappear = timeToDisappear;
		}
    }
    
    private Collection<deletedTank> deletedTanks;
    
    public void debugprint() {
        System.out.print("Immovable objects:");
        System.out.println(immovableObjects.size());
        System.out.print("Tanks: ");
        System.out.println(tanks.size());
        System.out.print("Projectiles: ");
        System.out.println(projectiles.size());
        map.debugprint();
    }
    
    public void start() throws ModelException {
        
    }
    
    protected Vector2D getRandomEmptyPosition(int w, int h) {
        List<Vector2D> freePositions = new ArrayList<Vector2D>();
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Vector2D pos = new Vector2D(i, j);
                if (map.isFree(pos, w, h)) {
                    freePositions.add(pos);
                }
            }
        }
        return freePositions.isEmpty() ? null : freePositions.get(GENERATOR.nextInt(freePositions.size()));
    }
    
    public GameModel() {
        rebuild(0, 0);
        pointsForBotKill = new EnumMap<>(Difficulty.class);
        pointsForBotKill.put(Difficulty.EASY, 200);
        pointsForBotKill.put(Difficulty.MEDIUM, 500);
        pointsForBotKill.put(Difficulty.HARD, 2000);
        pointsForBotKill.put(Difficulty.INSANE, 5000);
        pointsForBotKill.put(Difficulty.BOSS, 10000);
    }
    
    public int getScore() {
        return score;
    }
    
    public int getPlayerHealth() {
        return tanks.get(playerID).getHealth();
    }

       
    public Collection<GameObject> getGameObjects() {
        Collection<GameObject> result = new ArrayList<GameObject>();
        result.addAll(immovableObjects.values());
        result.addAll(tanks.values());
        result.addAll(projectiles.values());
        return result;
    }
    
    public void tick() throws ModelException {
        botsMakeTurn();
        moveProjectiles();
        moveTanks();
        
        score += SCORE_PER_TICK;
        
        updateDeletedTanks();
    }
    
    private void updateDeletedTanks() {
    	Iterator<deletedTank> it = deletedTanks.iterator();
        while (it.hasNext())
        {
        	deletedTank t = it.next();
        	if (t.timeToDisappear == 0) {
        		it.remove();
        	} else {
        		--t.timeToDisappear;
        	}
        }
    }
    
    public boolean isPlayerAlive() {
        return tanks.containsKey(playerID);
    }
    
    public void rebuild(int w, int h) {
        width = DISCRETE_FACTOR * w;
        height = DISCRETE_FACTOR * h;
        freeID = DiscreteMap.EMPTY_ID + 1;
        immovableObjects = new HashMap<>();
        projectiles = new HashMap<>();
        tanks = new HashMap<>();
        map = new DiscreteMap(width, height);
        bots = new HashMap<>();
        deletedTanks = new LinkedList<>();
        score = 0;
    }
    
    public Collection<deletedTank> getDeletedTanks() {
		return deletedTanks;
	}

	public void addImmovableObject(int i, int j, GameObjectDescription d) throws ModelException {
        Vector2D pos = new Vector2D(DISCRETE_FACTOR * i, DISCRETE_FACTOR * j);
        ImmovableObject obj;
        switch (d) {
        case GRASS:
        case GROUND:
        	obj = new ImmovableObject(freeID++, pos, d);
            immovableObjects.put(obj.getID(), obj);
        	return;
        case WATER:
        	obj = new ImmovableObject(freeID++, pos, d);
        	map.add(obj);
            immovableObjects.put(obj.getID(), obj);
            return;
        case PALM:
        case TREE:
            obj = new Tree(freeID++, pos, d);
            map.add(obj);
            immovableObjects.put(obj.getID(), obj);
            break;
        default:
            obj = new ImmovableObject(freeID++, pos, d);
            map.add(obj);
            immovableObjects.put(obj.getID(), obj);
        }
        obj = new ImmovableObject(freeID++, pos, GameObjectDescription.getRandomBackground());
        immovableObjects.put(obj.getID(), obj);
    }
    
    private void botsMakeTurn() {
        for (Bot bot : bots.values()) {
            bot.makeTurn();
        }
    }
    
    private Tank addTank(Team team, int delay, Vector2D position) {
        Tank tank = null;
        if (map.isFree(position, Tank.getMaxSize(), Tank.getMaxSize())) {
            tank = new  Tank(freeID++, position, team, delay);
            map.add(tank);
            tanks.put(tank.getID(), tank);
        }
        return tank;
    }
    
    // only Bot class can call this method
    public Tank addTank(Team team, Difficulty difficulty, Vector2D position) {
    	Tank tank = null;
        if (map.isFree(position, Tank.getMaxSize(), Tank.getMaxSize())) {
            tank = new  Tank(freeID++, position, team, difficulty);
            map.add(tank);
            tanks.put(tank.getID(), tank);
        }
        return tank;
    }
    
    protected void addBot(Team team, Difficulty difficulty, Vector2D position) throws ModelException {
        Bot bot = new Bot(this, position, difficulty);
        bots.put(bot.getTankID(), bot);
    }
    
    protected boolean addPlayer(Team team, int delay, Vector2D position) {
        Tank tank = addTank(team, delay, position);
        if (tank == null) {
            return false;
        }
        
        playerID = tank.getID();
        
        return true;
    }
    
    public void movePlayer(Direction d) {
        if (tanks.containsKey(playerID)) {
            moveTank(playerID, d);
        }
    }
    
    public void shootPlayer() {
        if (tanks.containsKey(playerID)) {
            shoot(playerID);
        }
    }
    
    public boolean canTankMove(int ID, Vector2D v) {
        return map.canMove(tanks.get(ID), v);
    }

    public boolean canTankMove(Tank t, Vector2D v) {
        return map.canMove(t, v);
    }
   
    public void moveTank(int ID, Direction direction) {
        moveTank(tanks.get(ID), direction);
    }
    
    public void moveTank(Tank t, Direction direction) {
    	t.setMoveDirection(direction);
    	t.setOrientation(direction);
    }
    
    public void shoot(Tank t) {
        if (t.canShoot(true)) {
            Projectile projectile = t.shoot(freeID++);
            if (map.isFreeForProjectile(projectile.getPosition(), Projectile.SIZE, Projectile.SIZE)) {
                projectiles.put(projectile.getID(), projectile);
                map.add(projectile);
            } else {
                attack(projectile);
            }
        }
    }
    
    public Tank getPlayerTank() {
        return tanks.containsKey(playerID) ? tanks.get(playerID) : null;
    }
    
    private void attack(Projectile projectile) {
        int id = map.getBlockID(projectile, projectile.getDirection().getMove());
        // quite unsafe code
        if (id == DiscreteMap.EMPTY_ID) {
            id = map.getBlockID(projectile, new Vector2D(0, 0));
        }
        
        if (id != DiscreteMap.EMPTY_ID) {
            if (immovableObjects.containsKey(id)) {
                if (immovableObjects.get(id).attacked(projectile)) {
                    deleteImmovableObject(id);
                }
            }
            if (tanks.containsKey(id)) {
                if (tanks.get(id).attacked(projectile)) {
                    deleteTank(id);
                }
            }
            if (projectiles.containsKey(id)) {
                if (projectiles.get(id).attacked(projectile)) {
                    deleteProjectile(id);
                }
            }
        }
    }
    
    public void shoot(int ID) {
        shoot(tanks.get(ID));
    }
    
    public Map<Vector2D, Vector2D> getAccessibleCells(Tank tank) {
        return map.getAccessibleCells(tank, MAX_DIST_FOR_SEARCH);
    }
    
    private void moveProjectiles() {
        Set<Integer> toDelete = new HashSet<Integer>();
        for (Projectile projectile : projectiles.values()) {
            
            if (!projectile.makeTurn()) {
                continue;
            }
            
            Vector2D pos = projectile.getPosition();
            Vector2D deltaMove = projectile.getDirection().getMove();

            int w = projectile.getWidth();
            int h = projectile.getHeight();
            
            if (map.isFreeForProjectile(pos.add(deltaMove), w, h)) {
                map.remove(projectile);
                projectile.setPosition(pos.add(deltaMove));
                map.add(projectile);
            } else {
                toDelete.add(projectile.getID());
                int id = map.getBlockID(projectile, projectile.getDirection().getMove());
                if (id != DiscreteMap.EMPTY_ID) {
                    if (immovableObjects.containsKey(id)) {
                        if (immovableObjects.get(id).attacked(projectile)) {
                            deleteImmovableObject(id);
                        }
                    }
                    if (tanks.containsKey(id)) {
                        if (tanks.get(id).attacked(projectile)) {
                            deleteTank(id);
                        }
                    }
                    if (projectiles.containsKey(id)) {
                        if (projectiles.get(id).attacked(projectile)) {
                            toDelete.add(id);
                        }
                    }
                }
            }
        }
        // deleting projectiles only here
        for (Integer id : toDelete) {
            deleteProjectile(id);
        }
    }
    
    public List<Tank> getEnemies(Team team) {
        List<Tank> enemies = new ArrayList<Tank>();
        for (Tank t: tanks.values()) {
            if (t.getTeam() != team) {
                enemies.add(t);
            }
        }
        return enemies;
    }
    
    private void moveTanks() {
        Set<Integer> toDelete = new HashSet<Integer>();
        for (Tank tank : tanks.values()) {
            
            if (tank.getDirection() == Direction.NONE || !tank.makeTurn()) {
                continue;
            }
            
            Vector2D pos = tank.getPosition();
            Vector2D deltaMove = tank.getDirection().getMove();
            
            tank.setMoveDirection(Direction.NONE);
            
            if (map.canMove(tank, deltaMove)) {
                map.remove(tank);
                tank.setPosition(pos.add(deltaMove));
                map.add(tank);
            } else {
                int id = map.getBlockID(tank, deltaMove);
                if (projectiles.containsKey(id)) {
                    Projectile projectile = projectiles.get(id);
                    // deleting projectile
                    map.remove(projectile);
                    deleteProjectile(id);

                    if (tank.attacked(projectile)) {
                        map.remove(tank);
                        toDelete.add(tank.getID());
                    }
                }
            }
        }
        
        // deleting tanks only here
        for (Integer id : toDelete) {
            deleteTank(id);
        }        
    }
    
    
    
    private void deleteTank(int ID) {
        deletedTanks.add(new deletedTank(tanks.get(ID).getPosition(), DEFAULT_DELETED_TANKS_LIFETIME));
    	
        if (bots.containsKey(ID)) {
            score += pointsForBotKill.get(bots.get(ID).getDifficulty());
            bots.remove(ID);
        }
        map.remove(tanks.get(ID));
        tanks.remove(ID);
    }
    
    private void deleteImmovableObject(int ID) {
        map.remove(immovableObjects.get(ID));
        immovableObjects.remove(ID);
    }
    
    private void deleteProjectile(int ID) {
        map.remove(projectiles.get(ID));
        projectiles.remove(ID);
    }

	public Stack<Direction> getRandomPath(MovableObject obj) {
		return map.getRandomPath(obj, MAX_DIST_FOR_SEARCH);
	}    
    
}
