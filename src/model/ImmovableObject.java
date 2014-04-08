package model;

import java.lang.UnsupportedOperationException;

public class ImmovableObject  extends GameObject {
    
    public ImmovableObject(int id, Vector2D p, int w, int h, GameObjectDescription d)  {
        super(id, p, w, h, d);
    }
}
