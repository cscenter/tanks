package model;

import io.GameModelLoader;
import io.MapIOException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Campaign {

    private CampaignGameModel model;
    private int currentLevel;
    private List<String> mapFilenames;
    
    public Campaign(String mapDir) throws ModelException {
        mapFilenames = new ArrayList<>();
        File folder = new File(mapDir);
        
        for (File f : folder.listFiles()) {
            mapFilenames.add(f.getAbsolutePath());
        }
        currentLevel = -1;
        nextLevel();
    }
    
    public String getMapName() {
        String filename  = new File(mapFilenames.get(currentLevel)).getName();
        String mapName = filename.replaceFirst("[.][^.]+$", "").replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
        mapName = mapName.substring(0, 1).toUpperCase() + mapName.substring(1);
        return mapName;
    }
    
    public void reloadLevel() throws ModelException {
        --currentLevel;
        nextLevel();
    }
    
    public GameModel getModel() {
        return model;
    }
    
    public boolean isLastLevel() {
        return (currentLevel == mapFilenames.size() - 1);
    }
    
    public void nextLevel() throws ModelException {
        try {
            if (isLastLevel()) {
                throw new ModelException("Out of levels.");
            }
            model = (CampaignGameModel) GameModelLoader.load(mapFilenames.get(++currentLevel));
        } catch (MapIOException e) {
            throw new ModelException("Cannot load level.\n" + e.getMessage());
        }
    }
}
