package model;

import model.MovableObject.Team;
import model.Tank.Difficulty;

public class InfiniteGameModel extends GameModel {

    private int botsCount;
    private static final int DEFAULT_BOTS_COUNT = 3;
    private static final int PLAYER_DELAY = 6;
    
    public InfiniteGameModel() {
        super();
    }

    
    @Override
    public void start() throws ModelException {
        super.start();
        
        addPlayer(Team.GREEN, PLAYER_DELAY, getRandomEmptyPosition());
        for (int i = 0; i < botsCount; ++i) {
            addBot(Team.RED, Difficulty.getRandomDifficulty(), getRandomEmptyPosition());
        }
    }
    
    @Override
    public void tick() throws ModelException {
        super.tick();
        
        if (bots.size() < botsCount) {
            Vector2D pos = getRandomEmptyPosition();
            while (bots.size() < botsCount && pos != null) {
                addBot(Team.RED, Difficulty.getRandomDifficulty(), pos);
                pos = getRandomEmptyPosition();
            }
        }
    }

    @Override
    public boolean isOver() {
        return ! isPlayerAlive();
    }


    public void setBotsCount(int botsCount) {
        if (botsCount != -1) {
            this.botsCount = botsCount;
        } else {
            this.botsCount = DEFAULT_BOTS_COUNT;
        }
    }

}
