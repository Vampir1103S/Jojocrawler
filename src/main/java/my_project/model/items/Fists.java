package my_project.model.items;

public class Fists extends Weapons {

    public Fists() {
        damage = 6;
        hitW = 45;
        hitH = 45;
        offset = 6;
        cooldownMultiplier = 0.7;
    }

    @Override
    public String toString() {
        return "Fists";
    }
}
