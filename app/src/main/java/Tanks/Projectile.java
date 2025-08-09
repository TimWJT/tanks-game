package Tanks;

import processing.core.PApplet;
/**
 * projectile class are the projectiles fired by tanks
 * the has methods to calclaute the movement of the projectile, the destruction of terrain, and damage to tanks
 */
public class Projectile {
    protected float x;
    protected float y;
    protected float gravity = 0.24f; //3.6 pixels per second per frame // found 0.24 is good
    protected float angle;
    protected PApplet parent;
    protected int power;
    protected Terrain terrain;
    protected String[] colourArray;
    protected Tank tank;
    protected float velocityInitial;
    protected float velocityX = 5f;
    protected float velocityY = -10f;
    protected boolean isInAir = false;
    protected boolean asLargeProjectile = false;
    protected boolean displayExplosionGraphics;
    protected int explosionGraphicDuration = 0;

    /**
     * constructor for projectile class
     * @param parent the papplet reference
     * @param tank  the tank who fired the projectile
     * @param terrain the terrain to be modified by explosino
     */

    public Projectile(PApplet parent, Tank tank, Terrain terrain) {
        this.parent = parent;
        this.tank = tank;
        this.terrain = terrain;
        this.angle = tank.getAngle();
        this.power = tank.getPower();
        
        this.x = (float) (tank.getX()+15*Math.cos(-angle));
        this.y= (float) (tank.getY()+15*Math.sin(angle));
        this.colourArray = tank.getColourArray();


        // velocityInitial = (float) Math.ceil(power/6);
        velocityInitial =(float) (3 + power * 0.14); // good with 0.24 g, 3 ,0.14
        velocityX = (float) (velocityInitial* Math.cos(angle));
        velocityY = (float) (velocityInitial* Math.sin(angle));
        if(tank.getLargeProjectileFlag()) {
            asLargeProjectile = true;
        }
    }
    /**
     * the duraction of the explosion
     * @return the expliosn graphics duration
     */
    public int getExplosionGraphicsDuration() {
        return this.explosionGraphicDuration;
    }
    public void setDisplayExplosionGraphics (boolean setStatus) {
        this.displayExplosionGraphics = setStatus;
    }
    public boolean getDisplayExplosionGraphics () {
        return this.displayExplosionGraphics;
    }
    /**
     * displays the projectile which is a circiel of the tank's colour
     */
    public void display() {
        parent.fill(Integer.parseInt(colourArray[0]),
        Integer.parseInt(colourArray[1]),
        Integer.parseInt(colourArray[2]));

        parent.ellipse(x, y, 14,14);

    }

    /**
     * the explosion graphics
     * it is 3 circles of yellow orange and red 
     */
    public void explosionGraphics(int radius) {
        if (explosionGraphicDuration >= 4) {
            parent.pushMatrix();
            parent.fill(255, 0, 0);
            parent.ellipse(x , y , 2*radius, 2*radius);
            parent.popMatrix();
        } 
        
        if (explosionGraphicDuration >= 2){
            parent.pushMatrix();
            parent.fill(255, 165, 0);
            parent.ellipse(x , y , radius, radius);
            parent.popMatrix();
        } 
        
        if (explosionGraphicDuration >= 0) {
            parent.pushMatrix();
            parent.fill(255, 255, 0);
            parent.ellipse(x , y , radius/2, radius/2);
            parent.popMatrix();
        } 
        
        if (explosionGraphicDuration >= 6) {
            setDisplayExplosionGraphics(false);
        }
        explosionGraphicDuration++;
        
            
    }   
    
    
/**
 * math for the trajectory of the projectile which is affected by gravity and wind
 */
    public void trajectory() {
        //shoot from the barrel

        velocityX += App.wind.getMagnitude() *0.001;
        velocityY += gravity;
        y += velocityY;
        x += velocityX;

        // System.out.println("x: " + x + "y: " + y);
        // System.out.println("tank's location x: " + tank.getX() + "y: " + tank.getY());
        
    }
    
    
    public int getX() {
        return (int) this.x;
    }
    public int getY() {
        return (int) this.y;
    }

    public boolean getIsInAir() {
        return isInAir;
    }
    public void setIsInAir(boolean setStatus) {
        this.isInAir = setStatus;
    }
    /**
     * checks the colision when it has touched the terrain
     * @return returns boolean whether it has touched terrain
     */
    public boolean checkCollision() {
        if (terrain.isCollision((int)x, (int)y)) {
            
            y = Math.round(y) ;
            x = Math.round(x);
            System.out.println("Collision occured at x: " + x + "y: " + y);

            return true;
        }
        return false;
    }

