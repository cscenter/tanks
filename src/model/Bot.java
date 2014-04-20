package model;

import java.util.*;

public class Bot {
    
    private Tank controlledTank;
    private Stack<Direction> plannedMoves;
    private GameModel model;
    
    private static final Random GENERATOR = new Random();
    
    public Bot(GameModel model, Tank tank) {
        this.model = model;
        controlledTank = tank;
        plannedMoves = new Stack<Direction>();
    }
    
    /*
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
    */
    
    private void createPath() {
        plannedMoves = model.getRandomPath(controlledTank);
    }
    
    public void makeTurn() {
        boolean shootFlag = false;
        Collection<Tank> enemies = model.getEnemies(controlledTank.getTeam());
        for (Tank enemy : enemies) {
            if (enemy.getPosition().sub(controlledTank.getPosition()).normalize().equals(controlledTank.getOrientation().getMove())) {
                if (controlledTank.canShoot(false)) {
                    model.shoot(controlledTank.getID());
                    return;
                } else {
                    shootFlag = true;
                    break;
                }
            }
        }

        if (shootFlag) {
            controlledTank.canShoot(true);
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
