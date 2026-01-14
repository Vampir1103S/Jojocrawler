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
        spriteSheet1 = new SpriteSheet("Dieb-Sprite.png", 4, 4);
    }


    public void addDialogLine(String dialog) {
        dialogQueue.add(dialog);
    }

    public void draw(DrawTool drawTool){
        drawTool.setCurrentColor(Color.YELLOW);
        drawTool.drawFilledRectangle(xpos, ypos, 30, 60);

        spriteSheet1.draw(drawTool,xpos,ypos,5);
        spriteSheet1.setCurrent(direction,0);
    }

    public void speak(){
        if (dialogQueue.isEmpty()) {
            System.out.println("(NPC hat nichts mehr zu sagen)");
        }

        if (!dialogQueue.isEmpty()){
         System.out.println(dialogQueue.poll());
        }
        direction = 1;
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
