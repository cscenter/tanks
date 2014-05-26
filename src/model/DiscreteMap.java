package model;

import java.awt.Rectangle;
import java.util.*;

import model.map.Quadtree;
import model.map.QuadtreeNode;

public class DiscreteMap {
    
    public static final int EMPTY_ID = 0;
    private static final int NOT_VISITED_ID = 0;
    private static final int ELEMENTS_PER_QUAD = 8;
    
    private Quadtree objects;
    
    private Map<Vector2D, Integer> visited;
    private int visitID = NOT_VISITED_ID;
    
    private final int width;
    private final int height;
    
    public DiscreteMap(int w, int h) {
        width = w;
        height = h;
        objects = new Quadtree(h, w, ELEMENTS_PER_QUAD);
        visited = new HashMap<>();
        staticRect = new Rectangle();
    }

    private void addToQuadtree(GameObject obj) {
        Vector2D pos = obj.getPosition();
        int w = obj.getWidth();
        int h = obj.getHeight();
        objects.insert(new QuadtreeNode(pos, obj));
        objects.insert(new QuadtreeNode(pos.add(0, w - 1), obj));
        objects.insert(new QuadtreeNode(pos.add(h - 1, w - 1), obj));
        objects.insert(new QuadtreeNode(pos.add(h - 1, 0), obj));
    }
    
    private void removeFromQuadtree(GameObject obj) {
        Vector2D pos = obj.getPosition();
        int w = obj.getWidth();
        int h = obj.getHeight();
        objects.remove(new QuadtreeNode(pos, obj));
        objects.remove(new QuadtreeNode(pos.add(0, w - 1), obj));
        objects.remove(new QuadtreeNode(pos.add(h - 1, w - 1), obj));
        objects.remove(new QuadtreeNode(pos.add(h - 1, 0), obj));
    }
    
    public void add(GameObject obj) {
        addToQuadtree(obj);
    }
    
    public void remove(GameObject obj) {       
        removeFromQuadtree(obj);        
    }
    
    private boolean isOutside(int x, int y) {
        return (x < 0 || y < 0 || x > height || y > width);
    }
    
    private boolean isOutside(Vector2D v) {
        return isOutside(v.getX(), v.getY());
    }
    
    public Stack<Direction> getRandomPath(MovableObject obj, int maxDist) {
    	
        ++visitID;
        
    	remove(obj);
        
    	Stack<Direction> stack = new Stack<>();
        Vector2D diag = new Vector2D(obj.getHeight(), obj.getWidth());
        
        Vector2D prevCell = obj.getPosition(); 
        visited.put(obj.getPosition(), visitID);
        
        int dist = 0;
        boolean flag = true;
        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        
        while (flag && dist < maxDist) {
            
        	if (dist % 5 == 0) {
        		Collections.shuffle(directions);
        	}
        	
            for (Direction d : directions) {
                
            	flag = false;
            	Vector2D tmp = prevCell.add(d.getMove());
                if (isOutside(tmp) || isOutside(tmp.add(diag))) {
                    continue;
                }
                
                if (getVisetedAtPos(tmp) != visitID && canBotMove(obj, tmp.sub(obj.getPosition())) && Vector2D.dist(tmp, obj.getPosition()) > dist) {
                    visited.put(tmp, visitID);
                    stack.push(d);
                    prevCell = tmp;
                    flag = true;
                    dist = Vector2D.dist(obj.getPosition(), prevCell);
                    break;                    
                }
            }
        }
        
        add(obj);
        
        Stack<Direction> result = new Stack<>(); 
        while (!stack.isEmpty()) {
        	result.push(stack.pop());
        }
        
        return result;
    }
    
    private int getVisetedAtPos(Vector2D pos) {
        if (visited.containsKey(pos)) {
            return visited.get(pos);
        } else {
            return NOT_VISITED_ID;
        }
    }
    
    public Map<Vector2D, Vector2D> getAccessibleCells(MovableObject obj, int maxDist) {
        
        ++visitID;
        
        remove(obj);
        
        Map<Vector2D, Vector2D> result = new HashMap<Vector2D, Vector2D>();
        Vector2D diag = new Vector2D(obj.getHeight(), obj.getWidth());
        
        Queue<Vector2D> queue = new LinkedList<Vector2D>();
        queue.add(obj.getPosition());
        visited.put(obj.getPosition(), visitID);
        
        while (!queue.isEmpty()) {
            
            Vector2D p = queue.remove();
            for (Direction d : Direction.values()) {
                Vector2D tmp = p.add(d.getMove());
                if (isOutside(tmp) || isOutside(tmp.add(diag))) {
                    continue;
                }
                if (getVisetedAtPos(tmp) != visitID && canMove(obj, tmp.sub(obj.getPosition())) && Vector2D.dist(obj.getPosition(), tmp) <= maxDist) {
                    visited.put(tmp, visitID);
                    result.put(tmp, p);
                    queue.add(tmp);
                }
            }
        }
        
        add(obj);
        
        return result;
    }
    
