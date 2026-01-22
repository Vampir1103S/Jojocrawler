package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;
import java.util.Queue;
import java.util.ArrayDeque;

public class StoryTeller extends Entity {

    private boolean ETrue;
    private SpriteSheet spriteSheet1;

    private int direction = 0;
    private double timer = 0;

    private Queue<String> dialogQueue;

    public StoryTeller(int x, int y, double hp, int speed, double stamina, int defense,
                       String name, int width, int height) {

        super(x, y, hp, speed, stamina, defense, name, width, height);


        dialogQueue = new ArrayDeque<>();
        spriteSheet1 = new SpriteSheet("Storyteller-Sprite.png", 2, 1);
    }

    /**
     * Fügt eine neue Dialogzeile zur Dialog-Warteschlange hinzu.
     * <p>
     * Die Dialogzeilen werden in der Reihenfolge ihres Hinzufügens
     * gespeichert und später nacheinander ausgegeben,
     * z.B. beim Sprechen mit einem NPC.
     *
     * @param dialog die Dialogzeile, die hinzugefügt werden soll
     */

    public void addDialogLine(String dialog) {
        dialogQueue.add(dialog);
    }

    public void draw(DrawTool drawTool){
       

        spriteSheet1.draw(drawTool,xpos,ypos,5);
        spriteSheet1.setCurrent(0,direction);
    }
    /**
     * Gibt die nächste Dialogzeile des NPCs zurück.
     * <p>
     * Die Methode entnimmt die nächste verfügbare Dialogzeile
     * aus der Dialog-Warteschlange und gibt sie zurück.
     * Ist keine Dialogzeile mehr vorhanden, wird ein
     * entsprechender Hinweistext geliefert.
     * Zusätzlich wird die Blickrichtung des NPCs gesetzt,
     * um eine passende Ausrichtung beim Sprechen zu ermöglichen.
     *
     * @return die nächste Dialogzeile oder ein Hinweistext,
     *         falls keine weiteren Dialoge vorhanden sind
     */

    public String speak(){
        if (dialogQueue.isEmpty()) {
            return "(NPC hat nichts mehr zu sagen)";
        }
        direction = 1;
        return dialogQueue.poll();
    }

    public void update(double dt) {
    }

    public boolean getETrue() {
        return ETrue;
    }

    public void setETrue(boolean ETrue) {
        this.ETrue = ETrue;
    }
}
