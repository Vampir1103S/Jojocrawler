package my_project.model.items;

public class Spear extends Weapons {

    public Spear() {
        damage = 14;              // mittel
        hitW = 100;                // schmal, aber
        hitH = 50;                // lang (Reichweite)
        offset = 22;              // großer Abstand nach vorne (Reach)
        cooldownMultiplier = 1.05; // leicht langsamer als Sword
    }

    @Override
    public String toString() {
        return "Spear";
    }
}
