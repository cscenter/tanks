package model;

import java.util.*;

public class Bot {
    
    private Tank controlledTank;
    private Stack<Direction> plannedMoves;
    private GameModel model;
    private int turnsToShoot;
    
    private static final Random GENERATOR = new Random();
    private static final int MAX_TURNS_TO_SHOOT = 3;
    
    public Bot(GameModel model, Tank tank) {
        this.model = model;
        controlledTank = tank;
        plannedMoves = new Stack<Direction>();
        turnsToShoot = 0;
    }
    
    private void createPath() {
        plannedMoves.clear();
        Map<Vector2D, Vector2D> availableCells = model.getAccessibleCells(controlledTank);

        if (!availableCells.isEmpty()) {
            int target = GENERATOR.nextInt(availableCells.size());
            Object[] keys = availableCells.keySet().toArray();
            Vector2D cell = (Vector2D) keys[target];
            Vector2D prev = availableCells.get(cell);
            
            List<Vector2D> cells = new ArrayList<Vector2D>();
            while (!prev.equals(controlledTank.getPosition())) {
                cells.add(cell.sub(prev));
                cell = prev;
                prev = availableCells.get(cell);
            }
            cells.add(cell.sub(prev));    

            for (Vector2D c : cells) {
                plannedMoves.push(Direction.fromVector2D(c));
            }
        }
    }
    
    public void makeTurn() {    
        if (turnsToShoot == 0) {
            Collection<Tank> enemies = model.getEnemies(controlledTank.getTeam());
            for (Tank enemy : enemies) {
                if (enemy.getPosition().sub(controlledTank.getPosition()).normalize().equals(controlledTank.getOrientation().getMove())) {
                    model.shoot(controlledTank.getID());
                    turnsToShoot = MAX_TURNS_TO_SHOOT;
                    return;
                }
            }
        } else {
            --turnsToShoot;
        }
        
        if (plannedMoves.empty() || !model.canTankMove(controlledTank.getID(), plannedMoves.peek().getMove())) {
            createPath();
        }
        
        if (!plannedMoves.empty()) {
            if (controlledTank.canMakeTurn()) {
                model.moveTank(controlledTank.getID(), plannedMoves.pop());
            } else {
                controlledTank.makeTurn();
            }
        }
    }
    
    public int getTankID() {
        return controlledTank.getID();
    }
    
}
