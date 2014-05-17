package model;

import java.util.ArrayList;
import java.util.List;

import model.MovableObject.Team;
import model.Tank.Difficulty;

public class CampaignGameModel extends GameModel {
	
    private List<BotSpawn> botsSpawns;
    private int remainingBots;
    
	public CampaignGameModel() {
        super();
        botsSpawns = new ArrayList<>();
    }

	private class BotSpawn {
        public Vector2D position;
	    public Difficulty botDifficulty;
	    public BotSpawn(Vector2D position, Difficulty botDifficulty) {
            this.position = position;
            this.botDifficulty = botDifficulty;
        }
	}
	
	@Override
	public void start() throws ModelException {
		
		super.start();
		
		for (BotSpawn spawn : botsSpawns) {
			if (remainingBots-- == 0) {
				break;
			}
			addBot(Team.RED, spawn.botDifficulty, spawn.position);
		}
	}
	
	@Override
	public void tick() throws ModelException {
		// TODO Auto-generated method stub
		super.tick();
	}

	public void addSpawn(Vector2D pos, Difficulty difficulty) {
	    botsSpawns.add(new BotSpawn(pos.sub(1,1).mul(DISCRETE_FACTOR), difficulty));
	}
	
    @Override
    public boolean isOver() {
        return !isPlayerAlive() || (remainingBots == 0 && bots.isEmpty());
    }

    public void setRemainingBots(int remainingBots) {
        this.remainingBots = remainingBots;
    }
}
