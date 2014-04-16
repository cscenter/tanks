package model;

public class Speed {

    private Direction direction;
    private TurnDelay turnDelay;
    
    public Speed(Direction direction, int delay) {
        this.direction = direction;
        turnDelay = new TurnDelay(delay, 1);
    }


    public Direction getDirection() {
        return direction;
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    public int getTurnsToMove() {
        return turnDelay.getTurnsToMove();
    }


    public boolean makeTurn() {
        return turnDelay.makeTurn();
    }


    public boolean canMakeTurn() {
        return turnDelay.canMakeTurn();
    }


    public void setDelay(int delay) {
        turnDelay.setDelay(delay);
    }
}
