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


    public void addDialogLine(String dialog) {
        dialogQueue.add(dialog);
    }

    public void draw(DrawTool drawTool){
       

        spriteSheet1.draw(drawTool,xpos,ypos,5);
        spriteSheet1.setCurrent(0,direction);
    }

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
