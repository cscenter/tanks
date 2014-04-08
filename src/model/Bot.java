package model;

import java.util.*;

public class Bot {
	
    private Tank controlledTank;
    private Stack<Vector2D> plannedMoves;
    private GameModel model;
    
	private static final Random GENERATOR = new Random();
	
    public Bot(GameModel model, Tank tank) {
        this.model = model;
        controlledTank = tank;
        plannedMoves = new Stack<Vector2D>();
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
				plannedMoves.push(c);
			}
		}
	}
	
    public void makeTurn() {	
		if (plannedMoves.empty() || !model.canTankMove(controlledTank.getID(), plannedMoves.peek())) {
            createPath();
        }
        
        Collection<Tank> enemies = model.getEnemies(controlledTank.getTeam());
        
        for (Tank enemy : enemies) {
            if (enemy.getPosition().sub(controlledTank.getPosition()).normalize().equals(controlledTank.getGunOrientation())) {
                model.shoot(controlledTank.getID());
                break;
            }
        }
        
		if (!plannedMoves.empty()) {
			model.moveTank(controlledTank.getID(), plannedMoves.pop());
		}
    }
    
    public int getTankID() {
        return controlledTank.getID();
    }
    
}
