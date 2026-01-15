package my_project.model.items.Consumables;

import KAGO_framework.view.DrawTool;

import java.awt.*;
import java.awt.event.MouseEvent;

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
