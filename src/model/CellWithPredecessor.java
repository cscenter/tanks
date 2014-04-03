package model;

import java.util.*;

public class CellWithPredecessor {

    public Vector2D position;    
    public Vector2D predecessor;
    
    public CellWithPredecessor(Vector2D pos, Vector2D pred) {
        position = pos;
        predecessor = pred;
    }
    
}
