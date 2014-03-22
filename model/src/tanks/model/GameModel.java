package tanks.model;

import java.util.*;

public class GameModel {
    
    private static final int discreteFactor = 3;
    private DiscreteMap map;
    private Map<Integer, GameObject> gameobjects;
    private Map<Integer, Projectile> projectiles;
    private Map<Integer, Tank> tanks;
    private int freeID;
    private int width;
    private int height;
    private Random r = new Random();
    
    private Map<String, Integer> namesOfTanks;
    
    
    public void debugprint() {
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
    
    public char getLetter(int x, int y) {
        int id = map.getObjectID(new Vector2D(x, y), 1, 1);
        if (id == DiscreteMap.emptyID) {
            return ' ';
        } else {
            return gameobjects.get(id).getLetter();
        }
    }
    
    public void tick() {
        moveProjectiles();
        moveTanks();
    }
    
    public void rebuild(int w, int h) {
        width = discreteFactor * w;
        height = discreteFactor * h;
        freeID = DiscreteMap.emptyID + 1;
        gameobjects = new HashMap<Integer, GameObject>();
        projectiles = new HashMap<Integer, Projectile>();
        tanks = new HashMap<Integer, Tank>();
        map = new DiscreteMap(width, height);
        namesOfTanks = new HashMap<String, Integer>();
    }
    
    public void addImmovableObject(int i, int j, char letter) {
        Vector2D pos = new Vector2D(discreteFactor * i, discreteFactor * j);
        ImmovableObject obj = new ImmovableObject(freeID++, pos, discreteFactor, discreteFactor,  letter);
        
        map.add(obj);
        gameobjects.put(obj.getID(), obj);
    }
    
    public void addTank(String name) {
        // TODO : write new tank placement algo
        
        Vector2D s = new Vector2D(0, 0);
        for (int i = 0; i < height; i += discreteFactor) {
            for (int j = 0; j < width; j += discreteFactor) {
                Vector2D pos = new Vector2D(i, j);
                if (map.isFree(pos, discreteFactor, discreteFactor)) {
                    Tank tank = new Tank(freeID++, pos, discreteFactor, discreteFactor,  '#', s);
                    map.add(tank);
                    gameobjects.put(tank.getID(), tank);
                    tanks.put(tank.getID(), tank);
                    namesOfTanks.put(name, tank.getID());
                    return;
                }
            }
        }
    }
    
    public void moveTank(String name, Direction d) {
        tanks.get(namesOfTanks.get(name)).setSpeed(d.getMove());
        tanks.get(namesOfTanks.get(name)).setGunOrientation(d.getMove());
    }
    
    public void shoot(String name) {
        Projectile projectile = tanks.get(namesOfTanks.get(name)).shoot(freeID++);

        if (map.isFree(projectile.getPosition(), Projectile.size, Projectile.size)) {
            gameobjects.put(projectile.getID(), projectile);
            projectiles.put(projectile.getID(), projectile);
            map.add(projectile);
        }
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
            
            map.remove(projectile);
            
            while (!destination.equals(pos) && map.isFree(pos.add(deltaMove), w, h)) {
                pos = pos.add(deltaMove);
                deltaMove = destination.sub(pos).normalize();
            }
            
            if (destination.equals(pos)) {        
                projectile.setPosition(pos);
                map.add(projectile);
            } else {

                int id = map.getObjectID(pos.add(deltaMove), w, h);
                //gameobjects.get(id).attacked(); /// !!!! no implementation yet!
                /* some code here */
                // projectiles.remove(projectile.getID()); // ERROR !!!!
                toDelete.add(projectile.getID());
                gameobjects.remove(projectile.getID());
            }
        }
        for (Integer id : toDelete) {
            projectiles.remove(id);
        }
    }
    
    private void moveTanks() {
        for (Tank tank : tanks.values()) {
            Vector2D pos = tank.getPosition();
            Vector2D destination = pos.add(tank.getSpeed());
            Vector2D deltaMove = destination.sub(pos).normalize();
            
            if (destination.equals(pos)) {
                continue;
            }
            
            int w = tank.getWidth();
            int h = tank.getHeight();
            
            map.remove(tank);
            
            while (!destination.equals(pos) && map.isFree(pos.add(deltaMove), w, h)) {
                pos = pos.add(deltaMove);
                deltaMove = destination.sub(pos).normalize();
            }
            
            if (destination.equals(pos)) {        
                tank.setPosition(pos);
                map.add(tank);
            } else { /// warning
                int id = map.getObjectID(pos.add(deltaMove), w, h);
                if (projectiles.containsKey(id)) {
                    Projectile projectile = projectiles.get(id);
                    projectiles.remove(id);
                    gameobjects.remove(id);
                    tank.attacked();
                }
                /* some code here */
                tank.setPosition(pos);
                map.add(tank);
            }
        }
    }
    
}
