package tanks.model;

import java.lang.Math;

public class Vector2D {
    private int x;
    private int y;
    
    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }
    
    public Vector2D sub(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }
    
    public Vector2D mul(int k) {
        return new Vector2D(x * k, y * k);
    }
    
    public boolean isNull() {
        return (x == 0) && (y == 0);
    }
    
    private static int sign(int i) {
        return (i > 0) ? 1 : -1;
    }
    
    public Vector2D normalize() {
        Vector2D v = new Vector2D(0, 0);
        if (Math.abs(x) > Math.abs(y)) {
            v.setX(sign(x));
        } else {
            v.setY(sign(y));
        }
        return v;            
    }
    
    public boolean equals(Vector2D v) {
        return (x == v.x) && (y == v.y);
    }
    
}





