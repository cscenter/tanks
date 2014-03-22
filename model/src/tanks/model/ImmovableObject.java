package tanks.model;

import java.lang.UnsupportedOperationException;

public class ImmovableObject  extends GameObject {
    public ImmovableObject(int id, Vector2D p, int w, int h, char c)  {
        super(id, p, w, h, c);
    }
}
