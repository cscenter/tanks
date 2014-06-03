package test;

import io.GameModelGenerator;
import model.ModelException;

import org.junit.Test;

public class GameModelGeneratorTest {
    
    @Test
    public void testSmallMapGeneration() throws ModelException {
        int w = 10, h = 10;
        for (int i = 0; i < 100; ++i) {
            GameModelGenerator.createMap(w, h, ".randomTestMap.txt", 5);
        }
    }
    
    @Test(expected = ModelException.class)
    public void TestTooManyBotsFail() throws ModelException {
        int w = 25, h = 25;
        GameModelGenerator.createMap(w, h, ".randomTestMap.txt", 626);
    }
    
    @Test
    public void testAverageMapGeneration() throws ModelException {
        int w = 40, h = 40;
        for (int i = 0; i < 10; ++i) {
            GameModelGenerator.createMap(w, h, ".randomTestMap.txt", 5);
        }
    }
    
    @Test
    public void testHugeMapGeneration() throws ModelException {
        int w = 250, h = 250;
        GameModelGenerator.createMap(w, h, ".randomTestMap.txt", 5);
    }
}
