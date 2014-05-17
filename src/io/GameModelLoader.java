package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import model.CampaignGameModel;
import model.GameModel;
import model.GameObjectDescription;
import model.InfiniteGameModel;
import model.ModelException;
import model.MovableObject.Team;
import model.Tank.Difficulty;
import model.Vector2D;

public class GameModelLoader {

    public static GameModel load(String filename) throws MapIOException {
        System.out.println(filename);
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String modelType = br.readLine();
            System.out.println(1);
            switch (GameModel.ModelType.valueOf(modelType)) {
            case INFINITE:
                return loadInfinite(br);
            case CAMPAIGN:
                return loadCampaign(br);
            default:
                throw new MapIOException("Model type specified incorrectly.");
            }
        } catch (IOException e) {
            throw new MapIOException("Cannot read map file.");
        }
    }
    
    private static InfiniteGameModel loadInfinite(BufferedReader br) throws MapIOException {
        InfiniteGameModel model = new InfiniteGameModel();
        GameModelReader.parse(model, br);
        
        try {
            model.setBotsCount(Integer.parseInt(br.readLine()));
        } catch (IOException e) {
            throw new MapIOException("Cannot read bots count.");
        }
        
        return model;
    }
    
    private final static int DEFAULT_PLAYER_DELAY = 6;
    
    private static CampaignGameModel loadCampaign(BufferedReader br) throws MapIOException {
        CampaignGameModel model = new CampaignGameModel();
        GameModelReader.parse(model, br);
        System.out.println(2);
        
        try {
            String[] splittedLine = br.readLine().split(" ");
            
            if (!splittedLine[0].equals("PLAYER")) {
                throw new MapIOException("Cannot find player's spawn coordinates.");
            }
            
            Vector2D pos = new Vector2D(Integer.parseInt(splittedLine[1]), Integer.parseInt(splittedLine[2]));
            model.addPlayer(Team.GREEN, DEFAULT_PLAYER_DELAY, pos.sub(1, 1).mul(GameModel.DISCRETE_FACTOR));
            
            splittedLine = br.readLine().split(" ");
            
            int spawnsCount = Integer.parseInt(splittedLine[0]);
            int totalBotsCount = Integer.parseInt(splittedLine[1]);
            
            model.setRemainingBots(totalBotsCount);
            
            for (int i = 0; i < spawnsCount; ++i) {
                splittedLine = br.readLine().split(" ");
                
                pos = new Vector2D(Integer.parseInt(splittedLine[1]), Integer.parseInt(splittedLine[2]));
                model.addSpawn(pos, Difficulty.valueOf(splittedLine[0]));
            }
            
        } catch (IOException e) {
            throw new MapIOException("Cannot read map file.");
        }
        
        
        return model;
    }
}
