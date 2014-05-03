package model;

import java.util.List;

import model.MovableObject.Team;

public class CampaignGameModel extends GameModel {
	
	private int remainingBots;
	private List<Vector2D> botsSpawns;
	
	/*private botsMaxDifficulty;
	
	@Override
	public void start() {
		
		super.start();
		
		for (Vector2D spawnCoords : botsSpawns) {
			if (remainingBots-- == 0) {
				break;
			}
			addBot(Team.RED, delay, spawnCoords)
		}
	}*/
	@Override
	public void tick() throws ModelException {
		// TODO Auto-generated method stub
		super.tick();
	}	

	
	
}
