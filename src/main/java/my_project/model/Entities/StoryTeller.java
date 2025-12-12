package my_project.model.Entities;

import KAGO_framework.view.DrawTool;

import java.awt.*;

public class StoryTeller extends Entity{
    public StoryTeller() {
        super(0,0,0,0,0,0,"Tomole",0,1);
    }

    public void draw(DrawTool drawTool){
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos, ypos, 30, 60);
    }


    public void update(double dt) {

    }
}
