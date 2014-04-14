package model;

public interface Attackable {
    
    public boolean attacked(Projectile p);
    public int getHealth();
    
}