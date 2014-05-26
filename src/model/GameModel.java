package model;

import java.util.*;

import model.BonusObject.Bonus;
import model.MovableObject.Team;
import model.Tank.Difficulty;

public class GameModel {
    
    public enum ModelType {
        INFINITE, CAMPAIGN
    }
    
    private final double BONUS_PROBABILITY = 0.0003;
    private final int BONUS_RADIUS = 10;
    
    public static final int DISCRETE_FACTOR = 31;
    private static final int SCORE_PER_TICK = 0;
    private static final int DEFAULT_DELETED_TANKS_LIFETIME = 50;
    private EnumMap<Difficulty, Integer> pointsForBotKill;
    public static final int MAX_DIST_FOR_SEARCH = 100;
    protected static final Random GENERATOR = new Random();
    
    protected DiscreteMap map;
    private Map<Integer, ImmovableObject> immovableObjects;
    private Map<Integer, Projectile> projectiles;
    private Map<Integer, Tank> tanks;
    private Map<Integer, BonusObject> bonuses;
    
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
    
    public boolean isOver() {
        return false;
    }
    
    protected List<Vector2D> getRandomEmptyPositions(Vector2D pos, int dist) {
        List<Vector2D> freePositions = new ArrayList<>();
        for (int i = pos.getX() - dist * DISCRETE_FACTOR; i < pos.getX() + dist * DISCRETE_FACTOR; i += DISCRETE_FACTOR) {
            for (int j = pos.getY() - dist * DISCRETE_FACTOR; j < pos.getY() + dist * DISCRETE_FACTOR; j += DISCRETE_FACTOR) {
                Vector2D p = new Vector2D(i, j);
                if (map.isFree(p, DISCRETE_FACTOR, DISCRETE_FACTOR)) {
                    freePositions.add(p);
                }
            }
        }
        return freePositions;
    }
    
    protected List<Vector2D> getRandomEmptyPositions() {
        List<Vector2D> freePositions = new ArrayList<>();
        for (int i = 0; i < height; i += DISCRETE_FACTOR) {
            for (int j = 0; j < width; j += DISCRETE_FACTOR) {
                Vector2D pos = new Vector2D(i, j);
                if (map.isFree(pos, DISCRETE_FACTOR, DISCRETE_FACTOR)) {
                    freePositions.add(pos);
                }
            }
        }
        return freePositions;
    }
    
    protected Vector2D getRandomEmptyPosition(Vector2D pos, int dist) {
        List<Vector2D> freePositions = getRandomEmptyPositions(pos, dist);
        if (freePositions.isEmpty()) {
            return null;
        }
        return freePositions.get(GENERATOR.nextInt(freePositions.size()));
    }
    
    protected Vector2D getRandomEmptyPosition() {
        List<Vector2D> freePositions = getRandomEmptyPositions();
        if (freePositions.isEmpty()) {
            return null;
        }
        return freePositions.get(GENERATOR.nextInt(freePositions.size()));
    }
    
    public void addRandomTrees(int numberOfTrees) throws ModelException {
        List<Vector2D> freePositions = getRandomEmptyPositions();
        Set<Vector2D> treePositions = new HashSet<>();
        numberOfTrees = Math.min(freePositions.size(), numberOfTrees);
        while (treePositions.size() < numberOfTrees) {
            treePositions.add(freePositions.get(GENERATOR.nextInt(freePositions.size())));
        }
        for (Vector2D position : treePositions) {
            Tree tree = new Tree(freeID++, position, GameObjectDescription.getRandomTree());
            map.add(tree);
            immovableObjects.put(tree.getID(), tree);
        }
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        result.addAll(bonuses.values());
        return result;
    }
    
