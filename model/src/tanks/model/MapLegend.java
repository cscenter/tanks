package tanks.model;

import java.util.*;

public enum MapLegend {
    WATER("W"), WOODENWALL("O"), STONEWALL("S"), ASPHALT("A"), TANK("T"), FLAG("F"), BONUS("B");
    
    private String tag;
    private static Map<String, MapLegend> letterToMapLegendMapping;
    
    private MapLegend(String tag) {
        this.tag = tag;
    }
    
    public static MapLegend getMapLegend(String tag) {
        if (letterToMapLegendMapping == null) {
            initMapping();
        }
        return letterToMapLegendMapping.get(tag);
    }
    
    private static void initMapping() {
        letterToMapLegendMapping = new HashMap<String, MapLegend>();
        for (MapLegend s : values()) {
            letterToMapLegendMapping.put(s.tag, s);
        }
    }
}
