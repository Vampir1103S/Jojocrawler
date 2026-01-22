package my_project.model.items.Consumables;

import KAGO_framework.view.DrawTool;

import java.awt.*;
import java.awt.event.MouseEvent;
/**
 * Konkreter Heiltrank, der zur Wiederherstellung von Lebenspunkten dient.
 * <p>
 * {@code HealingPotion} ist ein verbrauchbares Item und erbt von
 * {@link Potions}. Beim Benutzen löst der Trank einen Heil-Effekt aus,
 * der den Spieler um einen bestimmten Betrag heilt.
 *
 * Die Klasse speichert zusätzlich einen internen Zustand, der angibt,
 * ob der Heiltrank aktuell aktiviert bzw. verwendet wurde.
 */

public class HealingPotion extends Potions {


    private boolean healing = false;
    public HealingPotion() {
        super(3);
    }

    public boolean getHealing() {
        return healing;
    }


    public void setHealing(boolean healing) {
        this.healing = healing;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            System.out.println("Healing Potion");
            healing = true;
        }
    }
}
