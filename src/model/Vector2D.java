package model;

import java.lang.Math;
import java.lang.Comparable;

public class Vector2D implements Comparable<Vector2D> {
    private int x;
    private int y;
    
    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D(Vector2D v) {
        this.x = v.getX();
        this.y = v.getY();
    }
    
    public void incX() {
        ++(this.x);
    }
    
    public void incX(int k) {
        this.x += k;
    }
    
    public void incY() {
        ++(this.y);
    }
    
    public void incY(int k) {
        this.y += k;
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
    
    public Vector2D add(int x, int y) {
        return new Vector2D(this.x + x, this.y + y);
    }
    
    public Vector2D sub(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }
    
    public Vector2D sub(int x, int y) {
        return new Vector2D(this.x - x, this.y - y);
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
    
    public String toString() {
        return "(" + Integer.toString(x) + ", " + Integer.toString(y) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector2D other = (Vector2D) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
    
    public static int dist(Vector2D a, Vector2D b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    @Override
    public int compareTo(Vector2D v) {
        return (this.x != v.x) ? ((this.x > v.x) ? 1 : -1) : ((this.y != v.y) ? ((this.y > v.y) ? 1 : -1) : 0); 
    }
    
}
