package Tanks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

public class TankTest {

    @Test
    public void testConstructor() {

        //tests that the tank was set up properly
        String[] colourArray = {"255", "0", "0"};
        
        Tank tank = new Tank(null, 100, 200, new Terrain(""), colourArray, 1, 'A');
        assertEquals(100, tank.getX());
        assertEquals(200, tank.getY());
        assertArrayEquals(colourArray, tank.getColourArray());
        assertEquals(1, tank.getParacutes());
        assertEquals('A', tank.getTankID());
    }

    @Test
    public void testSetAndGetDead() {
        //testing thje setters and getters for dead boolean
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');

        // dead status to true
        tank.setDead(true);

        //check if dead status is true
        assertTrue(tank.getDead());

        //set status to false
        tank.setDead(false);

        // check dead status is false
        assertFalse(tank.getDead());
    }

    @Test
    public void testGetColourArray() {
        //testing getter for colour of tank array
        String[] colourA = new String[]{"255", "255", "255"};
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        assertArrayEquals(colourA, tank.getColourArray());
    }

    @Test 
    public void testSetAndGetPrintScore() {
        //testing setters and gettedrs for print score bool
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');


        //test setting and getting true
        tank.setPrintScore(true);

        assertTrue(tank.getPrintScore());
        //test setting and getting false
        tank.setPrintScore(false);
        assertFalse(tank.getPrintScore());
    }

    @Test
    public void testSetAndGetSpeed() {
        //test setting and getting speed
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        //setting speed to 1 and then check if it true
        tank.setSpeed(1);
        assertEquals(1, tank.getSpeed());

    }
    

    @Test
    public void testSetAndGetDamagedBy() {
        //testing set and get damage by function
        Tank tank1 = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        Tank tank2 = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'B');

        //set damaged by to tank2 for tank 1
        tank1.setDamagedBy(tank2);
        //check if  tank1 was damged by tank 2
        assertEquals(tank2, tank1.getDamagedBy());
    }

    @Test
    public void testChangeHealth() {

        //testing cahngeing the health of each tank
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setHealth(100);
        int healthChange = tank.changeHealth(-50);
        assertEquals(-50, healthChange);
        assertEquals(50, tank.getHealth());
    }

    @Test
    public void testChangePower() {
        //testing changing the power of tank function
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setHealth(100);
        tank.changePower(20);
        assertEquals(70, tank.getPower());
        tank.changePower(-70);
        assertEquals(0, tank.getPower());
    }

    public void testMove() {
        Terrain terrain = new Terrain("..\\app\\level1.txt");
        Tank tank = new Tank(null, 4, 1, terrain, new String[]{"255", "255", "255"}, 1, 'A');
        tank.setFuel(100);
        

        //move tank side to side
        tank.move(1);
        assertEquals(6, tank.getX());
        tank.move(-1);
        assertEquals(4, tank.getX());
    }

    @Test
    public void testTurn() {
        //testing turning of barrel
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.turn(0.5f); 
        assertTrue(tank.getAngle() > -1.5708f);

        tank.turn(-1.0f);
        assertTrue(tank.getAngle() < -1.5708f);
    }


    @Test
    public void testGetandSetDisplayParachute() {
        //test for displaying parachute using setter and getter

        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        assertFalse(tank.getDisplayParachute());
        tank.setDisplayParachute(true);
        assertTrue(tank.getDisplayParachute());
    }

    @Test
    public void testDisplayExplosionGraphics() {
        //tests graphics display
        PApplet parent = new PApplet(); 
        Terrain terrain = new Terrain(""); 
        Tank tank = new Tank(parent, 0, 0, terrain, new String[]{"255", "255", "255"}, 1, 'A');
        tank.displayExplosionGraphics(30);
        assertEquals(1, tank.explosionGraphicDuration);
    }

    
    @Test
    public void testGetSetX() {
        //setting and getting x position
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setX(100);
        assertEquals(100, tank.getX());
    }

    @Test
    public void testGetSetY() {
        //setting and getting y position
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setY(200);
        assertEquals(200, tank.getY());
    }

    @Test
    public void testGetSetAngle() {
        //setting and getting tank angle
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setAngle(-0.1f);
        assertEquals(-0.1f, tank.getAngle());
    }

    @Test
    public void testGetSetPower() {
        //setting and getting power
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setPower(75);
        assertEquals(75, tank.getPower());
    }

    @Test
    public void testGetSetHealth() {
        //ssetting and getting healthj
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setHealth(80);
        assertEquals(80, tank.getHealth());
    }

    @Test
    public void testGetSetParachutes() {
        //setting and getting parachute number
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 3, 'A');
        assertEquals(3, tank.getParacutes());
    }

    @Test
    public void testGetSetFuel() {
        //setting and getting amount of fuel
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setFuel(150);
        assertEquals(150, tank.getFuel());
    }

    @Test
    public void testGetSetScore() {
        //setting and getting their score
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setScore(500);
        assertEquals(500, tank.getScore());
    }

    @Test
    public void testGetSetTankID() {
        //settgina dn getting tank id
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        assertEquals('A', tank.getTankID());
    }

    @Test
    public void testGetSetDisplayParachute() {
        //test setting and getting the display parachute bool
        Tank tank = new Tank(null, 0, 0, new Terrain(""), new String[]{"255", "255", "255"}, 1, 'A');
        tank.setDisplayParachute(true);
        assertEquals(true, tank.getDisplayParachute());
    }
}
