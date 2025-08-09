package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * the main class which runs the tanks game
 * it is an extension of the PApplet class
 */
public class App extends PApplet {

    /**
     * initialising set up variables
     * 
     */
    //provided set up varaibles
    public static final int CELLSIZE = 32;  //originally 32
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH; // originally 864 
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR; // originally 640
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20; // originally 20 
    public static final int FPS = 30;
    public static final int INITIAL_PARACHUTES = 1; // was 1

    public String configPath;
    public static Random random = new Random();
	
    //my variables
    //tanks
    public boolean[] removedTanksIndexes;
    public int removedTanksCount;
    public static Map<Character, Integer> tankScores = new HashMap<>();
    private Tank winnerTank;
    private String[] winnerTankColourArray;
    String[] tankColourArray;
    String tankColourString;
    int tankCount;
    Tank tankScoreCurrentlyHighest;
    
    HashMap<Character, Integer[]> tanksHashMap;
    public static Tank[] tanks; 

    //turns
    private int playerTurn;
    private int totalTurns;
    
    //delays
    public int levelChangeDelay;
    public int endScreenDelay;
    public int endScreenDisplayIndex;
    private boolean displayEndScreenFlag;

    //terrain
    Terrain terrain;
    private char[][] terrainInApp;
    int treesRandomOffSet;
    
    //projectiles
    private HashSet<Projectile> projectilesHashSet;
    public static Wind wind;

    /**
     * loads powerups
     */
    PowerUp repairKit;
    PowerUp additionalFuel;
    PowerUp additionalParachutes;
    PowerUp largeProjetile;

    /**
     * loads the images to set up the game
     */
    PImage backgroundImage; 
    PImage tree;
    PImage parachuteImage;
    PImage windLeftImage;
    PImage windRightImage;
    PImage fuelImage;
    
    /**
     * varaibles for data in json.
     * shows the current level details
     */
    int currentLevelNumber = 0;
    String currentMapFile;
    String[] currentForegroundColour;
    String currentTreeFile;
    
    
    
