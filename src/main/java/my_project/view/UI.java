package my_project.view;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;

import java.awt.*;


public class UI extends GraphicalObject {


    public UI() {

    }

    public void draw(DrawTool drawTool){
        drawStartScreen(drawTool);
    }
    public void drawStartScreen(DrawTool drawTool){
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawFilledRectangle(0,0, 1920, 1080);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.formatText("Algerian", 1, 300);
        drawTool.drawText(Config.WINDOW_WIDTH/2-450, Config.WINDOW_HEIGHT/2-150, "START");
    }



}
