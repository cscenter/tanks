package model;

import java.util.*;

public class DiscreteMap {
    
    public static final int EMPTY_ID = 0;
    private static final int NOT_VISITED_ID = 0;
    
    private Map<Vector2D, Cell> maze;
    private Map<Vector2D, Integer> movableIDs;
    private Map<Vector2D, Integer> immovableIDs;
    
    private Map<Vector2D, Integer> visited;
    private int visitID = NOT_VISITED_ID;
    
    private final int width;
    private final int height;
    
    
    public DiscreteMap(int w, int h) {
        width = w;
        height = h;
        maze = new HashMap<>();
        movableIDs = new HashMap<>();
        immovableIDs = new HashMap<>();
        visited = new HashMap<>();
    }
    
    private void mazePutRectangle(Vector2D pos, int h, int w, Cell cell) {
        int i;
        int j;
        for (i = 0, j = 0; i < h; ++i) {
            maze.put(pos.add(i, j), cell);
        }
        for (i = 0, j = 0; j < w; ++j) {
            maze.put(pos.add(i, j), cell);
        }
        for (i = 0, j = w - 1; i < h; ++i) {
            maze.put(pos.add(i, j), cell);
        }
        for (i = h - 1, j = 0; j < w; ++j) {
            maze.put(pos.add(i, j), cell);
        }
    }
    
    private void immovableIDsPutRectangle(Vector2D pos, int h, int w, int id) {
        int i;
        int j;
        for (i = 0, j = 0; i < h; ++i) {
            immovableIDs.put(pos.add(i, j), id);
        }
        for (i = 0, j = w - 1; i < h; ++i) {
            immovableIDs.put(pos.add(i, j), id);
        }
        for (i = h - 1, j = 0; j < w; ++j) {
            immovableIDs.put(pos.add(i, j), id);
        }
        for (i = 0, j = 0; j < w; ++j) {
            immovableIDs.put(pos.add(i, j), id);
        }
    }
    
    private void movableIDsPutRectangle(Vector2D pos, int h, int w, int id) {
        int i;
        int j;
        for (i = 0, j = 0; i < h; ++i) {
            movableIDs.put(pos.add(i, j), id);
        }
        for (i = 0, j = w - 1; i < h; ++i) {
            movableIDs.put(pos.add(i, j), id);
        }
        for (i = h - 1, j = 0; j < w; ++j) {
            movableIDs.put(pos.add(i, j), id);
        }
        for (i = 0, j = 0; j < w; ++j) {
            movableIDs.put(pos.add(i, j), id);
        }
    }
    
    public void add(ImmovableObject obj) {
        Vector2D pos = obj.getPosition();
        int id = obj.getID();
        int w = obj.getWidth();
        int h = obj.getHeight();
        
        Cell cell;
        
        switch (obj.getDescription()) {
        case GRASS:
        case GROUND:
        	return;
        case WATER:
            cell = Cell.SEMIBLOCKED;
        	break;
        default:
            cell = Cell.BLOCKED;
        }

        mazePutRectangle(pos, h, w, cell);
        immovableIDsPutRectangle(pos, h, w, id);
    }
    
    public void add(MovableObject obj) {
        Vector2D pos = obj.getPosition();
        int id = obj.getID();
        int w = obj.getWidth();
        int h = obj.getHeight();
        
        Cell cell = Cell.BLOCKED;

        mazePutRectangle(pos, h, w, cell);
        movableIDsPutRectangle(pos, h, w, id);
    }
    
    public void remove(GameObject obj) {       
        GameObjectDescription desc = obj.getDescription();
        if (desc == GameObjectDescription.TANK || desc == GameObjectDescription.PROJECTILE) {
            remove((MovableObject) obj);
        } else {
            remove((ImmovableObject) obj);
        }        
    }
    
    public void remove(ImmovableObject obj) {
        Vector2D pos = obj.getPosition();
        int w = obj.getWidth();
        int h = obj.getHeight();
        
        mazePutRectangle(pos, h, w, Cell.EMPTY);
        immovableIDsPutRectangle(pos, h, w, EMPTY_ID);        
    }
    