    /**
     * constructor for app class which uses config.json as the path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * loads the images terrain and tanks to set up the game
     */
	@Override
    public void setup() {
        frameRate(FPS);
        // Load the JSON configuration file

        JSONObject configObject = loadJSONObject(configPath);

        JSONArray levels = configObject.getJSONArray("levels");
        
        JSONObject level = levels.getJSONObject(currentLevelNumber);

        JSONObject tankColours = configObject.getJSONObject("player_colours");

        //parachute image
        parachuteImage = loadImage("Tanks/parachute.png");

        //wind image
        windRightImage = loadImage("Tanks/wind-1.png");
        windLeftImage = loadImage("Tanks/wind.png");

        //fuel image
        fuelImage = loadImage("Tanks/fuel.png");

        //background file and image
        currentMapFile = level.getString("background");
        backgroundImage = loadImage("Tanks/" + currentMapFile);


        //foreground (terrain) colour
        currentForegroundColour = level.getString("foreground-colour").split(",");

        //read the terrain details
        String currentTerrainFile = level.getString("layout");
        String currentTerrainFullPath = "..\\app\\" + currentTerrainFile;

        //tree files
        if (level.hasKey("trees")) {
            currentTreeFile = level.getString("trees");
            tree = loadImage("Tanks/" + currentTreeFile);
        }
        
        //creating terrain
        terrain = new Terrain(currentTerrainFullPath);
        // terrain.printTerrainDataSimple();
        terrainInApp = terrain.getTerrainDataSimple();
        terrainInApp = Terrain.transformTerrain(terrainInApp);
        terrainInApp = Terrain.applyMovingAvg(terrainInApp);
        terrainInApp = Terrain.applyMovingAvg(terrainInApp);
        terrainInApp = Terrain.fillTerrain(terrainInApp);
        terrain.setTerrainDataTransformed(terrainInApp);
        terrainInApp = Terrain.addTreesAndTanksBack(terrainInApp, terrain.getTerrainDataSimple());
        
        
        //initalising tanks

        //reset player turns
        playerTurn = 0;
        totalTurns = 0;
        tankCount = 0;
        
        
        tanksHashMap = Terrain.getTanksLocation(terrainInApp);
        tanks = new Tank[tanksHashMap.size()];
        int i = 0;
        for (Map.Entry<Character, Integer[]> entry: tanksHashMap.entrySet()) {
            Integer[] tankLocation = entry.getValue();
            Character tankID = entry.getKey();
            int tankX = tankLocation[1];
            int tankY = tankLocation[0];
            
            
            if (tankColours.hasKey(tankID.toString())) {
                tankColourString = tankColours.getString(tankID.toString());
                if (tankColourString.equals("random")) {
                    
                    tankColourArray = new String[3];
                    for (int j = 0; j < 3; j++) {
                        tankColourArray[j] = Integer.toString(random.nextInt(256));
                    }
                    
                } else {
                    try {
                        tankColourArray = tankColourString.split(",");
                        
                        
                    } catch (NumberFormatException e) {
                        System.err.println("NumberFormatException occured. Please check JSON tank colour data.");
                        System.exit(0);
                    }
                }
            }
            // System.out.println(tankScores);
            tanks[i] = new Tank(this, tankX, tankY, terrain, tankColourArray, INITIAL_PARACHUTES, tankID);
            if (!tankScores.containsKey(tanks[i].getTankID())) {
                tankScores.put(tanks[i].getTankID(), 0);
            } else {
                tanks[i].setScore(tankScores.get(tanks[i].getTankID()));
                // System.out.println("set " + tanks[i].getTankID() + " score to " + tankScores.get(tanks[i].getTankID()) );
            }

            tankCount++;
            i++;
        }
        projectilesHashSet = new HashSet<>();


        treesRandomOffSet = random.nextInt(30);
 
        //wind
        wind = new Wind((int) random(-35, 35));

        //powerups
         repairKit = new RepairKit();
         additionalFuel = new AdditionalFuel();
        additionalParachutes = new AdditionalParachute();
        largeProjetile = new LargeProjectile();

         //resetting varaibles
         removedTanksIndexes = new boolean[tankCount];
         removedTanksCount = 0;
         levelChangeDelay = 0;
         endScreenDelay = 0;
         endScreenDisplayIndex = 0;
    }
    
