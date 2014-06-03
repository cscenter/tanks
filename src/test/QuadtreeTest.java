package test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Random;
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
        
    }
    
    @Test
    public void testSingleAdd() {
        Quadtree tree = new Quadtree(100, 100, 1);
        
        toAdd.add(createNode(1, 1, 1));
        
        for (QuadtreeNode node : toAdd) {
             assertTrue(tree.insert(node));
        }
    }
    
    @Test
    public void test100Additions() {
        Quadtree tree = new Quadtree(100, 100, 1);
        
        Random r = new Random();
        
        
        for (int i = 0; i < 100; ++i) {
            toAdd.add(createNode(r.nextInt(100), r.nextInt(100), 1));
        }
        
        for (QuadtreeNode node : toAdd) {
             assertTrue(tree.insert(node));
        }
    }
    
    @Test
    public void testFailAddition() {
        Quadtree tree = new Quadtree(100, 100, 1);

        toAdd.add(createNode(200, 200, 1));
        
        for (QuadtreeNode node : toAdd) {
             assertFalse(tree.insert(node));
        }
    }
    
    @Test
    public void testSingleDel() {
        Quadtree tree = new Quadtree(100, 100, 1);
        
        Random r = new Random();
        
        
        for (int i = 0; i < 1; ++i) {
            toAdd.add(createNode(r.nextInt(100), r.nextInt(100), 1));
        }
        
        for (QuadtreeNode node : toAdd) {
             assertTrue(tree.insert(node));
        }
        for (QuadtreeNode node : toAdd) {
            assertTrue(tree.remove(node));
        }
    }
    
    @Test
    public void testFailDel() {
        Quadtree tree = new Quadtree(100, 100, 1);

        for (int i = 0; i < 1; ++i) {
            toAdd.add(createNode(5, 5, 1));
        }
        
        for (QuadtreeNode node : toAdd) {
             assertTrue(tree.insert(node));
        }
        for (QuadtreeNode node : toAdd) {
            assertFalse(tree.remove(createNode(5, 6, 1)));
        }
    }
    
    @Test
    public void testMultiDelFail() {
        Quadtree tree = new Quadtree(100, 100, 1);
        
        Random r = new Random();
        QuadtreeNode node = createNode(r.nextInt(100), r.nextInt(100), 1);
        toAdd.add(node);
        
        assertTrue(tree.insert(node));
        assertTrue(tree.remove(node));
        assertFalse(tree.remove(node));
    }
    
    @Test
    public void testMultiDel() {
        int w = 1000;
        Quadtree tree = new Quadtree(w, w, 1);
        
        Random r = new Random();
        for (int i = 0; i < 100; ++i) {
            toAdd.add(createNode(r.nextInt(w), r.nextInt(w), 1));
        }
        
        for (QuadtreeNode node : toAdd) {
            assertTrue(tree.insert(node));
        }
        for (QuadtreeNode node : toAdd) {
            assertTrue(tree.remove(node));
        }
    }
       
}