    public void remove(MovableObject obj) {
        Vector2D pos = obj.getPosition();
        int w = obj.getWidth();
        int h = obj.getHeight();
        
        mazePutRectangle(pos, h, w, Cell.EMPTY);
        movableIDsPutRectangle(pos, h, w, EMPTY_ID);         
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
                
                if (getVisetedAtPos(tmp) != visitID && canMove(obj, tmp.sub(obj.getPosition())) && Vector2D.dist(tmp, obj.getPosition()) > dist) {
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
    
    private Cell getMazeAtPos(Vector2D pos) {
        if (maze.containsKey(pos)) {
            return maze.get(pos);
        } else {
            return Cell.EMPTY;
        }
    }
    
    private int getImmovableIDsAtPos(Vector2D pos) {
        if (immovableIDs.containsKey(pos)) {
            return immovableIDs.get(pos);
        } else {
            return EMPTY_ID;
        }
    }
    
    private int getMovableIDsAtPos(Vector2D pos) {
        if (movableIDs.containsKey(pos)) {
            return movableIDs.get(pos);
        } else {
            return EMPTY_ID;
        }
    }
    
    // I assume that 'obj' is going to add 'v' to its position.
    // only the final position is checked.
    public boolean canMove(MovableObject obj, Vector2D v) {
        Vector2D cornerLU = obj.getPosition().add(v);
        Vector2D cornerRD = cornerLU.add(new Vector2D(obj.getHeight(), obj.getWidth()));
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Vector2D pos = new Vector2D(cornerLU);
        for (; pos.getX() < cornerRD.getX(); pos.incX()) {
            for (pos.setY(cornerLU.getY()); pos.getY() < cornerRD.getY(); pos.incY()) {
				if ((getMazeAtPos(pos) != Cell.EMPTY) && (getMovableIDsAtPos(pos) != obj.getID())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Simply the same as 'canMove' method
    public int getBlockID(MovableObject obj, Vector2D v) {
        Vector2D cornerLU = obj.getPosition().add(v);
        Vector2D cornerRD = cornerLU.add(new Vector2D(obj.getHeight(), obj.getWidth()));
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return EMPTY_ID; // can cause a bug.
        }
        Vector2D pos = new Vector2D(cornerLU);
        for (; pos.getX() < cornerRD.getX(); pos.incX()) {
            for (pos.setY(cornerLU.getY()); pos.getY() < cornerRD.getY(); pos.incY()) {
				if (getMazeAtPos(pos) != Cell.EMPTY) {
                    return (getMovableIDsAtPos(pos) != EMPTY_ID) ? getMovableIDsAtPos(pos) : getImmovableIDsAtPos(pos);
                }
            }
        }
        return EMPTY_ID; // it's okay
    }
    
    public boolean isFree(Vector2D v, int w, int h) {
        Vector2D cornerLU = v;
        Vector2D cornerRD = cornerLU.add(new Vector2D(h, w));
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Vector2D pos = new Vector2D(cornerLU);
        for (; pos.getX() < cornerRD.getX(); pos.incX()) {
            for (pos.setY(cornerLU.getY()); pos.getY() < cornerRD.getY(); pos.incY()) {
                if (getMazeAtPos(pos) != Cell.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isFreeForProjectile(Vector2D v, int w, int h) {
        Vector2D cornerLU = v;
        Vector2D cornerRD = cornerLU.add(new Vector2D(h, w));
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        Vector2D pos = new Vector2D(cornerLU);
        for (; pos.getX() < cornerRD.getX(); pos.incX()) {
            for (pos.setY(cornerLU.getY()); pos.getY() < cornerRD.getY(); pos.incY()) {
                if (getMazeAtPos(pos) == Cell.BLOCKED) {
                    return false;
                }
            }
        }
        return true;
    }
  
    public void debugprint() {
        System.out.println("Maze");
        Vector2D pos = new Vector2D(0, 0);
        for (; pos.getX() < height; pos.incX()) {
            for (pos.setY(0); pos.getY() < width; pos.incY()) {
                switch (getMazeAtPos(pos)) {
                case EMPTY:
                    System.out.print(0);
                    break;
                case BLOCKED:
                    System.out.print(1);
                    break;
                case SEMIBLOCKED:
                    System.out.print(2);
                    break;                    
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
    
    private enum Cell {
        EMPTY, BLOCKED, SEMIBLOCKED
    }
}
