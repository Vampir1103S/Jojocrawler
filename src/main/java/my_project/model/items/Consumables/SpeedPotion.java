package my_project.model.items.Consumables;

public class SpeedPotion extends Potions {

    public SpeedPotion() {
        super(1); // falls eure Potions-Menge noch genutzt wird, egal – wir konsumieren per Loadout-Remove
    }

    @Override
    public String toString() {
        return "SpeedPotion";
    }
}
