package my_project.model.items;

public class Axe extends Weapons {

    public Axe() {
        damage = 24;             // sehr hoher Schaden
        hitW = 90;               // große Hitbox
        hitH = 75;
        offset = 12;              // eher nah am Spieler
        cooldownMultiplier = 1.55; // deutlich langsamer (Cooldown wird größer)
    }

    @Override
    public String toString() {
        return "Axe";
    }
}
