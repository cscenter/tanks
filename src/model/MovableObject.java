package model;

public class MovableObject extends GameObject {

    private final int team;
    
    protected Direction orientation;
    protected Speed speed;
    
    protected int health = 1;
    
    public MovableObject(int id, Vector2D p, GameObjectDescription d, int t) {
        super(id, p, d);
        speed = new Speed(Direction.NONE, 3);
        team = t;
        orientation = Direction.DOWN;
    }
    
    public void setDirection(Direction direction) {
        speed.setDirection(direction);
    }

    public void setPosition(Vector2D p) {
        position = p;
    }
    
    public int getTeam() {
        return team;
    }
    
    public Direction getDirection() {
        return speed.getDirection();
    }
    
    public void setOrientation(Direction orientation) {
        this.orientation = orientation;
    }
    
    public Direction getOrientation() {
        return orientation;
    }

    
    public boolean canMakeTurn() {
        return speed.canMakeTurn();
    }

    public boolean makeTurn() {
        return speed.makeTurn();
    }

    @Override
    public boolean attacked(Projectile p) {
        if (p.getTeam() != getTeam()) {
            --health;
        }
        return (health == 0);
    }
    
    @Override
    public int getHealth() {
        return health;
    }
}
