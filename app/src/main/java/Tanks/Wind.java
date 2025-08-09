package Tanks;

/**
 * Wind class is the wind effect on projectiles
 * it sotres the magnitude of the wind 
 * Methods include setters and getters of wind effect magnitude
 */
public class Wind {
    private int magnitude;
    /**
     * constructor for wind class
     * @param magnitude magnitude is the acceleration of the projectile in the x direction
     */
    public Wind(int magnitude) {
        this.magnitude = magnitude;
    }
    /**
     * getter for magnitude
     * @return returns magnitude
     */
    public int getMagnitude() {
        return magnitude;
    }
    /**
     * setter for magnidtude
     * @param magnitude the nmagnitude needded to be changed to
     */
    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * used to change the magnitude
     * @param change the cahnge amount
     */
    public void changeMagnitude(int change) {
        magnitude += change;
    }
}