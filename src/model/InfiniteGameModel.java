package model;

import model.MovableObject.Team;

public class InfiniteGameModel extends GameModel {

    private int botsCount;
    private final int DEFAULT_BOTS_COUNT = 3;
    private final int PLAYER_DELAY = 2;
    private final int BOTS_DELAY = 3;
    
    public InfiniteGameModel() {
        super();
        
        botsCount = DEFAULT_BOTS_COUNT;
    }
    
    public InfiniteGameModel(int botsCount) {
        super();
        this.botsCount = botsCount;
    }
    
    @Override
    public void start() {
        super.start();
        
        addPlayer(Team.GREEN, PLAYER_DELAY, getRandomEmptyPosition(Tank.SIZE, Tank.SIZE));
        for (int i = 0; i < botsCount; ++i) {
            addBot(Team.RED, BOTS_DELAY, getRandomEmptyPosition(Tank.SIZE, Tank.SIZE));
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        while (bots.size() < botsCount) {
            addBot(Team.RED, BOTS_DELAY, getRandomEmptyPosition(Tank.SIZE, Tank.SIZE));
            score += SCORE_PER_KILL;
        }
    }

}
