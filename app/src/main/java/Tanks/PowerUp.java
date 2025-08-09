package Tanks;

/**
 * represents a powerup in the game
 * its a abstract class to implement use method and each of the pwoerups atrtributes
 */
public abstract class PowerUp {
    protected int cost;
    protected char keyBind;
    /**
     * constructor  for the powerup class
     * @param cost the cost of the powerup
     * @param keyBind the keybind to activate the powerup
     */
    public PowerUp(int cost, char keyBind) {
        this.cost = cost;
        this.keyBind = keyBind;
    }
/**
 * the use method to use the pwoerup
 * @param tank the tank which used the powerup
 */
    public abstract void use(Tank tank);

}
