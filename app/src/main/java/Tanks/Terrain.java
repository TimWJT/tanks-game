package Tanks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Terrain class is the solid terrain for the tank game.
 * It has functions which transforms the terrain to fit the size specifications of the program from a text file with length of 28.
 * 
 * It has functions to transform, smooth, and add trees and tanks.
 * 
 */
public class Terrain {
    protected char[][] terrainDataSimple;
    protected String terrainFilePath;
    private char[][] terrainDataTransformed;
    public int length;
    
    /**
     * COnstructor for terrain object
     * @param terrainFilePath file path to the 28 pretransformed terrain file
     */
    public Terrain(String terrainFilePath) {
        this.terrainFilePath = terrainFilePath;
        if (terrainFilePath != "") {

            terrainDataSimple = loadTerrain(this.terrainFilePath);
        }
    }
    /**
     *  gives location of tanks on the transformed terrain
     * @param transformedTerrain the post transformed terrain
     * @return hashmap of the tank's id as key and their location as value
     */
    public static HashMap<Character, Integer[]> getTanksLocation(char[][] transformedTerrain) {

        HashMap<Character, Integer[]> playerHashMap = new HashMap<>();

        for (int rows = 0; rows < transformedTerrain.length; rows ++) {
            for (int cols = 0; cols < transformedTerrain[rows].length; cols+=32) {
                if (transformedTerrain[rows][cols] != 'X' && transformedTerrain[rows][cols] != '\u0000' && transformedTerrain[rows][cols] != 'T') {
                    Integer[] tankLocation = {rows, cols};
                    playerHashMap.put(transformedTerrain[rows][cols], tankLocation);
                }
            }
        }
        return playerHashMap;
    }


    /**
     * adds trees back to the transformed terrain after being removed due to the transformaion.
     * @param originalTerrain origional terrain array
     * @param simpleTerrain the simple terrain which was gotten from the file
     * @return transformed terrain with trees and tanks added.
     */
    public static char[][] addTreesAndTanksBack(char[][] originalTerrain, char[][] simpleTerrain) {

        char c;
        char[][] addedTTTerrain = originalTerrain;
    
        for (int rows = 0; rows < simpleTerrain.length; rows++) {
            for (int cols = 0; cols < simpleTerrain[rows].length; cols++) {
                if (simpleTerrain[rows][cols] != 'X' && simpleTerrain[rows][cols] != '\u0000' && simpleTerrain[rows][cols] != ' ') {
                // if (Character.isLetter(simpleTerrain[rows][cols]) || Character.isDigit(simpleTerrain[rows][cols])) {
                    //move everything up till found 'X' (bring everying up)
                    c = simpleTerrain[rows][cols];
                    // System.out.println(c);
                    for (int i = 0; i < 32; i++) {
                        int j = 0;
                        while(addedTTTerrain[rows*32 - j][cols*32 + i] == 'X') {
                            j ++;
                        }
                        if (addedTTTerrain[rows*32 - j+1][cols*32 + i] == 'X') {
                            addedTTTerrain[rows*32 - j][cols*32 + i] = c;
                        }

                    }
                    //bring everything down
                    for (int i = 0; i < 32; i++) {
                        int j = 0;
                        while(addedTTTerrain[rows*32 + j][cols*32 + i] != 'X') {
                            j ++;
                        }
                        if (addedTTTerrain[rows*32 + j-1][cols*32 + i] == '\u0000') {
                            addedTTTerrain[rows*32 + j][cols*32 + i] = c;
                        }

                    }
                }
            }
        }
        return addedTTTerrain;
    }
    
    /**
     * checks if there was a collision with the terrain. used by tanks and projectiles
     * @param x x coordinate of the terrain
     * @param y y coordinate of the terrain
     * @return true/false depdneding on if it collides or not
     */
    public boolean isCollision(int x, int y) {
        //check if there's a collision at coordinates (x, y) with the terrain
        
        if (y < 0 || y >= terrainDataTransformed.length || x < 0 || x >= terrainDataTransformed[0].length) {
            return true; //treat out-of-bounds as a collision
        }

        return terrainDataTransformed[y][x] == 'X'; 
    }
    
