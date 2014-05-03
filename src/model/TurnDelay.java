package model;

public class TurnDelay {

    private int delay;
    private int turnsToMove;
   
    public TurnDelay(int delay) {
    	super();
        this.delay = delay;
        this.turnsToMove = 1;
    }
    
    public TurnDelay(int delay, int turnsToMove) {
        super();
        this.delay = delay;
        this.turnsToMove = turnsToMove;
    }
    
    public int getTurnsToMove() {
        return turnsToMove;
    }

    public boolean makeTurn() {
        if (turnsToMove == 0) {
            turnsToMove = delay;
            return true;
        } else {
            --turnsToMove;
            return false;
        }
    }
    
    public boolean canMakeTurn() {
        return turnsToMove == 0;
    }


    public void setDelay(int delay) {
        this.delay = delay;
    }
}