    private Rectangle staticRect;
    private static final int SEARCH_SQUARE_SIZE = GameModel.DISCRETE_FACTOR;
    // I assume that 'obj' is going to add 'v' to its position.
    // only the final position is checked.
    public boolean canMove(MovableObject obj, Vector2D v) {
        int w = obj.getWidth();
        int h = obj.getHeight();
        Vector2D cornerLU = obj.getPosition().add(v);
        Vector2D cornerRD = cornerLU.add(h, w);
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Set<GameObject> objsAtRect = new HashSet<>();
        staticRect.setBounds(cornerLU.getX() - SEARCH_SQUARE_SIZE, cornerLU.getY() - SEARCH_SQUARE_SIZE, h + SEARCH_SQUARE_SIZE * 2, w + SEARCH_SQUARE_SIZE * 2);
        objects.query(staticRect, objsAtRect);
        staticRect.setBounds(cornerLU.getX(), cornerLU.getY(), w, h);
        for (GameObject block : objsAtRect) {
            if (block == obj) {
                continue;
            }                
            if (staticRect.intersects(block.getPosition().getX(), block.getPosition().getY(), block.getWidth(), block.getHeight())) {
                return false;
            }
        }
        return true;
    }
    
    // I assume that 'obj' is going to add 'v' to its position.
    // only the final position is checked.
    public boolean canBotMove(MovableObject obj, Vector2D v) {
        int w = obj.getWidth();
        int h = obj.getHeight();
        Vector2D cornerLU = obj.getPosition().add(v);
        Vector2D cornerRD = cornerLU.add(h, w);
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Set<GameObject> objsAtRect = new HashSet<>();
        staticRect.setBounds(cornerLU.getX() - SEARCH_SQUARE_SIZE, cornerLU.getY() - SEARCH_SQUARE_SIZE, h + SEARCH_SQUARE_SIZE * 2, w + SEARCH_SQUARE_SIZE * 2);
        objects.query(staticRect, objsAtRect);
        staticRect.setBounds(cornerLU.getX(), cornerLU.getY(), w, h);
        for (GameObject block : objsAtRect) {
            if (block == obj || block.getDescription() == GameObjectDescription.BONUS) {
                continue;
            }                
            if (staticRect.intersects(block.getPosition().getX(), block.getPosition().getY(), block.getWidth(), block.getHeight())) {
                return false;
            }
        }
        return true;
    }
    
    //// !!!! UNSAFE FUNCTION. TEST CAREFULLY
    // Simply the same as 'canMove' method
    public int getBlockID(MovableObject obj, Vector2D v) {
        int w = obj.getWidth();
        int h = obj.getHeight();
        Vector2D cornerLU = obj.getPosition().add(v);
        Vector2D cornerRD = cornerLU.add(h, w);
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return EMPTY_ID; // can cause a bug.
        }
        Set<GameObject> objsAtRect = new HashSet<>();
        staticRect.setBounds(cornerLU.getX() - SEARCH_SQUARE_SIZE, cornerLU.getY() - SEARCH_SQUARE_SIZE, h + SEARCH_SQUARE_SIZE * 2, w + SEARCH_SQUARE_SIZE * 2);
        objects.query(staticRect, objsAtRect);
        staticRect.setBounds(cornerLU.getX(), cornerLU.getY(), w, h);
        for (GameObject block : objsAtRect) {
            if (block == obj) {
                continue;
            }
            if (staticRect.intersects(block.getPosition().getX(), block.getPosition().getY(), block.getWidth(), block.getHeight())) {
                return block.getID();
            }
        }
        return EMPTY_ID;
    }
    
    public boolean isFree(Vector2D v) {
        return isFree(v, GameModel.DISCRETE_FACTOR, GameModel.DISCRETE_FACTOR);
    }
    
    public boolean isFree(Vector2D v, int w, int h) {
        Vector2D cornerLU = v;
        Vector2D cornerRD = cornerLU.add(h, w);
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Set<GameObject> objsAtRect = new HashSet<>();
        objects.query(new Rectangle(cornerLU.getX(), cornerLU.getY(), w, h), objsAtRect);
        return (objsAtRect.isEmpty());
    }
    
    public boolean isFreeForProjectile(Vector2D v, int w, int h) {
        Vector2D cornerLU = v;
        Vector2D cornerRD = cornerLU.add(h, w);
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Set<GameObject> objsAtRect = new HashSet<>();
        staticRect.setBounds(cornerLU.getX() - SEARCH_SQUARE_SIZE, cornerLU.getY() - SEARCH_SQUARE_SIZE, h + SEARCH_SQUARE_SIZE * 2, w + SEARCH_SQUARE_SIZE * 2);
        objects.query(staticRect, objsAtRect);
        staticRect.setBounds(cornerLU.getX(), cornerLU.getY(), w, h);
        for (GameObject block : objsAtRect) {
            if (((block.getDescription() != GameObjectDescription.WATER)) && staticRect.intersects(block.getPosition().getX(), block.getPosition().getY(), block.getWidth(), block.getHeight())) {
                return false;
            }
        }
        return true;
    }
  
    public void debugprint() {
        System.out.println("Maze");
        Set<GameObject> objsAtRect = new HashSet<>();
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                objsAtRect.clear();
                objects.query(new Rectangle(i, j, 1, 1), objsAtRect);
                if (objsAtRect.isEmpty()) {
                    System.out.print(0);
                } else {
                    if (objsAtRect.iterator().next().getDescription() == GameObjectDescription.WATER) {
                        System.out.print(2);
                    } else {
                        System.out.print(1);
                    }    
                }                
                System.out.print(' ');
            }
            System.out.print("\n");
        }
        /*
        System.out.println("Movable");
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                System.out.print(movableIDs[i][j]);           
                System.out.print(' ');
            }
            System.out.print("\n");
        }
        
        System.out.println("Immovable");
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                System.out.print(immovableIDs[i][j]);           
                System.out.print(' ');
            }
            System.out.print("\n");
        }
        */
    }
}
