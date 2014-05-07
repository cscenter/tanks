package model.map;

import java.awt.Rectangle;
import java.util.Set;

import model.GameObject;

public class Quadtree {
    
    private final Rectangle bounds;
    private QuadtreeNode[] elements;
    
    private Quadtree topLeft;
    private Quadtree topRight;
    private Quadtree botLeft;
    private Quadtree botRight;
    
    public Quadtree(int width, int height, int elemPerQuad) {
        this(0, 0, width, height, elemPerQuad);
    }
    
    public Quadtree(int x, int y, int width, int height, int elemPerQuad) {
        bounds = new Rectangle(x, y, width, height);
        elements = new QuadtreeNode[elemPerQuad];
    }
    
    protected boolean set(QuadtreeNode e) {
        for (int i = 0; i < maxElem(); i++) {
            if (elements[i] == null) {
                elements[i] = e;
                return true;
            }
        }
        return false;
    }
    
    public int maxElem() {
        return elements.length;
    }
    
    public boolean insert(QuadtreeNode e) {
        if (!bounds.contains(e.getX(), e.getY())) {
            return false;
        }
        if (set(e)) {
            return true;
        } else {
             subdivide();
             if (topRight.insert(e)) {
                  return true;
             }
             if (topLeft.insert(e)) {
                  return true;
             }
             if (botRight.insert(e)) {
                  return true;
             }
             if (botLeft.insert(e)) {
                  return true;
            }
            return false; // too bad!!!
        }
    }
    
    protected boolean del(QuadtreeNode e) {
        for (int i = 0; i < maxElem(); i++) {
            if (elements[i] == null) {
                continue;
            }
            if (elements[i].getX() == e.getX() && elements[i].getY() == e.getY() && elements[i].obj.getID() == e.obj.getID()) {
                elements[i] = null;
                return true;
            }
        }
        return false;
     }
    
    public boolean remove(QuadtreeNode e) {
        if (!bounds.contains(e.getX(), e.getY())) {
            return false;
        } 
        if (del(e)) {
            return true;
        } else {
            if (topRight == null) {
                // if this code called
                // it is quite probably that it is a bug...
                return false;
            }
            if (topRight.remove(e)) {
                return true;
            }
            if (topLeft.remove(e)) {
                return true;
            }
            if (botLeft.remove(e)) {
                return true;
            }
            if (botRight.remove(e)) {
                return true;
            }
            return false; // too bad!!!
        }
    }
    
    public void query(Rectangle range, Set<GameObject> results) {
        if (!bounds.intersects(range)) {
            return;
        }
        for (int i = 0; i < maxElem(); i++) {
            if (elements[i] != null) {
                if (range.contains(elements[i].getX(), elements[i].getY())) {
                    results.add(elements[i].obj);
                }
            }
        }
        if (!hasChildren()) {
            return;
        }
        topLeft.query(range, results);
        topRight.query(range, results);
        botLeft.query(range, results);
        botRight.query(range, results);
    }
    
    public boolean hasChildren() {
        return topLeft != null;
    }
    
    /**
     * <p>
     * Subdivides this Quadtree into 4 other quadtrees.
     * </p>
     * <p>
     * This is usually used, when this Quadtree already has an
     * Element.
     * </p>
     * @return whether this Quadtree was subdivided, or didn't subdivide,
     * because it was already subdivided.
     */
    protected boolean subdivide() {
        if (hasChildren()) {
            return false;
        }
        int width1 = bounds.width / 2;
        int width2 = bounds.width - width1;
        int height1 = bounds.height / 2;
        int height2 = bounds.height - height1;
        topLeft  = new Quadtree(bounds.x, bounds.y, width1, height1, maxElem());
        topRight = new Quadtree(bounds.x + width1, bounds.y, width2, height1, maxElem());
        botLeft  = new Quadtree(bounds.x, bounds.y + height1, width1, height2, maxElem());
        botRight = new Quadtree(bounds.x + width1, bounds.y + height1, width2, height2, maxElem());
        return true;
    }
}