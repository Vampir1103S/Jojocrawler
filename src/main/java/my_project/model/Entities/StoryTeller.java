package my_project.model.Entities;

import KAGO_framework.view.DrawTool;

import java.awt.*;

public class StoryTeller extends Entity{
    public StoryTeller() {
        super(500,500,0,0,0,0,"Tomole",0,1);
    }
    private boolean ETrue;
    public void draw(DrawTool drawTool){
        drawTool.setCurrentColor(Color.YELLOW);
        drawTool.drawFilledRectangle(xpos, ypos, 30, 60);
    }

    public void speak(){
        System.out.print("Hello bin da");
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
