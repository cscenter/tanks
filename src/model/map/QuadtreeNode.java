package model.map;

import model.GameObject;
import model.Vector2D;

public class QuadtreeNode extends Vector2D {
    public GameObject obj;
    public QuadtreeNode(Vector2D pos, GameObject obj) {
        super(pos);
        this.obj = obj;
    }
}
