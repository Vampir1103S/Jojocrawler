package my_project.model.items;

public abstract class Weapons extends Item {
    protected int damage;
    protected double hitW;
    protected double hitH;
    protected double offset;
    protected double cooldownMultiplier = 1.0;

    public int getDamage() { return damage; }
    public double getHitW() { return hitW; }
    public double getHitH() { return hitH; }
    public double getOffset() { return offset; }
    public double getCooldownMultiplier() { return cooldownMultiplier; }
}
