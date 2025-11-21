package my_project.model.Entities;

import KAGO_framework.view.DrawTool;

import java.awt.*;

public class StoryTeller extends Entity{
    public StoryTeller(int xpos,int ypos ,double hp, int speed, double stamina, int defense, String Name) {
        super(xpos, ypos ,hp, speed, stamina, defense, Name);
    }

    public void draw(DrawTool drawTool){
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos, ypos, 30, 60);
    }


    public void update(double dt) {

    }
}
