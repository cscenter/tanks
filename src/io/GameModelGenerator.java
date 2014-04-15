package io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import model.GameObjectDescription;
import model.Vector2D;

public class GameModelGenerator {
    
    private static Random generator = new Random();
    
    public static void createMap(int width, int height, String filename) {
        GameObjectDescription[][] map = new GameObjectDescription[height][];
        for (int i = 0; i < height; ++i) {
            map[i] = new GameObjectDescription[width];
        }
        
        List<GameObjectDescription> cellVariants = new ArrayList<>();
        for (GameObjectDescription desc : GameObjectDescription.values()) {
            cellVariants.add(desc);
        }
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.GROUND));
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.TANK));
        cellVariants.remove(cellVariants.indexOf(GameObjectDescription.PROJECTILE));
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                map[i][j] = cellVariants.get(generator.nextInt(cellVariants.size()));
            }
        }
        
        int firstGroundCellsCount = 1 * (width * height) / (width + height);
        List<Vector2D> firstGroundCells = new ArrayList<>();
        for (int i = 0; i < firstGroundCellsCount; ++i) {
            Vector2D v = new Vector2D(generator.nextInt(height), generator.nextInt(height));
            firstGroundCells.add(v);
            map[v.getX()][v.getY()] = GameObjectDescription.GROUND;
        }
        for (int i = 0; i < firstGroundCellsCount - 1; ++i) {
            List<Vector2D> path = findPath(map, firstGroundCells.get(i), firstGroundCells.get(i + 1));
            for (Vector2D cell : path) {
                map[cell.getX()][cell.getY()] = GameObjectDescription.GROUND;
            }
        }
        
        printMap(map, filename);
    }
    
    private static void printMap(GameObjectDescription[][] map, String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        writer.println(Integer.toString(map.length) + " " + Integer.toString(map[0].length));
        
        String preparedOutput = Arrays.deepToString(map).replaceAll("],", "],\n");
        preparedOutput = preparedOutput.replaceAll("]", "").replaceAll("\\[", "");
        preparedOutput = preparedOutput.replaceAll(",", "");
        preparedOutput = preparedOutput.replaceAll("\n ", "\n");
        
        for (GameObjectDescription desc : GameObjectDescription.values()) {
            preparedOutput = preparedOutput.replaceAll(desc.toString(), "" + desc.getTag());
        }
        
        writer.print(preparedOutput);
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
        
        Stack<Vector2D> stack = new Stack<>();
        Map<Vector2D, Vector2D> pred = new HashMap<>();
        stack.push(a);
        visited[a.getX()][a.getY()] = true;
        while (!stack.empty()) {
            Vector2D cell = stack.pop();
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
                stack.push(v);
            }
        }
        
        while (!b.equals(a)) {
            b = pred.get(b);
            resultPath.add(b);
        }
        return resultPath;
    }
    
}
