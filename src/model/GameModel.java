package model;

import java.util.*;

public class GameModel {
    
    private static final int DISCRETE_FACTOR = 3;
    private DiscreteMap map;
    private Map<Integer, GameObject> gameobjects;
    private Map<Integer, Projectile> projectiles;
    private Map<Integer, Tank> tanks;
    private int freeID;
    private int width;
    private int height;
    private Random r = new Random();
    
    private int playerID;
    private Collection<Bot> bots;
    
    
    public void debugprint() {
        System.out.print("Game objects:");
        System.out.println(gameobjects.size());
        System.out.print("Tanks: ");
        System.out.println(tanks.size());
        System.out.print("Projectiles: ");
        System.out.println(projectiles.size());
        map.debugprint();
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
       
    public GameObject getGameObject(int x, int y) {
        int id = map.getObjectID(new Vector2D(x, y), 1, 1);
        return (id == DiscreteMap.EMPTY_ID) ? null : gameobjects.get(id);
    }
    
    public void tick() {
        botsMakeTurn();
        moveProjectiles();
        moveTanks();
    }
    
    public void rebuild(int w, int h) {
        width = DISCRETE_FACTOR * w;
        height = DISCRETE_FACTOR * h;
        freeID = DiscreteMap.EMPTY_ID + 1;
        gameobjects = new HashMap<Integer, GameObject>();
        projectiles = new HashMap<Integer, Projectile>();
        tanks = new HashMap<Integer, Tank>();
        map = new DiscreteMap(width, height);
        bots = new ArrayList<Bot>();
    }
    
    public void addImmovableObject(int i, int j, GameObjectDescription d) {
        Vector2D pos = new Vector2D(DISCRETE_FACTOR * i, DISCRETE_FACTOR * j);
        ImmovableObject obj = new ImmovableObject(freeID++, pos, DISCRETE_FACTOR, DISCRETE_FACTOR, d);
        
        map.add(obj);
        gameobjects.put(obj.getID(), obj);
    }
    
    private void botsMakeTurn() {
        for (Bot bot : bots) {
            bot.makeTurn();
        }
    }
    
    private Tank addTank(int team) {
        // TODO : write new tank placement algo
        
        Vector2D s = new Vector2D(0, 0);
        for (int i = 0; i < height; i += DISCRETE_FACTOR) {
            for (int j = 0; j < width; j += DISCRETE_FACTOR) {
                Vector2D pos = new Vector2D(i, j);
                if (map.isFree(pos, DISCRETE_FACTOR, DISCRETE_FACTOR)) {
                    Tank tank = new Tank(freeID++, pos, DISCRETE_FACTOR, DISCRETE_FACTOR,  GameObjectDescription.TANK, team, s);
                    map.add(tank);
                    gameobjects.put(tank.getID(), tank);
                    tanks.put(tank.getID(), tank);
                    return tank;
                }
            }
        }
        return null;
    }
    
    public int getPlayerID() {
        return playerID;
    }
    
    public void addBot(int team) {
        Tank tank = addTank(team);
        if (tank != null) {
            bots.add(new Bot(this, tank));
        }
    }
    
    public void addPlayer(int team) {
        Tank tank = addTank(team);
        if (tank != null) {
            playerID = tank.getID();
        }
    }
    
    public void moveTank(int ID, Direction d) {
        moveTank(ID, d.getMove());
    }
        
    public boolean canTankMove(int ID, Vector2D v) {
        Tank tank = tanks.get(ID);
        return !map.isAnythingElse(v.add(tank.getPosition()), tank);
    }
        
    public void moveTank(int ID, Vector2D v) {
        tanks.get(ID).setSpeed(v);
        tanks.get(ID).setOrientation(v);
    }
    
    public void shoot(int ID) {
        Projectile projectile = tanks.get(ID).shoot(freeID++);
        
        if (map.isFree(projectile.getPosition(), Projectile.SIZE, Projectile.SIZE)) {
            gameobjects.put(projectile.getID(), projectile);
            projectiles.put(projectile.getID(), projectile);
            map.add(projectile);
        }
    }
    
    public Map<Vector2D, Vector2D> getAccessibleCells(Tank tank) {        
        Map<Vector2D, Vector2D> result = new HashMap<Vector2D, Vector2D>();
        boolean visited[][] = new boolean[height][];
        for (int i = 0; i < height; ++i) {
            visited[i] = new boolean[width];
        }
        Queue<Vector2D> queue = new LinkedList<Vector2D>();
        queue.add(tank.getPosition());
        while (!queue.isEmpty()) {
        
            Vector2D p = queue.remove();
            visited[p.getX()][p.getY()] = true;
            for (Direction d : Direction.values()) {
                Vector2D tmp = p.add(d.getMove());
                if (!map.isAnythingElse(tmp, tank) &&  !visited[tmp.getX()][tmp.getY()]) {
                    result.put(tmp, p);
                    queue.add(tmp);
                }
            }
        }
        return result;
    }
    
    private void moveProjectiles() {
        Collection<Integer> toDelete = new ArrayList<Integer>();
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
            
            while (!destination.equals(pos) && map.isFree(pos.add(deltaMove), w, h)) {
                pos = pos.add(deltaMove);
                deltaMove = destination.sub(pos).normalize();
            }
            
            if (!pos.equals(projectile.getPosition())) {
                map.remove(projectile);
                projectile.setPosition(pos);
                map.add(projectile);
            }
            
            if (!destination.equals(pos)) {
                
                map.remove(projectile);
                toDelete.add(projectile.getID());
                
                int id = map.getObjectID(pos.add(deltaMove), w, h);
                if (id != DiscreteMap.EMPTY_ID) {
                    if (gameobjects.get(id).attacked(projectile)) {
                        // Should be changed in case its not a tank
                        map.remove(gameobjects.get(id));
                        deleteTank(id);
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
        Collection<Integer> toDelete = new ArrayList<Integer>();
        for (Tank tank : tanks.values()) {
            Vector2D pos = tank.getPosition();
            Vector2D destination = pos.add(tank.getSpeed());
            Vector2D deltaMove = destination.sub(pos).normalize();
            
            int w = tank.getWidth();
            int h = tank.getHeight();
            
            while (!destination.equals(pos) && !map.isAnythingElse(pos.add(deltaMove), tank)) {
                pos = pos.add(deltaMove);
                deltaMove = destination.sub(pos).normalize();
            }
            
            map.remove(tank);
            tank.setPosition(pos);
            map.add(tank);
            
            if (!destination.equals(pos)) {        
                int id = map.getObjectID(pos.add(deltaMove), w, h);
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
        gameobjects.remove(ID);
        tanks.remove(ID);
    }
    
    private void deleteProjectile(Projectile projectile) {
        deleteProjectile(projectile.getID());
    }
    
    private void deleteProjectile(int ID) {
        gameobjects.remove(ID);
        projectiles.remove(ID);
    }    
    
}
