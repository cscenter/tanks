package model;

import java.util.*;

public class GameModel {
    
    public static final int DISCRETE_FACTOR = 3;
    public static final int SCORE_PER_TICK = 10;
    public static final int SCORE_PER_KILL = 500;
    
    private DiscreteMap map;
    private Map<Integer, ImmovableObject> immovableobjects;
    private Map<Integer, Projectile> projectiles;
    private Map<Integer, Tank> tanks;
    private int freeID;
    private int width;
    private int height;
    private int score;
    private Random r = new Random();
    
    private int playerID;
    private Collection<Bot> bots;
    
    
    public void debugprint() {
        System.out.print("Immovable objects:");
        System.out.println(immovableobjects.size());
        System.out.print("Tanks: ");
        System.out.println(tanks.size());
        System.out.print("Projectiles: ");
        System.out.println(projectiles.size());
        map.debugprint();
    }
    
    public void start() {
        addPlayer(1);
        addBot(2);
        addBot(2);
        addBot(2);
    }
        
    public GameModel() {
        rebuild(0, 0);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getPlayerHealth() {
        return tanks.get(playerID).getHealth();
    }

       
    public Collection<GameObject> getGameObjects() {
        Collection<GameObject> result = new ArrayList<GameObject>();
        result.addAll(immovableobjects.values());
        result.addAll(tanks.values());
        result.addAll(projectiles.values());
        return result;
    }
    
    public void tick() {
        botsMakeTurn();
        moveProjectiles();
        moveTanks();
        
        while (bots.size() < 3) {
            addBot(2);
            score += SCORE_PER_KILL;
        }
        score += SCORE_PER_TICK;
    }
    
    public boolean isPlayerAlive() {
        return tanks.containsKey(playerID);
    }
    
    public void rebuild(int w, int h) {
        width = DISCRETE_FACTOR * w;
        height = DISCRETE_FACTOR * h;
        freeID = DiscreteMap.EMPTY_ID + 1;
        immovableobjects = new HashMap<Integer, ImmovableObject>();
        projectiles = new HashMap<Integer, Projectile>();
        tanks = new HashMap<Integer, Tank>();
        map = new DiscreteMap(width, height);
        bots = new ArrayList<Bot>();
        score = 0;
    }
    
    public void addImmovableObject(int i, int j, GameObjectDescription d) {
        Vector2D pos = new Vector2D(DISCRETE_FACTOR * i, DISCRETE_FACTOR * j);
        ImmovableObject obj = new ImmovableObject(freeID++, pos, DISCRETE_FACTOR, DISCRETE_FACTOR, d);
        map.add(obj);
        immovableobjects.put(obj.getID(), obj);
    }
    
    private void botsMakeTurn() {
        for (Bot bot : bots) {
            bot.makeTurn();
        }
    }
    
    private Tank addTank(int team) {
        List<Vector2D> freePositions = new ArrayList<Vector2D>();
        Vector2D s = new Vector2D(0, 0);
        for (int i = 0; i < height; i += DISCRETE_FACTOR) {
            for (int j = 0; j < width; j += DISCRETE_FACTOR) {
                Vector2D pos = new Vector2D(i, j);
                if (map.isFree(pos, DISCRETE_FACTOR, DISCRETE_FACTOR)) {
                    freePositions.add(pos);
                }
            }
        }
        if (freePositions.isEmpty()) {
            return null;
        } else {
            Tank tank = new Tank(freeID++, freePositions.get(r.nextInt(freePositions.size())), team, s);
            map.add(tank);
            tanks.put(tank.getID(), tank);
            return tank;
        }
    }
    
    private void addBot(int team) {
        Tank tank = addTank(team);
        if (tank != null) {
            bots.add(new Bot(this, tank));
        }
    }
    
    private void addPlayer(int team) {
        Tank tank = addTank(team);
        if (tank != null) {
            playerID = tank.getID();
        }
    }
    
    public void movePlayer(Direction d) {
        moveTank(playerID, d);
    }
    
    public void shootPlayer() {
        shoot(playerID);
    }
    
    public void moveTank(int ID, Direction d) {
        moveTank(ID, d.getMove());
    }
        
    public boolean canTankMove(int ID, Vector2D v) {
        return map.canMove(tanks.get(ID), v);
    }

    public boolean canTankMove(Tank t, Vector2D v) {
        return map.canMove(t, v);
    }
   
    public void moveTank(int ID, Vector2D v) {
        tanks.get(ID).setSpeed(v);
        tanks.get(ID).setOrientation(v);
    }
    
    public void moveTank(Tank t, Vector2D v) {
        t.setSpeed(v);
        t.setOrientation(v);
    }
    
    public void shoot(Tank t) {
        Projectile projectile = t.shoot(freeID++);
        if (map.isFreeForProjectile(projectile.getPosition(), Projectile.SIZE, Projectile.SIZE)) {
            projectiles.put(projectile.getID(), projectile);
            map.add(projectile);
        }
    }
    
    public void shoot(int ID) {
        shoot(tanks.get(ID));
    }
    
    public Map<Vector2D, Vector2D> getAccessibleCells(Tank tank) {        
        return map.getAccessibleCells(tank);
    }
    
    private void moveProjectiles() {
        Set<Integer> toDelete = new HashSet<Integer>();
        for (Projectile projectile : projectiles.values()) {
            if (projectile.isJustCreated()) {
                projectile.setCreateStatus(false);
                continue;
            }
            
            Vector2D pos = projectile.getPosition();
            Vector2D destination = pos.add(projectile.getSpeed());
            Vector2D deltaMove = destination.sub(pos).normalize();
                        
            int w = projectile.getWidth();
            int h = projectile.getHeight();
            
            while (!destination.equals(pos) && map.isFreeForProjectile(pos.add(deltaMove), w, h)) {
                pos = pos.add(deltaMove);
                deltaMove = destination.sub(pos).normalize();
            }
            
            if (!pos.equals(projectile.getPosition())) {
                map.remove(projectile);
                projectile.setPosition(pos);
                map.add(projectile);
            }
            
            if (!destination.equals(pos)) {
                
                toDelete.add(projectile.getID());
                // Should be changed in case its not a tank
                int id = map.getBlockID(projectile, deltaMove);
                if (id != DiscreteMap.EMPTY_ID) {
                
                    if (tanks.containsKey(id)) {
                        if (tanks.get(id).attacked(projectile)) {
                            deleteTank(id);
                        }
                    }
                    if (projectiles.containsKey(id)) {
                        toDelete.add(id);
                    }
                }
            }
        }
        // deleting projectiles only here
        for (Integer id : toDelete) {
            deleteProjectile(id);
        }
    }
    
    public List<Tank> getEnemies(int team) {
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
            Vector2D pos = tank.getPosition();
            Vector2D destination = pos.add(tank.getSpeed());
            Vector2D deltaMove = destination.sub(pos).normalize();
            
            int w = tank.getWidth();
            int h = tank.getHeight();
            
            while (!destination.equals(pos) && map.canMove(tank, deltaMove)) {
                pos = pos.add(deltaMove);
                deltaMove = destination.sub(pos).normalize();
            }
            
            map.remove(tank);
            tank.setPosition(pos);
            map.add(tank);
            
            if (!destination.equals(pos)) {        
                int id = map.getBlockID(tank, deltaMove);
                if (projectiles.containsKey(id)) {
                    Projectile projectile = projectiles.get(id);
                    // deletine projectile
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
    
    
    private void deleteTank(Tank tank) {
        deleteTank(tank.getID());
    }
    
    private void deleteTank(int ID) {
        Bot bot = null;
        for (Bot b : bots) {
            if (b.getTankID() == ID) {
                bot = b;
                break;
            }
        }
        if (bot != null) {
            bots.remove(bot);
        }
        map.remove(tanks.get(ID));
        tanks.remove(ID);
    }
    
    private void deleteProjectile(Projectile projectile) {
        deleteProjectile(projectile.getID());
    }
    
    private void deleteProjectile(int ID) {
        map.remove(projectiles.get(ID));
        projectiles.remove(ID);
    }    
    
}