    public void tick() throws ModelException {
        botsMakeTurn();
        moveProjectiles();
        moveTanks();
        
        score += SCORE_PER_TICK;
        
        updateDeletedTanks();
        
        if (GENERATOR.nextDouble() < BONUS_PROBABILITY) {
            Vector2D pos = getRandomEmptyPosition(getPlayerTank().getPosition(), BONUS_RADIUS);
            if (pos != null) {
                pos = pos.add(GENERATOR.nextInt(DISCRETE_FACTOR - BonusObject.getSize()), GENERATOR.nextInt(DISCRETE_FACTOR - BonusObject.getSize()));
                addImmovableObject(pos, GameObjectDescription.BONUS);
            }
        }
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
        bonuses = new HashMap<>();
        deletedTanks = new LinkedList<>();
        score = 0;
    }
    
    public Collection<deletedTank> getDeletedTanks() {
		return deletedTanks;
	}

    public void addImmovableObject(Vector2D pos, GameObjectDescription d) throws ModelException {
        ImmovableObject obj;
        switch (d) {
        case GRASS:
        case GROUND:
            obj = new ImmovableObject(freeID++, pos, d);
            immovableObjects.put(obj.getID(), obj);
            return;
        case BONUS:
            Bonus b = Bonus.values()[GENERATOR.nextInt(Bonus.values().length)];
            obj = new BonusObject(freeID++, pos, d, b);
            map.add(obj);
            bonuses.put(obj.getID(), (BonusObject)obj);
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
    
	public void addImmovableObject(int i, int j, GameObjectDescription d) throws ModelException {
	    Vector2D pos = new Vector2D(DISCRETE_FACTOR * i, DISCRETE_FACTOR * j);
	    addImmovableObject(pos, d);
    }
    
    private void botsMakeTurn() throws ModelException {
        for (Bot bot : bots.values()) {
            bot.makeTurn();
        }
    }
    
    private Tank addTank(Team team, int delay, Vector2D position) throws ModelException {
        Tank tank = null;
        if (map.isFree(position, Tank.getSize(), Tank.getSize())) {
            tank = new  Tank(freeID++, position, team, delay);
            map.add(tank);
            tanks.put(tank.getID(), tank);
        }
        if (tank == null) {
            throw new ModelException("Cannot add tank at position" + position.toString());
        }
        return tank;
    }
    
    // only Bot class can call this method
    public Tank addTank(Team team, Difficulty difficulty, Vector2D position) throws ModelException {
    	Tank tank = null;
        if (map.isFree(position, Tank.getSize(), Tank.getSize())) {
            tank = new  Tank(freeID++, position, team, difficulty);
            map.add(tank);
            tanks.put(tank.getID(), tank);
        }
        if (tank == null) {
            throw new ModelException("Cannot add tank(bot) at position" + position.toString());
        }
        return tank;
    }
    
    protected void addBot(Team team, Difficulty difficulty, Vector2D position) throws ModelException {
        Bot bot = new Bot(this, position, difficulty);
        bots.put(bot.getTankID(), bot);
    }
    
    public void addPlayer(Team team, int delay, Vector2D position) throws ModelException {
        Tank tank;
        try {
            tank = addTank(team, delay, position);
        }
        catch (ModelException e) {
            throw new ModelException("Cannot add player at position " + position.toString());
        }
        playerID = tank.getID();
    }
    
    public void movePlayer(Direction d) {
        if (tanks.containsKey(playerID)) {
            moveTank(playerID, d);
        }
    }
    
    public void shootPlayer() throws ModelException {
        if (tanks.containsKey(playerID)) {
            shoot(playerID);
        }
    }
    
    public boolean canBotMove(Tank t, Vector2D v) {
        return map.canBotMove(t, v);
    }
   
    public void moveTank(int ID, Direction direction) {
        moveTank(tanks.get(ID), direction);
    }
    
    public void moveTank(Tank t, Direction direction) {
    	t.setMoveDirection(direction);
    	t.setOrientation(direction);
    }
    
    public void shoot(Tank t) throws ModelException {
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
    
    private GameObject getGameObject(int id) throws ModelException {
        if (immovableObjects.containsKey(id)) {
            return immovableObjects.get(id);
        }
        if (tanks.containsKey(id)) {
            return tanks.get(id);
        }
        if (projectiles.containsKey(id)) {
            return projectiles.get(id);
        }
        if (bonuses.containsKey(id)) {
            return bonuses.get(id);
        }
        throw new ModelException("Invalid game object ID");
    }
    
    private void attack(Projectile projectile) throws ModelException {
        int id = map.getBlockID(projectile, projectile.getDirection().getMove());
        // quite unsafe code
        if (id == DiscreteMap.EMPTY_ID) {
            id = map.getBlockID(projectile, new Vector2D(0, 0));
        }
        
        if (id != DiscreteMap.EMPTY_ID) {
            GameObject obj = getGameObject(id);
            if (obj.attacked(projectile)) {
                deleteObject(obj);
            }
        }
    }
    
    public void shoot(int ID) throws ModelException {
        shoot(tanks.get(ID));
    }
    
    public Map<Vector2D, Vector2D> getAccessibleCells(Tank tank) {
        return map.getAccessibleCells(tank, MAX_DIST_FOR_SEARCH);
    }
    
    private void moveProjectiles() throws ModelException {
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
                    GameObject obj = getGameObject(id);
                    
                    // !!! to prevent iterated list from changing !!!
                    if (projectiles.containsKey(id)) {
                        if (obj.attacked(projectile)) {
                            toDelete.add(id);
                            continue;
                        }
                    }
                    
                    if (obj.attacked(projectile)) {
                        deleteObject(obj);
                    }
                }
            }
        }
        // deleting projectiles only here
        for (Integer id : toDelete) {
            deleteObject(projectiles.get(id));
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
    
    private void moveTanks() throws ModelException {
        Set<Integer> toDelete = new HashSet<Integer>();
        for (Tank tank : tanks.values()) {
            
            if (tank.getDirection() == Direction.NONE || !tank.makeTurn()) {
                continue;
            }
            
            // side effect of global bonuses...
            if (tank.getHealth() <= 0) {
                toDelete.add(tank.getID());
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
                    deleteObject(projectile);

                    if (tank.attacked(projectile)) {
                        map.remove(tank);
                        toDelete.add(tank.getID());
                    }
                }
                if (bonuses.containsKey(id)) {
                    BonusObject bonus = bonuses.get(id); 
                    // not the best idea to write it here
                    if (tank.getID() == playerID) {
                        bonus.effect(this, tank);
                    }
                    deleteObject(bonus);
                    if (tank.getHealth() <= 0) {
                        toDelete.add(tank.getID());
                        continue;
                    } else {
                        map.remove(tank);
                        tank.setPosition(pos.add(deltaMove));
                        map.add(tank);
                    }
                }
            }
        }
        
        // deleting tanks only here
        for (Integer id : toDelete) {
            deleteObject(tanks.get(id));
        }        
    }
    
    private void deleteObject(GameObject obj) throws ModelException {
        int ID = obj.getID();
        switch (obj.getDescription()) {
        case GRASS:
        case GROUND:
        case TREE:
        case PALM:
        case WATER:
        case STONE:
            map.remove(obj);
            immovableObjects.remove(ID);
            break;
        case BONUS:
            map.remove(obj);
            bonuses.remove(ID);
            break;
        case PROJECTILE:
            map.remove(obj);
            projectiles.remove(ID);
            break;
        case TANK:
            deletedTanks.add(new deletedTank(obj.getPosition(), DEFAULT_DELETED_TANKS_LIFETIME));
            if (bots.containsKey(ID)) {
                score += pointsForBotKill.get(bots.get(ID).getDifficulty());
                bots.remove(ID);
            }
            map.remove(obj);
            tanks.remove(ID);
            break;
        default:
            throw new ModelException("Removing unknown object");
        }
    }

	public Stack<Direction> getRandomPath(MovableObject obj) {
		return map.getRandomPath(obj, MAX_DIST_FOR_SEARCH);
	}    
}
