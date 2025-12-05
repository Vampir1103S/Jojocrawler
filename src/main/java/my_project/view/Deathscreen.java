package my_project.view;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.control.Controller;

import java.awt.*;
import java.awt.event.MouseEvent;


public class Deathscreen extends InteractiveGraphicalObject {

    boolean deathscreen = true;
    private double alphaAnimation = 255;
    private double xPos = Config.WINDOW_WIDTH/2;
    private double yPos = Config.WINDOW_HEIGHT/2;


    public Deathscreen() {

    }

    public void draw(DrawTool drawTool){
        drawStartScreen(drawTool);
    }

    public void drawStartScreen(DrawTool drawTool){

        drawTool.setCurrentColor(0, 0, 0, (int) alphaAnimation);
        drawTool.drawFilledRectangle(0,0, 1920, 1080);
        drawTool.setCurrentColor(145,0,1, 255);
        drawTool.formatText("Algerian", 2, 200);
        drawTool.drawText(xPos-300, yPos, "DEATH");
        drawTool.formatText("Algerian", 4, 50);
        drawTool.drawText(xPos-200, yPos+450, "Close to Restart");
    }

    @Override
    public void update(double dt){
        if (deathscreen == false) {
            startDeathScreenAn(dt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {
            deathscreen = false;
        }
    }

    public void startDeathScreenAn(double dt){
        yPos -= 850*dt;
        alphaAnimation -= 220*dt;
        if (yPos < -400){
            Controller.switchScene(2);
            alphaAnimation = 0;
        }
    }
}

