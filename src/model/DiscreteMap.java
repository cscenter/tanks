package model;

import java.util.*;

public class DiscreteMap {
    
    public static final int EMPTY_ID = 0;
    
    private Cell[][] maze;
    private int[][] movableIDs;
    private int[][] immovableIDs;
    private final int width;
    private final int height;
    
    public DiscreteMap(int w, int h) {
        width = w;
        height = h;
        maze = new Cell[h][];
        movableIDs = new int[h][];
        immovableIDs = new int[h][];
        for (int i = 0; i < h; ++i) {
            maze[i] = new Cell[w];
            movableIDs[i] = new int[w];
            immovableIDs[i] = new int[w];
            for (int j = 0; j < w; ++j) {
                maze[i][j] = Cell.EMPTY;
                movableIDs[i][j] = EMPTY_ID;
                immovableIDs[i][j] = EMPTY_ID;
            }
        }
    }
    /*
    public void add(GameObject obj) {       
        GameObjectDescription desc = obj.getDescription();
        if (desc == GameObjectDescription.TANK || desc == GameObjectDescription.PROJECTILE) {
            add((MovableObject) obj);
        } else {
            add((ImmovableObject) obj);
        }        
    }
    */
    
    public void add(ImmovableObject obj) {
        int i = obj.getPosition().getX();
        int x = i + obj.getHeight();
        int j = obj.getPosition().getY();
        int y = j + obj.getWidth();
        
        Cell tmp = (obj.getDescription() == GameObjectDescription.WATER) ? Cell.SEMIBLOCKED : Cell.BLOCKED;
        int id = obj.getID();
        
        for (; i < x; ++i) {
            for (j = obj.getPosition().getY(); j < y; ++j) {
                maze[i][j] = tmp;
                immovableIDs[i][j] = id;
            }
        }
    }
    
    public void add(MovableObject obj) {
        int i = obj.getPosition().getX();
        int x = i + obj.getHeight();
        int j = obj.getPosition().getY();
        int y = j + obj.getWidth();
        
        Cell tmp = Cell.BLOCKED;
        int id = obj.getID();
        
        for (; i < x; ++i) {
            for (j = obj.getPosition().getY(); j < y; ++j) {
                maze[i][j] = tmp;
                movableIDs[i][j] = id;
            }
        }
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
        int i = obj.getPosition().getX();
        int x = i + obj.getHeight();
        int j = obj.getPosition().getY();
        int y = j + obj.getWidth();

        Cell tmp = Cell.EMPTY;
        int id = EMPTY_ID;
        
        for (; i < x; ++i) {
            for (j = obj.getPosition().getY(); j < y; ++j) {
                maze[i][j] = tmp; // it can cause a bug, if there are several objects
                immovableIDs[i][j] = id;
            }
        }        
    }
    
    public void remove(MovableObject obj) {
        int i = obj.getPosition().getX();
        int x = i + obj.getHeight();
        int j = obj.getPosition().getY();
        int y = j + obj.getWidth();

        Cell tmp = Cell.EMPTY;
        int id = EMPTY_ID;
        
        for (; i < x; ++i) {
            for (j = obj.getPosition().getY(); j < y; ++j) {
                maze[i][j] = (immovableIDs[i][j] == 0) ? tmp : Cell.SEMIBLOCKED; // it can cause a bug, if there are several objects
                movableIDs[i][j] = id;
            }
        }        
    }
    
    private boolean isOutside(int x, int y) {
        return (x < 0 || y < 0 || x > height || y > width);
    }
    
    private boolean isOutside(Vector2D v) {
        return isOutside(v.getX(), v.getY());
    }
    
    public Map<Vector2D, Vector2D> getAccessibleCells(MovableObject obj) {
        remove(obj);
        
        Map<Vector2D, Vector2D> result = new HashMap<Vector2D, Vector2D>();
        Vector2D diag = new Vector2D(obj.getHeight(), obj.getWidth());
        boolean visited[][] = new boolean[height][];
        for (int i = 0; i < height; ++i) {
            visited[i] = new boolean[width];
        }
        
        Queue<Vector2D> queue = new LinkedList<Vector2D>();
        queue.add(obj.getPosition());
        visited[obj.getPosition().getX()][obj.getPosition().getY()] = true;
        
        while (!queue.isEmpty()) {
            
            Vector2D p = queue.remove();
            for (Direction d : Direction.values()) {
                Vector2D tmp = p.add(d.getMove());
                if (isOutside(tmp) || isOutside(tmp.add(diag))) {
                    continue;
                }
                if (!visited[tmp.getX()][tmp.getY()] && canMove(obj, tmp.sub(obj.getPosition()))) {
                    visited[tmp.getX()][tmp.getY()] = true;
                    result.put(tmp, p);
                    queue.add(tmp);
                }
            }
        }
        
        add(obj);
        
        
        return result;
    }
    
    // I assume that 'obj' is going to add 'v' to its position.
    // only the final position is checked.
    public boolean canMove(MovableObject obj, Vector2D v) {
        Vector2D cornerLU = obj.getPosition().add(v);
        Vector2D cornerRD = cornerLU.add(new Vector2D(obj.getHeight(), obj.getWidth()));
        if (isOutside(cornerLU) || isOutside(cornerRD)) {
            return false;
        }
        for (int i = cornerLU.getX(); i < cornerRD.getX(); ++i) {
            for (int j = cornerLU.getY(); j < cornerRD.getY(); ++j) {
				if ((maze[i][j] != Cell.EMPTY) && (movableIDs[i][j] != obj.getID())) {
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
        for (int i = cornerLU.getX(); i < cornerRD.getX(); ++i) {
            for (int j = cornerLU.getY(); j < cornerRD.getY(); ++j) {
				if (maze[i][j] != Cell.EMPTY) {
                    return (movableIDs[i][j] != EMPTY_ID) ? movableIDs[i][j] : immovableIDs[i][j];
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
        for (int i = cornerLU.getX(); i < cornerRD.getX(); ++i) {
            for (int j = cornerLU.getY(); j < cornerRD.getY(); ++j) {
                if (maze[i][j] != Cell.EMPTY) {
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
        for (int i = cornerLU.getX(); i < cornerRD.getX(); ++i) {
            for (int j = cornerLU.getY(); j < cornerRD.getY(); ++j) {
                if (maze[i][j] == Cell.BLOCKED) {
                    return false;
                }
            }
        }
        return true;
    }
  
    public void debugprint() {
        System.out.println("Maze");
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                switch (maze[i][j]) {
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
    }
    
    enum Cell {
        EMPTY, BLOCKED, SEMIBLOCKED
    }
}
