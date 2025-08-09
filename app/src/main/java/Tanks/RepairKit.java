//repair kit
package Tanks;
/**
 * repair kit class is a powerup which tanks can use by pressing a certain key assigned
 * 
 * it inherites from the powerup class
 * 
 * repair kit restores health
 */
public class RepairKit extends PowerUp{
    /**
     * contrcutor for repairkit class
     */
    public RepairKit() {
        super(20, 'r');
    }
    /**
     * uses the repair kit
     * 
     * the method will deduct the score from the tank and will add health to the tank 
     * Health added will not excced 100
     * @param tank the tank object which used the powerup
     */
    @Override
    public void use(Tank tank) {
        int tankScore = tank.getScore();
        if (tankScore >= cost) {
            int currentHealth = tank.getHealth();
            int newHealth = currentHealth + 20;
            if (newHealth > 100) {
                newHealth = 100;
            }
            tank.setHealth(newHealth);
            tank.changeScore(-cost);
            } else {
            System.out.println("Not enough points to buy repair kit.");
            }
        }
    }
    

