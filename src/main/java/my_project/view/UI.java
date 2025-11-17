package my_project.view;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.control.Controller;

import java.awt.*;
import java.awt.event.MouseEvent;


public class UI extends InteractiveGraphicalObject {

    boolean startscreen = true;
    public UI() {

    }

    public void draw(DrawTool drawTool){
        drawStartScreen(drawTool);
    }

    public void drawStartScreen(DrawTool drawTool){

        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawFilledRectangle(0,0, 1920, 1080);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.formatText("Algerian", 2, 300);
        drawTool.drawText(Config.WINDOW_WIDTH/2-450, Config.WINDOW_HEIGHT/2-150, "START");
    }

    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {

            System.out.println(e.getButton());
            Controller.switchScene(1);
        }
    }
}

