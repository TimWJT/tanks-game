package Tanks;
/**
 * The additional fuel powerup
 * gives more fuel to tank in cost of points
 */
public class AdditionalFuel extends PowerUp {
    /**
     * constructor for additional fuel object
     */
    public AdditionalFuel() {
        super(10, 'f');
    }
/**
 * uses the additional fuel powerup for the tank
 * @param tank the tank which used the powerup
 */
    @Override
    public void use(Tank tank) {
        int tankScore = tank.getScore();
        if (tankScore >= cost) {

        int currentFuel = tank.getFuel();
        tank.setFuel(currentFuel + 200);
        tank.changeScore(-cost);
    } else {
        System.out.println("not enough points to get fuel.");
        }
    }
}