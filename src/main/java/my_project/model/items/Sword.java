package my_project.model.items;

public class Sword extends Weapons {

    public Sword() {
        damage = 15;
        hitW = 50;
        hitH = 80;
        offset = 12;
        cooldownMultiplier = 1.0;
    }

    @Override
    public String toString() {
        return "Sword";
    }
}
