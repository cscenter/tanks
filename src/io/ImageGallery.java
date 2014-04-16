package io;

import model.*;

import java.util.*;

import javax.imageio.*;

import java.io.*;
import java.awt.*;

public class ImageGallery {
    
    private Image backgroundImage;
    private Image waterImage;
    private Image stoneImage;
    private Image heartImage;
    private Image treeImage;
    private EnumMap<Direction, Image> greenTankImage;
    private EnumMap<Direction, Image> redTankImage;
    private EnumMap<Direction, Image> projectileImage;
    
    public ImageGallery(String spritesDestination) throws MapIOException {
        try {
            backgroundImage = ImageIO.read(new File(spritesDestination + "//ground//ground.png"));
            waterImage = ImageIO.read(new File(spritesDestination + "//water//water.png"));
            stoneImage = ImageIO.read(new File(spritesDestination + "//stone//stone.png"));
            treeImage = ImageIO.read(new File(spritesDestination + "//tree//tree.png"));
            heartImage = ImageIO.read(new File(spritesDestination + "//health//heart.png"));
        } catch (IOException e) {
            throw new MapIOException("Cannot load image of Immovable Object");
        }
        greenTankImage = new EnumMap<Direction, Image>(Direction.class);
        redTankImage = new EnumMap<Direction, Image>(Direction.class);
        projectileImage = new EnumMap<Direction, Image>(Direction.class);
        
        Collection<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        directions.remove(Direction.NONE);
        
        for (Direction d : directions) {
            try {
                String filename;
                filename = spritesDestination + "//tank//red//tank" + d.toString() + ".png";
                redTankImage.put(d, ImageIO.read(new File(filename)));
                filename = spritesDestination + "//tank//green//tank" + d.toString() + ".png";
                greenTankImage.put(d, ImageIO.read(new File(filename)));
                filename = spritesDestination + "//projectile//projectile" + d.toString() + ".png";
                projectileImage.put(d, ImageIO.read(new File(filename)));
            } catch (IOException e) {
                throw new MapIOException("Cannot load image of Movable Object");
            }
        }
    }
    
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    public Image getHeartImage() {
        return heartImage;
    }
    
    public Image getImage(GameObject obj) {
        switch (obj.getDescription()) {
            case WATER:
                return waterImage;
            case STONE:
                return stoneImage;
            case TREE:
                return treeImage;
            case TANK:
                Tank t = (Tank) obj;
                if (t.getTeam() == 1) {
                    return greenTankImage.get(t.getOrientation());
                } else {
                    return redTankImage.get(t.getOrientation());
                }
            case PROJECTILE:
                return projectileImage.get(((Projectile)obj).getOrientation());
            default:
                return null;
        }
    }
}