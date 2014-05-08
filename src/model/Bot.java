package model;

import java.util.*;

import model.MovableObject.Team;
import model.Tank.Difficulty;

public class Bot {
    
    private Tank controlledTank;
    private Stack<Direction> plannedMoves;
    private GameModel model;
    private Difficulty difficulty;   
    
    public Bot(GameModel model, Vector2D position, Difficulty difficulty) throws ModelException {
        this.model = model;
        controlledTank = model.addTank(Team.RED, difficulty, position);
        setDifficulty(difficulty);
        if (controlledTank == null) {
        	throw new ModelException("Cannot add bot at position " + position.toString());
        }
        plannedMoves = new Stack<>();
    }
    
    /*
     * 
    private static final Random GENERATOR = new Random();
    
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
    
    public void makeTurn() throws ModelException {
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
        
        if (plannedMoves.empty() || !model.canBotMove(controlledTank, plannedMoves.peek().getMove())) {
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
    
    
    public Difficulty getDifficulty() {
		return difficulty;
	}

	private void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}
}
