package model;

public class MovableObject extends GameObject {

    private final Team team;
    
    protected Direction orientation;
    protected Speed speed;
    
    protected int health = 1;
    
    public void setHealth(int health) {
        this.health = health;
    }

    public MovableObject(int id, Vector2D p, GameObjectDescription d, Team team) {
        super(id, p, d);
        speed = new Speed(Direction.NONE, 3);
        this.team = team;
        orientation = Direction.DOWN;
    }
    
    public void setMoveDirection(Direction direction) {
        speed.setDirection(direction);
    }

    public void setPosition(Vector2D p) {
        position = p;
    }
    
    public Team getTeam() {
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
            health -= p.getDamage();
        }
        return (health <= 0);
    }
    
    @Override
    public int getHealth() {
        return health;
    }
    
    public enum Team {
        GREEN, RED
    }
}
