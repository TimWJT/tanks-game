package Tanks;
/**
 * The large projectile powerup
 * larger explosion radius for next shot
 */
public class LargeProjectile extends PowerUp {
    /**
     * constructor for big projectile object
     */
    public LargeProjectile() {
        super(20, 'x');
    }
/**
 * uses the big projectile powerup for the tank
 * @param tank the tank which used the powerup
 */
    @Override
    public void use(Tank tank) {
        int tankScore = tank.getScore();
        if (tankScore >= cost) {
            tank.setLargeProjectileFlag(true);
        tank.changeScore(-cost);
    } else {
        System.out.println("not enough points to shoot large projectile.");
        }
    }
}