package my_project.model.items;

public class Dagger extends Weapons {

    public Dagger() {
        damage = 9;              // eher wenig
        hitW = 38;               // kleine Hitbox
        hitH = 38;
        offset = 10;             // normaler Abstand
        cooldownMultiplier = 0.45; // sehr schnell (Cooldown wird stark reduziert)
    }

    @Override
    public String toString() {
        return "Dagger";
    }
}