    boolean projectileFlag = false;
    /**
     * Handles key inputs
     */
	@Override
    public void keyPressed(KeyEvent event){
        char key = event.getKey();
        int keyCode = event.getKeyCode();
        
        System.out.println(keyCode);


        if (keyCode == 78 && currentLevelNumber <= 1) { // 'n'
            currentLevelNumber += 1;
            setup();
        } else
        if (keyCode == 77 && currentLevelNumber > 0) { // 'm'
            currentLevelNumber -= 1;
            setup();
        } else
        if (keyCode == 76) { // 'l'
            treesRandomOffSet = random.nextInt(30);
            tankScores = new HashMap<>();
            displayEndScreenFlag = false;
            setup();
        } else
        if (keyCode == 32 && levelChangeDelay <= 0) { //spacebar ' '
            //check if tank has been removed
 
            //hashset for projectiles
            Projectile newProjectile = new Projectile(this, tanks[playerTurn], terrain);
            tanks[playerTurn].setLargeProjectileFlag(false);
            tanks[playerTurn].setArrowDuration(0);
            newProjectile.setIsInAir(true);
            projectilesHashSet.add(newProjectile);
            System.out.println("tank shot at X: " + tanks[playerTurn].getX() + " y: " + tanks[playerTurn].getY());
            totalTurns++;
            playerTurn = totalTurns % tankCount;
            
            //change wind
            wind.changeMagnitude((int) random(-5, 5));
            
            while (removedTanksIndexes[playerTurn]) {
                totalTurns++;
                playerTurn = totalTurns % tankCount;
            }
        } 
        
        
         if (keyCode == 82) { // 'r'
         if(winnerTank !=null) {
            currentLevelNumber = 0;
            tankScores = new HashMap<>();
            displayEndScreenFlag = false;
            endScreenDelay = 0;
            endScreenDisplayIndex = 0;
            setup();
         } else
         if(tanks[playerTurn].getHealth() <100) {
             repairKit.use(tanks[playerTurn]); // repair kit

         }
            

        } else if (keyCode == 70) { // 'f'
            additionalFuel.use(tanks[playerTurn]); // additional fuel
            
        }  else if (keyCode == 80) { // 'p'
        additionalParachutes.use(tanks[playerTurn]); // additional parachute
        
        } else if (keyCode == 88 && !tanks[playerTurn].getLargeProjectileFlag()) { // 'x'
        largeProjetile.use(tanks[playerTurn]); // additional parachute
        
        } else if (keyCode == 90) { //'z'
            tanks[playerTurn].setScore((100));
        }

        if (keyCode == 38) { //up arrow
            tanks[playerTurn].turn(-0.08f); // barrel angle upwards //was -0.1
        } else
        if (keyCode == 40) { // down arrow
            tanks[playerTurn].turn(0.08f); // barrel angle down //was -0.1
        } else
        if (keyCode == 37) {//left arrow
            tanks[playerTurn].move(-1); //move tank left
        } else
        if (keyCode == 39) { //right arrow
            tanks[playerTurn].move(1); // move tank left
        } else


        if (keyCode == 87) {  // 'w' 
            tanks[playerTurn].changePower(1); //incrase power
        } else 
        if (keyCode == 83) { // 's'
            tanks[playerTurn].changePower(-1);  //decrease power
        } 
        if (keyCode == 79) { // 'o'
        currentLevelNumber = 0;
        tankScores = new HashMap<>();
        displayEndScreenFlag = false;
        endScreenDelay = 0;
        endScreenDisplayIndex = 0;
        setup();
    } 
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
    int playerTurnInDraw;
     float angle = 0; 
	@Override
    public void draw() {
        playerTurnInDraw = (playerTurn + 3) % 4;
        image(backgroundImage, 0, 0, width, height);
 

        // actual terrain
        noStroke();
        fill(Integer.parseInt(currentForegroundColour[0]),
                    Integer.parseInt(currentForegroundColour[1]),
                    Integer.parseInt(currentForegroundColour[2])); // fill map colour

        beginShape();
        vertex(0,HEIGHT+2);
        //i is cols left to right, j is rows going top to down
        for (int i = 0; i < terrainInApp[0].length; i++) {
            for (int j = 0; j < terrainInApp.length; j++) {


                // j%(32)==0+treesRandomOffSet == treesRandomOffSet
                if (terrainInApp[j][i] == 'T' && (i - treesRandomOffSet) % 32 == 0) {
                    image(tree, i-22, j-50, 50, 50);  // j = x axis, i = y axis (original: -25,-50,50,50)
                    // fill(0);
                    // rect(j, i, 1, 1);
                    break;

                } else
                if (terrainInApp[j][i] == 'X' || j == 639) {                        
                        vertex(i, j);
                    
                    break;
                } 
            }
        }

        vertex(WIDTH, HEIGHT+2);
        endShape();

        //tank display
        for (int i = 0; i < tankCount; i++ ) {
            if (tanks[i].getHealth() <=0) {
                if (!removedTanksIndexes[i]) {
                    removedTanksIndexes[i] = true;
                    removedTanksCount++;
                    tanks[i].setDead(true);
                }

                if (removedTanksCount >= tankCount-1) {
                    levelChangeDelay++;

                    if (levelChangeDelay >= 30) {
                        if (currentLevelNumber < 2) {
                            currentLevelNumber++;
                            setup();
                            break;
                        } else if (currentLevelNumber >= 2) {

                                displayEndScreenFlag = true;
                        }
             
                    }
                }
                if (removedTanksIndexes[playerTurn]) {
                    totalTurns++;
                    playerTurn = totalTurns % tankCount;
                }
                
            }
            if (tanks[i] != null && !removedTanksIndexes[i]) {
                tanks[i].display();
                tanks[i].initaliseFall();
                //check if hit bottom of map
                if (tanks[i].getY() >= 639) {
                    tanks[i].damageNearbyTanks(30);
                    // tanks[i].setHealth(0);
                    tanks[i].setDead(true);
                }

                //check tank fall
                if( tanks[i].getDisplayParachute()) {
                    image(parachuteImage, tanks[i].getX()-25, tanks[i]. getY()-55, 50, 50);
                }
            }
            if (tanks[i].getDead()) {
                if (tanks[i].getY() >=637) {
                    tanks[i].displayExplosionGraphics(30);
                } else {
                    tanks[i].displayExplosionGraphics(15);

                }
            }
        }

        //projectile display

            //list to hold projectiles to be removed

        HashSet<Projectile> projectilesToRemove = new HashSet<>();

        for (Projectile projectile : projectilesHashSet) {
            if (projectile.getIsInAir()) {
                //check if the projectile is out of bounds
                projectile.trajectory();
                // projectile.checkTerrainClipping(terrainInApp);

                if (projectile.getX() <= 1 || projectile.getX() >= WIDTH ||
                    projectile.getY() <= 1 || projectile.getY() >= HEIGHT) {
                        //ADD outof bounds projectiles to the remove list
                    projectilesToRemove.add(projectile);
                    continue;
                }

                if (projectile.checkCollision()) {
                    terrainInApp = projectile.destoryTerrain(terrainInApp);
                    terrain.setTerrainDataTransformed(terrainInApp);
                    projectile.setIsInAir(false);
                    projectile.setDisplayExplosionGraphics(true);
                } else {
                    projectile.display();
                }
            }

            if (projectile.getDisplayExplosionGraphics()) {
                if(projectile.asLargeProjectile) {
                    projectile.explosionGraphics(60);

                } else {
                    projectile.explosionGraphics(30);
                }
            } else if (projectile.getExplosionGraphicsDuration() >= 6) {
                projectilesToRemove.add(projectile); // removes projctile if its exploded
            }
        }

        projectilesHashSet.removeAll(projectilesToRemove);
    
        //wind text
        textSize(18);
        fill(0);
        text(Math.abs(wind.getMagnitude()), 815, 40);

        //wind image
        if (wind.getMagnitude() > 0) {
            image(windLeftImage, 760, 10, 40, 40);
        } else {
            image(windRightImage, 760, 10, 40, 40);
        }
        //player turn text
        textSize(18);
        fill(0);
        text("Player " + tanks[playerTurn].getTankID() + "'s turn", 20, 40);


        //display fuel image
        image(fuelImage, 160, 10, 40, 40);
        //display fuel amount
        textSize(18);
        fill(0);
        text(tanks[playerTurn].getFuel(), 210, 40);

        //display parachute image
        image(parachuteImage, 160, 60, 40, 40);
        //display parachute amount
        textSize(18);
        fill(0);
        text(tanks[playerTurn].getParacutes(), 210, 90);

        //health text
        textSize(18);
        fill(0);
        text("Health: ", 400, 40);

        //health amount
        textSize(18);
        fill(0);
        text(tanks[playerTurn].getHealth(), 640, 40);

        //health display black ground
        fill(0); 
        rect(470 - 1, 15 - 1, 160 + 2, 30 + 2); 

        //health display tank colour health
        String[] currentTankColourArray = tanks[playerTurn].getColourArray();
        fill(Integer.parseInt(currentTankColourArray[0]),
        Integer.parseInt(currentTankColourArray[1]),
        Integer.parseInt(currentTankColourArray[2]));
        rect(470, 15, 160* tanks[playerTurn].getHealth()/100, 30 );


        //power text
        textSize(18);
        fill(0);
        text("Power: ", 400, 80);

        //power amount
        textSize(18);
        fill(0);
        text(tanks[playerTurn].getPower(), 640, 80);

        //power display black background
        fill(0); 
        rect(470 - 1, 55 - 1, 160 + 2, 30 + 2); 

        //power display
        fill(120); 
        rect(470, 55, 160 * tanks[playerTurn].getPower()/100, 30 );
        
        //score board outline
        if(!displayEndScreenFlag) {
        
                //small rectangle 
            stroke(0); 
            strokeWeight(3); 
            noFill(); 
            rect(740, 90, 120, 20); 
            
                    //big rectangle
            stroke(0); 
            strokeWeight(3); // thickness
            noFill(); 
            rect(740, 110, 120, 20 * tankCount); 
            

            //score board title texts
            textSize(18);
            fill(0);
            text("Scores", 743, 107);

            //player titles

            for(int i = 0; i < tanks.length; i++) {
                String[] displayTankColourArray = tanks[i].getColourArray();
                textSize(18);
                fill(Integer.parseInt(displayTankColourArray[0]),
                Integer.parseInt(displayTankColourArray[1]),
                Integer.parseInt(displayTankColourArray[2]));
                text("Player " + tanks[i].getTankID(), 743, 127 + 20 * i);

                fill(0);
                text(tanks[i].getScore(), 823, 127 + 20 * i);
            }
    } else { // if displayEndScreenFlag == true
        //find winner/highest score
        if (winnerTank == null) {
            winnerTank = findWinner();
            textSize(30);
            fill(0);
            text("No one Wins", 300, 120);
            fill(100);
        } 
        if (winnerTank != null) {

            winnerTankColourArray = winnerTank.getColourArray();
            
            //display conngrats text
            textSize(30);
            fill(Integer.parseInt(winnerTankColourArray[0]),
            Integer.parseInt(winnerTankColourArray[1]),
            Integer.parseInt(winnerTankColourArray[2]));
            text("Player " + winnerTank.getTankID() + " wins!", 300, 120);
            
            fill((int) (Integer.parseInt(winnerTankColourArray[0])),
            (int)  (Integer.parseInt(winnerTankColourArray[1])),
            (int)  (Integer.parseInt(winnerTankColourArray[2])), 70);
        }
        //end screen scorebaord red blackground
        // fill(255, 179,200);
        rect(232, 140, 400, 40+40*tankCount); 

        //end screen scoreboard outline
                //small rectangle 
        stroke(0); 
        strokeWeight(4); 
        noFill(); 
        rect(232, 140, 400, 40); 
        
                //big rectangle
        stroke(0); 
        strokeWeight(4); // thickness
        noFill(); 
        rect(232, 180, 400, 40 * tankCount); 

        //final scores text
        textSize(30);
        fill(0);
        text("Final Scores", 245, 170);

        //extra keybinds reminder
        textSize(20);
        fill(0);
        text("\n L: referesh stage \n n: next level \n m: previous level \n o: restart game on demand \n r: restart game after game end \n\n Thanks for playing :)" , 10, 400);


        // sort and display each player
        endScreenDelay++;
        endScreenDisplayIndex = (int) Math.floor(endScreenDelay/21);
        if (endScreenDisplayIndex > tanks.length) {
            endScreenDisplayIndex = tanks.length;
        } 
            for(int i = 0; i < endScreenDisplayIndex; i++) {

                String[] displayTankColourArray = tanks[i].getColourArray();
                textSize(30);
                fill(Integer.parseInt(displayTankColourArray[0]),
                    Integer.parseInt(displayTankColourArray[1]),
                    Integer.parseInt(displayTankColourArray[2]));
                text("Player " + tanks[i].getTankID(), 242, 208 + 40 * i);
            
                fill(0);
                text(tanks[i].getScore(), 542, 208 + 40 * i);
            }
 


        
        }
        //arrow on top of tank
        tanks[playerTurn].displayArrow();
 
        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action

        if (tanks[playerTurn].getLargeProjectileFlag()) {
            textSize(18);
            fill(0);
            text("Large Projectile", 20, 70);
    
        }
    }
    /**
     * finds the winning tank by score
     * @return  returns the tank who won
     */
    public Tank findWinner() {
        Tank winner = null;
        int highestScore = 0;
    
        for (Tank tank : tanks) {
            int currentScore = tank.getScore();
            if (currentScore > highestScore) {
                highestScore = currentScore;
                winner = tank;
            }
        }
        
        return winner;
    }

    /**
     * main function
     * @param args command line arguments
     */
    public static void main(String[] args) {
        PApplet.main("Tanks.App");

        
    }


}