    public void setTerrainDataTransformed(char[][] newTerrainDataTransformed) {
        this.terrainDataTransformed = newTerrainDataTransformed;
    }

    public void setTerrainDataSimple(char[][] newTerrainDataSimple) {
        this.terrainDataSimple = newTerrainDataSimple;
    }
    /**
     * loads terrain into 2d array from a file
     * @param currentTerrainFullPath the relative path of the file
     * @return  2d array of terrain
     */
    public static char[][] loadTerrain(String currentTerrainFullPath) {
        try {
            File file = new File(currentTerrainFullPath);
            Scanner scanner = new Scanner(file);

            char[][] terrain = new char[20][28];

            int row = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                for (int col = 0; col < line.length(); col++) {
                    terrain[row][col] = line.charAt(col);
                }
                row++;
            }
            scanner.close();

            return terrain;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public char[][] getTerrainDataSimple() {
        return terrainDataSimple;
    }

    public char[][] getTerrainDataTransformed() {
        return terrainDataTransformed;
    }

    /**
     * transformes terrain from the one gottem from the file to one whihc has size that matches the window size.
     * @param originalTerrain original terrain from the file
     * @return trnasofmred terrain which is around 864 by 640
     */
    public static char[][] transformTerrain(char[][] originalTerrain) {
        // int rows = originalTerrain.length;
        int rows = 20;
        int cols = 28;

        char[][] transformedTerrain = new char[rows * 32][cols * 32];
        
        

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < originalTerrain[i].length; j++) {
                char c = originalTerrain[i][j];
                // for (int k = 0; k < 32; k++) {
                    for (int l = 0; l < 32; l++) {
                        transformedTerrain[i * 32][j * 32 + l] = c;
                        
                    }
                // }
            }
        }
        return transformedTerrain;
    }

    /**
     * applys the moving average transformation to the transformed terrain
     * @param originalTerrain original terrain which has  to tbe transformed
     * @return  the averaged terrain
     */

    public static char[][] applyMovingAvg(char[][] originalTerrain) {
        int rows = originalTerrain.length;
        int cols = originalTerrain[0].length;
        int movingAverage = 32;
        char[][] averagedTerrain = new char[rows][cols];

        //for each col of averaged terrain
        for (int i = 0; i < cols; i++) { 
            long sum = 0;

        //find average of 32 original terrain

        //note j is col, k is row

        // check if its gonna go out of bounds
            if ((896 - i) < 32) {
                movingAverage = 896 - i;
            }

            for (int col = i; col < movingAverage+i; col++) {
                for (int row = 0; row < originalTerrain.length; row++) {
                    // System.out.print(grid[row][col] + ", ");
                    if (originalTerrain[row][col] == 'X') {
                        // System.out.print(row);
                        // System.out.print(' ');
                        sum = sum + row;
                    }


                }
            }


            int average = (int) Math.ceil((double) sum / movingAverage);

            averagedTerrain[average][i] = 'X'; // correct


        }
        return averagedTerrain;
    }


    /**
     * fills empty space in the original terrain with 'X'
     * @param originalTerrain   original terrain which has to be transformed first
     * @return  the filled terrain without empty spaces
     */
    public static char[][] fillTerrain(char[][] originalTerrain) {
        int rows = originalTerrain.length;
        int cols = originalTerrain[0].length;
        char[][] filledTerrain = originalTerrain;

        for (int j = 0; j < cols; j++) {
            for (int i = rows-1; i >= 0; i--) {
                if (originalTerrain[i][j] == 'X') {
                    // System.out.print(row);
                    // System.out.print(' ');
                    break;
                }
                filledTerrain[i][j] = 'X';
            }
        }

        return filledTerrain;
    }


}