    /**
     * destorys the terrain in a raidus of 30
     * @param originalTerrain the original terrain before destruction
     * @return  the destoryed terrain
     */
    public char[][] destoryTerrain(char[][] originalTerrain) {
        float explosionSquared;
        double differenceSqrt;
        int explosionDistance = 30; // replace all 30 by this
        if (asLargeProjectile) {
            explosionDistance = 60;
            System.out.println("large explosion boom");
        } 
        for (int cols = 0; cols < originalTerrain[0].length; cols++) {
            for (int rows = originalTerrain.length-1; rows > 0; rows--) {
                 float explosionX =  Math.abs(cols - x);
                 float explosionY =  Math.abs(rows - y);
    
                 explosionSquared = explosionX * explosionX + explosionY * explosionY;
                // int squaredDistance = (int) (this.x * this.x + this.y * this.y);
                // squaredDistance = (float) Math.sqrt(squaredDistance);
                differenceSqrt = Math.sqrt(explosionDistance*explosionDistance - explosionX*explosionX);

                if (explosionSquared <= explosionDistance*explosionDistance) {
                    // originalTerrain[cols][rows] = '\u0000';
                    if (originalTerrain[rows][cols] == 'X') {
                        originalTerrain[rows][cols] = '\u0000';

                    } else if (originalTerrain[rows][cols] == 'T'){
                        int treeShift = 0;
                        while (rows + treeShift < originalTerrain.length - 1 && originalTerrain[rows + treeShift + 1][cols] == '\u0000') {
                            treeShift++;
                        }
                        //move the tree to the lowest position
                        originalTerrain[rows + treeShift][cols] = 'T';
                        originalTerrain[rows][cols] = '\u0000';

                    }

                    
                    calculateTankDamage(explosionSquared, rows, cols, explosionDistance);
                    assignFallDamage(rows, cols);

    
                } else if (explosionX <= explosionDistance && y > rows) {
                    if ((originalTerrain[rows][cols] == 'X' || originalTerrain[rows][cols] == 'T') && rows < 639 && originalTerrain[rows+1][cols] == '\u0000') {
                        char c = originalTerrain[rows][cols];
                        originalTerrain[rows][cols] = '\u0000';
                        int diff2 = elementToBottom(explosionX, rows, differenceSqrt);
                        if (diff2 > 639) {
                            diff2 = 639;
                        }
                        originalTerrain[(int) diff2][cols] = c;
                    }
                    assignFallDamage(rows, cols);
                }
            }
        }
        tank.setLargeProjectileFlag(false);
        return originalTerrain;
    }
    /**
     * assigns fall damage to the tank hit by the projectile
     * its used for other tanks to gain points
     * @param rows the row which is being checked
     * @param cols the col which is being checked
     */
    private void assignFallDamage(int rows, int cols) {
        for (Tank tank : App.tanks) {
            float tankX = tank.getX();
            float tankY = tank.getY();
            if (tankX == cols && tankY == rows) {
                tank.setDamagedBy(this.tank);
                // System.out.println(tank.getTankID() + " was damaged by " + this.tank.getTankID());
            }
        }
    }
    /**
     * deals damage to tanks hit by the projectile
     * @param explosionSquared the explosion distance squared
     * @param rows  the row hit
     * @param cols the col hit
     */
    private void calculateTankDamage(float explosionSquared, int rows, int cols, int explosionDistance) {

        float tankDistance;
        for (Tank tank : App.tanks) {
            float tankX = tank.getX();
            float tankY = tank.getY();
            if (tankX == cols && tankY == rows) {
            tankDistance = (float) Math.sqrt((tankX - x) * (tankX - x) + (tankY - y) * (tankY - y));

            //do damage to tank
            int damage = 60 *(explosionDistance -(int) tankDistance)/explosionDistance;

            int attackerScoreGain =  -tank.changeHealth(-damage);

                if (tank != this.tank) {
                    this.tank.changeScore(attackerScoreGain);

                }
            //testing print statements
            //  System.out.println("tanks distance from explosion: " + tankDistance);
            //  System.out.println("damage taken explosion: " + damage);
            }
        }
    }
    /**
     * sends the elemnt of a particular piece of terrain to the bottom to avoid overhangs
     * @param explosionX the x displacement
     * @param rows the current row is being checked
     * @param differenceSqrt the raidus of the explosion
     * @return the y axis coordiante which the element will be sent to.
     */
    private int elementToBottom(float explosionX, int rows, double differenceSqrt) {
        double point2 =  differenceSqrt + y;
        double point1 = -differenceSqrt + y;
        double diff = rows - point1;
        double foundBottom = point2 + diff;
        return (int) foundBottom;

    }
}
