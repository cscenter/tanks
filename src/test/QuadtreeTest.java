package test;

import java.util.HashSet;
import java.util.Set;

import model.GameObject;
import model.GameObjectDescription;
import model.Vector2D;
import model.map.Quadtree;
import model.map.QuadtreeNode;

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
    public void clear() {
        toAdd.clear();
    }
    
    @Before
    public void prepareTest() {
        toAdd.add(createNode(5, 5, 1));
    }
    
    @Test
    public void test1() {
        Quadtree tree = new Quadtree(100, 100, 1);
        for (QuadtreeNode node : toAdd) {
            tree.insert(node);
        }
    }
    
    @Test
    public void test2() {
        
    }
}
