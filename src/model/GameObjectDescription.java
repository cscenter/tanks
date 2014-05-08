package model;

import java.util.*;

public enum GameObjectDescription {
    WATER('W'), TREE('R'), STONE('S'), GROUND('G'), TANK('T'), PROJECTILE('P'), PALM('A'), GRASS('V'), BONUS('B');
    
    char tag;
    private static Map<Character, GameObjectDescription> charToDescriptionMapping;
    
    private GameObjectDescription(char tag) {
        this.tag = tag;
    }
    
    public char getTag() {
        return tag;
    }
    
    public static GameObjectDescription getDescription(char tag) {
        if (charToDescriptionMapping == null) {
            initMapping();
        }
        if (charToDescriptionMapping.containsKey(tag)) {
        	return charToDescriptionMapping.get(tag);
        } else {
        	switch (tag) {
        	case '1' :	
        		return getRandomBackground();
        	case '2' :
        		return getRandomTree();
        	default:
        		return null;
        	}
        }
    }
    
    private static void initMapping() {
        charToDescriptionMapping = new HashMap<Character, GameObjectDescription>();
        for (GameObjectDescription s : values()) {
            charToDescriptionMapping.put(s.tag, s);
        }
    }
    
    private static final Random GENERATOR = new Random();
    private static GameObjectDescription[] backgrounds = {GRASS, GROUND};
    private static GameObjectDescription[] trees = {TREE, PALM};
    
    public static GameObjectDescription getRandomTree() {
    	return trees[GENERATOR.nextInt(trees.length)];
    }
    
    public static GameObjectDescription getRandomBackground() {
    	return backgrounds[GENERATOR.nextInt(backgrounds.length)];    	
    }
}
