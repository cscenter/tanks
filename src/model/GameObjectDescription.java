package model;

import java.util.*;

public enum GameObjectDescription {
    WATER('W'), TREE('R'), STONE('S'), GROUND('G'), TANK('T'), PROJECTILE('P');
    
    char tag;
    private static Map<Character, GameObjectDescription> stringToDescriptionMapping;
    
    private GameObjectDescription(char tag) {
        this.tag = tag;
    }
    
    public char getTag() {
        return tag;
    }
    
    public static GameObjectDescription getDescription(char tag) {
        if (stringToDescriptionMapping == null) {
            initMapping();
        }
        return stringToDescriptionMapping.get(tag);
    }
    
    private static void initMapping() {
        stringToDescriptionMapping = new HashMap<Character, GameObjectDescription>();
        for (GameObjectDescription s : values()) {
            stringToDescriptionMapping.put(s.tag, s);
        }
    }
}
