package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import java.awt.*;
import java.util.Queue;
import java.util.ArrayDeque;

public class StoryTeller extends Entity {

    private boolean ETrue;


    private Queue<String> dialogQueue;

    public StoryTeller(int x, int y, double hp, int speed, double stamina, int defense,
                       String name, int width, int height) {

        super(x, y, hp, speed, stamina, defense, name, width, height);


        dialogQueue = new ArrayDeque<>();
    }


    public void addDialogLine(String dialog) {
        dialogQueue.add(dialog);
    }

    public void draw(DrawTool drawTool){
        drawTool.setCurrentColor(Color.YELLOW);
        drawTool.drawFilledRectangle(xpos, ypos, 30, 60);
    }

    public void speak(){
        if (dialogQueue.isEmpty()) {
            System.out.println("(NPC hat nichts mehr zu sagen)");
        }

        if (!dialogQueue.isEmpty()){
         System.out.println(dialogQueue.poll());
        }
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
