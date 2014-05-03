package model;

import model.MovableObject.Team;
import model.Tank.Difficulty;

public class InfiniteGameModel extends GameModel {

    private int botsCount;
    private final int DEFAULT_BOTS_COUNT = 3;
    private final int PLAYER_DELAY = 2;
    
    private final Difficulty DEFAULT_DIFFICULTY = Difficulty.BOSS;
    
    public InfiniteGameModel() {
        super();
        
        botsCount = DEFAULT_BOTS_COUNT;
    }
    
    public InfiniteGameModel(int botsCount) {
        super();
        this.botsCount = botsCount;
    }
    
    @Override
    public void start() throws ModelException {
        super.start();
        
        addPlayer(Team.GREEN, PLAYER_DELAY, getRandomEmptyPosition(Tank.SIZE, Tank.SIZE));
        for (int i = 0; i < botsCount; ++i) {
            addBot(Team.RED, DEFAULT_DIFFICULTY, getRandomEmptyPosition(Tank.SIZE, Tank.SIZE));
        }
    }
    
    @Override
    public void tick() throws ModelException {
        super.tick();
        
        while (bots.size() < botsCount) {
            addBot(Team.RED, DEFAULT_DIFFICULTY, getRandomEmptyPosition(Tank.SIZE, Tank.SIZE));
            score += SCORE_PER_KILL;
        }
    }

}
