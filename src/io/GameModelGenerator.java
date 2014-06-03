package io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import model.GameModel;
import model.GameObjectDescription;
import model.ModelException;
import model.Vector2D;

public class GameModelGenerator {
    
    private static Random generator = new Random();
    
    public static void createMap(int width, int height, String filename, int botsCount) throws ModelException {
        
        if (botsCount >= width * height) {
            throw new ModelException("Too many bots.");
        }
        
        GameObjectDescription[][] map = new GameObjectDescription[height][];
        for (int i = 0; i < height; ++i) {
            map[i] = new GameObjectDescription[width];
        }
        
        List<GameObjectDescription> cellVariants = new ArrayList<>();
        for (GameObjectDescription desc : GameObjectDescription.values()) {
            cellVariants.add(desc);
        }
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.BONUS));
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.GROUND));
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.GRASS));
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.TANK));
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.PROJECTILE));
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                map[i][j] = cellVariants.get(generator.nextInt(cellVariants.size()));
            }
        }
        
        int firstGroundCellsCount = 2 * (width * height) / (width + height);
        List<Vector2D> firstGroundCells = new ArrayList<>();
        for (int i = 0; i < firstGroundCellsCount; ++i) {
            Vector2D v = new Vector2D(generator.nextInt(height), generator.nextInt(height));
            firstGroundCells.add(v);
            map[v.getX()][v.getY()] = GameObjectDescription.getRandomBackground();
        }
        for (int i = 0; i < firstGroundCellsCount - 1; ++i) {
            List<Vector2D> path = findPath(map, firstGroundCells.get(i), firstGroundCells.get(i + 1));
            for (Vector2D cell : path) {
                map[cell.getX()][cell.getY()] =  GameObjectDescription.getRandomBackground();
            }
        }
        
        printMap(map, filename, botsCount);
    }
    
    private static void printMap(GameObjectDescription[][] map, String filename, int botsCount) throws ModelException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new ModelException("Map generation error.");
        } catch (UnsupportedEncodingException e) {
            throw new ModelException("Map generation error.");
        }
        
        writer.println(GameModel.ModelType.INFINITE.toString());
        writer.println(Integer.toString(map.length) + " " + Integer.toString(map[0].length));
        
        String preparedOutput = Arrays.deepToString(map).replaceAll("],", "],\n");
        preparedOutput = preparedOutput.replaceAll("]", "").replaceAll("\\[", "");
        preparedOutput = preparedOutput.replaceAll(",", "");
        preparedOutput = preparedOutput.replaceAll("\n ", "\n");
        
        for (GameObjectDescription desc : GameObjectDescription.values()) {
            preparedOutput = preparedOutput.replaceAll(desc.toString(), "" + desc.getTag());
        }
        
        writer.println(preparedOutput);
        
        writer.print(botsCount);
        writer.close();
        
    }

    private static List<Vector2D> findPath(GameObjectDescription[][] map,
            Vector2D a, Vector2D b) {
        List<Vector2D> resultPath = new ArrayList<>();
        int h = map.length;
        int w = map[0].length;
        boolean[][] visited = new boolean[h][];
        for (int i = 0; i < h; ++i) {
            visited[i] = new boolean[w];
        }
        
        Queue<Vector2D> queue = new LinkedList<>();
        Map<Vector2D, Vector2D> pred = new HashMap<>();
        queue.add(a);
        visited[a.getX()][a.getY()] = true;
        while (!queue.isEmpty()) {
            Vector2D cell = queue.poll();
            if (cell.equals(b)) {
                break;
            }
            List<Vector2D> toAdd = new ArrayList<>();
            if (cell.getY() > 0 && !visited[cell.getX()][cell.getY() - 1]) {
                Vector2D v = cell.sub(0, 1);
                visited[v.getX()][v.getY()] = true;
                toAdd.add(v);
                pred.put(v, cell);
            }
            if (cell.getX() > 0 && !visited[cell.getX() - 1][cell.getY()]) {
                Vector2D v = cell.sub(1, 0);
                visited[v.getX()][v.getY()] = true;
                toAdd.add(v);
                pred.put(v, cell);
            }
            if (cell.getX() + 1 < h && !visited[cell.getX() + 1][cell.getY()]) {
                Vector2D v = cell.add(1, 0);
                visited[v.getX()][v.getY()] = true;
                toAdd.add(v);
                pred.put(v, cell);
            }
            if (cell.getY() + 1 < w && !visited[cell.getX()][cell.getY() + 1]) {
                Vector2D v = cell.add(0, 1);
                visited[v.getX()][v.getY()] = true;
                toAdd.add(v);
                pred.put(v, cell);
            }
            Collections.shuffle(toAdd);
            for (Vector2D v : toAdd) {
                queue.add(v);
            }
        }
        
        while (!b.equals(a)) {
            b = pred.get(b);
            resultPath.add(b);
        }
        return resultPath;
    }
    
}
