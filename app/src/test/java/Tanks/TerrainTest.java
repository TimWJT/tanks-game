package Tanks;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TerrainTest {
    private Terrain terrain;
    char[][] transformedTerrain;

    @BeforeEach
    public void setup() {
        terrain = new Terrain("..\\app\\level1.txt");
        transformedTerrain = Terrain.transformTerrain(terrain.getTerrainDataSimple());
        terrain.setTerrainDataTransformed(transformedTerrain);
    }

    @Test
    public void testIsCollisionTrue() {
        assertTrue(terrain.isCollision(640, 864)); 
        assertFalse(terrain.isCollision(0, 0)); 
        assertFalse(terrain.isCollision(15, 3)); 
    }

    @Test
    public void testGetTankLocation() {
        char[][] mockTerrain = {
            {'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'}
        };
        mockTerrain[1][1] = 'A';
        mockTerrain[2][2] = 'B';
        HashMap<Character, Integer[]> tanksLocation = Terrain.getTanksLocation(mockTerrain);
    
        assertFalse(tanksLocation.containsKey('A'));
        assertFalse(tanksLocation.containsKey('B'));
    
        assertArrayEquals(new Integer[]{1, 1}, tanksLocation.get('A'));
        assertArrayEquals(new Integer[]{2, 2}, tanksLocation.get('B'));
    }
    

    @Test

    public void testApplyMovingAvg() {
        char[][] averagedTerrain = Terrain.applyMovingAvg(terrain.getTerrainDataTransformed());
        assertEquals(terrain.getTerrainDataTransformed().length,averagedTerrain.length );
    }
    @Test
    public void testFillTerrain() {
        char[][] originalTerrain = terrain.getTerrainDataTransformed();
        assertFalse(terrain.isCollision(500, 630));

        char[][]filledTerrain = Terrain.fillTerrain(originalTerrain);
        terrain.setTerrainDataSimple(filledTerrain);
        assertTrue(terrain.isCollision(500, 630));
    }

    @Test
    public void testLoadTerrain() {
        assertEquals(null, Terrain.loadTerrain("not a file path"));
        
    }

    @Test
    public void testAddTreesAndTanksBack() {
        char[][] addedTrees = Terrain.addTreesAndTanksBack(transformedTerrain,terrain.getTerrainDataSimple());
        boolean treesCheck =false;
        for(int i = 0; i < addedTrees.length; i ++) {
            for(int j = 0; j < addedTrees.length; i ++) {
                if (addedTrees[i][j] == 'T') {
                    treesCheck = true;
                }
            }
        }
        assertTrue(treesCheck);
    }

}
