package model;

public class DiscreteMap {
    
    public static final int EMPTY_ID = 0;
    
    private int ids[][];
    private final int width;
    private final int height;
    
    public DiscreteMap(int w, int h) {
        width = w;
        height = h;
        ids = new int[h][];
        for (int i = 0; i < h; ++i) {
            ids[i] = new int[w];
            for (int j = 0; j < w; ++j) {
                ids[i][j] = EMPTY_ID;
            }
        }
    }
    
    public void add(GameObject obj) {
        // for safety, checks should be added!
        int i = obj.getPosition().getX();
        int x = i + obj.getHeight();
        int j = obj.getPosition().getY();
        int y = j + obj.getWidth();
        for (; i < x; ++i) {
            for (j = obj.getPosition().getY(); j < y; ++j) {
                ids[i][j] = obj.getID();
            }
        }
    }
    
    public void remove(GameObject obj) {
        // for safety, checks should be added!
        int i = obj.getPosition().getX();
        int x = i + obj.getHeight();
        int j = obj.getPosition().getY();
        int y = j + obj.getWidth();
        for (; i < x; ++i) {
            for (j = obj.getPosition().getY(); j < y; ++j) {
                ids[i][j] = EMPTY_ID;
            }
        }        
    }
    
    public int getObjectID(Vector2D pos, int w, int h) {
        // it also isnt very safe.
        if (pos.getX() < 0 || pos.getY() < 0) {
            return EMPTY_ID;
        }
        if (pos.getX() + h > height || pos.getY() + w > width) {
            return EMPTY_ID;
        }
        
        for (int i = pos.getX(); i < pos.getX() + h; ++i) {
            for (int j = pos.getY(); j < pos.getY() + w; ++j) {
                if (ids[i][j] != EMPTY_ID) {
                    return ids[i][j];
                }
            }
        }
        return EMPTY_ID;
    }
    
	public boolean isAnythingElse(Vector2D pos, GameObject obj) {
		int w = obj.getWidth();
		int h = obj.getHeight();
		if (pos.getX() < 0 || pos.getY() < 0) {
            return true;
        }
        if (pos.getX() + h > height || pos.getY() + w > width) {
            return true;
        }
        
        for (int i = pos.getX(); i < pos.getX() + h; ++i) {
            for (int j = pos.getY(); j < pos.getY() + w; ++j) {
				if ((ids[i][j] != EMPTY_ID) && (ids[i][j] != obj.getID())) {
                    return true;
                }
            }
        }
        return false;
    }
	
    public boolean isFree(Vector2D pos, int w, int h) {
        
        if (pos.getX() < 0 || pos.getY() < 0) {
            return false;
        }
        if (pos.getX() + h > height || pos.getY() + w > width) {
            return false;
        }
        
        for (int i = pos.getX(); i < pos.getX() + h; ++i) {
            for (int j = pos.getY(); j < pos.getY() + w; ++j) {
                if (ids[i][j] != EMPTY_ID) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void debugprint() {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                System.out.print(ids[i][j]);
                System.out.print(' ');
            }
            System.out.print("\n");
        }
    }
}
