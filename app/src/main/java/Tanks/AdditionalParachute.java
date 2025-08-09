package Tanks;
/**
 * The additional parachute powerup
 * gives more parachute to tank in cost of points
 */
public class AdditionalParachute extends PowerUp {
    /**
     * constructor for additional parachute object
     */
    public AdditionalParachute() {
        super(15, 'p');
    }
/**
 * uses the additional parachute powerup for the tank
 * @param tank the tank which used the powerup
 */
    @Override
    public void use(Tank tank) {
        int tankScore = tank.getScore();
        if (tankScore >= cost) {

        int currentParachutes = tank.getParacutes();
        tank.setParachutes(currentParachutes + 1);
        tank.changeScore(-cost);
    } else {
        System.out.println("not enough points to get parachutes.");
        }
    }
}