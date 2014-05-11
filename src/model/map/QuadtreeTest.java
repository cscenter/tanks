package model.map;

import java.util.HashSet;
import java.util.Set;

import model.GameObject;
import model.GameObjectDescription;
import model.Vector2D;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QuadtreeTest {

    private static Set<QuadtreeNode> toAdd;
    
    @BeforeClass
    public static void init() {
        toAdd = new HashSet<>();
    }
    
    private QuadtreeNode createNode(int x, int y, int id) {
        Vector2D pos = new Vector2D(x, y);
        GameObject obj = new GameObject(id, pos, GameObjectDescription.STONE);
        return new QuadtreeNode(pos, obj);
    }
    
    @After
    public void clear1() {
        toAdd.clear();
    }
    
    @Before
    public void prepareTest1() {
        toAdd.add(createNode(5, 5, 1));
    }
    
    @Test
    public void test1() {
        Quadtree tree = new Quadtree(100, 100, 1);
        for (QuadtreeNode node : toAdd) {
            tree.insert(node);
        }
    }
    
    @Before
    public void prepareTest2() {
        toAdd.add(createNode(10, 10, 1));
    }
    
    @Test
    public void test2() {
        
    }
    
    @After
    public void clear2() {
        toAdd.clear();
    }
}
