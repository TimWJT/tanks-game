package Tanks;

import Tanks.PowerUp;
import processing.core.PApplet;
/**
 * tank class which is the individual tanks in the game
 * it has attributes which are unique to the tank (position, angle of barrel, health, score, fuel, power)
 * it handles tanks movements graphics, and terrain manipulation
 */
public class Tank {
    protected int x, y; 
    protected float angle; // barrel direction
    protected int speed; //distance it moves per arrow press
    protected PApplet parent; 
    protected Terrain terrain; // to check collision
    protected String[] colourArray;
    protected int power;
    protected int parachutes;
    protected int fallDamage = 0;
    protected int health = 100;
    protected boolean displayParachute = false;
    protected int fuel;
    protected int score;
    protected char tankID;
    protected int displayArrowDuration; // duration for arrow indicator for turn
    protected Tank damagedBy;
    protected int explosionGraphicDuration;
    protected Tank[] tanks;
    protected boolean largeProjectileFlag = false;
    protected boolean explosionGraphicFlag;
    protected boolean printScore;
    protected boolean dead;

    /**
     * tank constructor
     * @param parent the papple object
     * @param x x coordinate
     * @param y y coordinate
     * @param terrain the terrain map for moving
     * @param colourArray   the colour of the tank
     * @param parachutes     the amount of parachutes
     * @param tankID    the ID of the tank 
     */
    public Tank(PApplet parent, int x, int y, Terrain terrain, String[] colourArray, int parachutes, char tankID) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.angle = -1.5708f;
        this.speed = 2; //default speed//moves 2 pix per press
        this.terrain = terrain;
        this.colourArray = colourArray;
        this.power = 50;
        this.parachutes = 3;
        this.tankID = tankID;
        this.fuel = 250; //250
        this.explosionGraphicDuration = 0;
        this.tanks = App.tanks;
        this.explosionGraphicFlag = true;
        this.printScore = true;
        this.dead = false;

    }
    /**
     * set status of the tanks life
     * @param setStatus
     */
    public void setDead(boolean setStatus) {
        this.dead = setStatus;
    }
    public void setArrowDuration(int duration) {
        this.displayArrowDuration = duration;
    }
    public void setLargeProjectileFlag(boolean setstatus) {
        this.largeProjectileFlag = setstatus;
    }
    public boolean getLargeProjectileFlag() {
        return this.largeProjectileFlag;
    }
    
    public boolean getDead() {
        return this.dead;
    }
    public void setDamagedBy(Tank attacker) {
        this.damagedBy = attacker;
    }
    public Tank getDamagedBy() {
        return this.damagedBy;
    }

    public int getParacutes() {
        return this.parachutes;
    }

    public boolean getPrintScore () {
        return this.printScore;
    }

    public void setPrintScore(boolean  setStatus) {
        this.printScore = setStatus; 
    }
    public char getTankID () {
        return this.tankID;
    }
    public int getScore() {
        return this.score;
    }
    public void setScore(int newScore) {
        this.score = newScore;
    }

    public String[] getColourArray() {
        return this.colourArray;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public float getAngle() {
        return this.angle;
    }
    public int getPower() {
        return this.power;
    }
    public int getHealth() {
        return this.health;
    }
    public void setHealth(int newHealth) {
        this.health = newHealth;
    }
    /**
     * method to display the explosions when the tank dies
     * @param explosionRadius the raidus of the explosion (depends on the reason for death)
     */
    public void displayExplosionGraphics(int explosionRadius) {

        if (explosionGraphicFlag) {
        if (explosionGraphicDuration >= 4) {
            parent.pushMatrix();
            parent.fill(255, 0, 0);
            parent.ellipse(x , y , 2*explosionRadius, 2*explosionRadius);
            parent.popMatrix();
        } 
        
        if (explosionGraphicDuration >= 2){
            parent.pushMatrix();
            parent.fill(255, 165, 0);
            parent.ellipse(x , y , explosionRadius, explosionRadius);
            parent.popMatrix();
        } 
        
        if (explosionGraphicDuration >= 0) {
            parent.pushMatrix();
            parent.fill(255, 255, 0);
            parent.ellipse(x , y , explosionRadius/2, explosionRadius/2);
            parent.popMatrix();
        } 
        if (explosionGraphicDuration >= 6) {
            setExplosionGraphicsFlag(false);
            this.setHealth(0);

        }
  
        explosionGraphicDuration++;
    }
            
    }   
    /**
     * setter for explosion graphics flag
     * @param setStatus status for explosion graphics flag
     */
    public void setExplosionGraphicsFlag(boolean setStatus) {
        this.explosionGraphicFlag = setStatus;
    }
    /**
     * damages near by tanks upon death or due to being out of bounds
     * @param radius the radius of the damage
     */
    public void damageNearbyTanks(int radius) {

        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i] != null && tanks[i] != this && tanks[i].getHealth() > 0) {
                
                double dx = tanks[i].getX() - this.x;
                double dy = tanks[i].getY() - this.y;

                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= radius) {
                    //damage the other tank
                    // tanks[i].changeHealth((int)-(15-distance));
                    int otherTankOriginalHealth = tanks[i].getHealth();
                    tanks[i].setHealth(otherTankOriginalHealth - (int)(radius - distance));
                    // System.out.println("self destruct explosion did: " +(int)(radius - distance));
                    
                }
            }
        }
    }
    /**
     * changes the health of the tank. Used for repairs or taking damage
     * @param changeAmount the amount to cahnge
     * @return the changed amount
     */
    public int changeHealth(int changeAmount) {
        
        if (changeAmount < 0 && this.health < -changeAmount) {
            changeAmount = -this.health;
            this.health = 0;
            this.power = 0;
            damageNearbyTanks(15);
            displayExplosionGraphics(30);
            return changeAmount;
        }

        this.health += changeAmount;
        if (this.power > health) {
            this.power = health;
        }
        return changeAmount;
    }

    public int getFuel() {
        return this.fuel;
    }
    public void setFuel(int newFuel) {
        this.fuel = newFuel;
    }

    /**
     * used to display the arrow which indicates the turn of the tank
     * 
     */
    public void displayArrow() {
        if (displayArrowDuration < 130 && this.health > 0) {
            parent.pushMatrix();
            parent.translate(x, y-80);
            parent.noFill(); 
            parent.triangle(0, 0, 8, -34, -8, -34); 
            parent.popMatrix();
        }
        displayArrowDuration++;

    }
    /**
     * tanks graphical display 
     * includes base, head and barrel
     * 
     * barrel moves according to the angle
     */
    public void display() {

        //  tank barrel
        parent.pushMatrix();
        parent.translate(x, y-4); //move to tank position
        parent.rotate(angle); // rotate barrel
        parent.fill(0);
        parent.rect(0, -4, 26, 8, 50); //  barrel graphics
        parent.popMatrix();



        //draw tank body
        try {
        parent.fill(Integer.parseInt(colourArray[0]),
        Integer.parseInt(colourArray[1]),
        Integer.parseInt(colourArray[2])); //fill tank colour

        parent.rect(x-8, y-8, 16, 6, 50); // head (dy = - 2 * base change)
        parent.rect(x-11, y-4, 22, 6, 50); // base
        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException occurred. Check JSON tank colour data.");
            System.exit(0);
        }

    }
    public int getSpeed() {
        return this.speed;
    }
        
    /**
     * changes the power of the tank.
     * @param changeAmount the amount to cahnge by
     */
    public void changePower(int changeAmount) {
        if (((changeAmount > 0 && power < 100 && this.power < health) || (changeAmount < 0 && power > 0))) {
        this.power += changeAmount;
        }
    }

    public boolean getDisplayParachute() {
        return this.displayParachute;
    }

    public void setDisplayParachute(boolean setStatus) {
        this.displayParachute = setStatus;
    }

    public void setPower(int newPower) {
        if (newPower >=0 && newPower <= 100) {
            this.power = newPower;
        }
    }
    /**
     * cahnges the score of the tank
     * @param change the amount to change by
     */
    public void changeScore(int change) {
        
        this.score += change;
        App.tankScores.put(this.tankID, this.score);
    }
    /**
     * check the fall status of the tank. 
     * if there isnt terrain beneath, it will fall at different speeds depending on whether they have a parachute or not.
     */
    public void initaliseFall() {
        if (!terrain.isCollision(x,y+1)) {

            // if  (!terrain.isCollision((int) x, (int) y)) {
                if (parachutes <= 0 ) {
                    y += 4;
                } else  {
                    this.setDisplayParachute(true);
                    y += 2;
                }
                fallDamage += 4;

            // }
            
            
        } else {
            this.setDisplayParachute(false);

            if (fallDamage > 0) {
                if (parachutes > 0) {
                    fallDamage = 0;
                    parachutes = parachutes -  1;
                    // System.out.println("lost parachute");
                } else {
                    
                    
                    int attackerScoreGain = -this.changeHealth(-fallDamage);

                    System.out.println("took" + (fallDamage) + " fall dmg");
                    // System.err.println("Heal left: " + health);
                    if (damagedBy != null && damagedBy != this) {
                        damagedBy.changeScore(attackerScoreGain);
                        // int attackerScore = damagedBy.getScore();
                        // damagedBy.setScore(attackerScore+fallDamage);
                    }
                    fallDamage = 0;
                }
                // y-= 2;
                // System.out.println("y value minus");
            }

        }
    }
    /**
     * handles the movement of the tank.
     * 
     * @param direction the direction of the movement. 1 indicates moving to the right, -1 to the left
     */
    public void move(int direction) {
        if (((direction == -1 && x >=8) || (direction == 1 && x <=854)) && this.fuel > 0) {
            x += speed * direction;
            this.fuel -= speed;
                        
                if (this.displayParachute == false) {
                // //if no collision, move the tank down until it collides with the terrain
                while (!terrain.isCollision((int) x, (int) y+1)) {
                    y++;
                }
                // }

                //if collision, move the tank back up till no collision
                while (terrain.isCollision((int) x, (int) y)) {
                    y--; 
                }
            }
        }

    }
    
    //turns the cannon angle
    /**
     * turns the cannon of the tank
     * @param t change of angle
     */
    public void turn(float t) {
        angle += t;
        if (angle  > 0) {
            angle = 0;
        } else if( angle < -3.14) {
            angle = -3.14f;
        }
        
        System.out.println(angle);

    }

    public void setSpeed(int s) {
        speed = s;
    }
    public void setY(int newY) {
        this.y = newY;
    }
    public void setAngle(float f) {
        if (f <0 && f > -3.13) {
            this.angle = f;
        }
    }
    public void setX(int newX) {
        this.x = newX;
    }
    public void setParachutes(int newAmount) {
        this.parachutes = newAmount;
    }
}
