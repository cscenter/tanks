package model;

import java.util.*;

public class Bot {

    private Tank controlledTank;
    private List<Direction> path;
    private GameModel model;
    
    public Bot(GameModel model, Tank tank) {
        this.model = model;
        controlledTank = tank;
        path = new ArrayList<Direction>();
    }
    
    public void makeTurn(List<Vector2D> enemies) {
        if (path.empty()) {
            List<CellWithPredecessor> availableCells = model.getAccessibleCells(controlledTank);
            if (!availableCells.empty()) {
                
            }
        }
        
    }
    
}
