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
    private int alpha = 255;
    private double alphaTimer = 0;

    public UI() {

    }

    public void draw(DrawTool drawTool){
        drawStartScreen(drawTool);
    }

    public void drawStartScreen(DrawTool drawTool){

        drawTool.setCurrentColor(0, 0, 0, 255);
        drawTool.drawFilledRectangle(0,0, 1920, 1080);
        drawTool.setCurrentColor(255,192,203, alpha);
        drawTool.formatText("Algerian", 2, 200);
        drawTool.drawText(Config.WINDOW_WIDTH/2-300, Config.WINDOW_HEIGHT/2-150, "JOJO'S");
        drawTool.setCurrentColor(255, 255, 130, alpha);
        drawTool.drawText(Config.WINDOW_WIDTH/2-450, Config.WINDOW_HEIGHT/2+30, "CRAWLER");
        drawTool.formatText("Algerian", 4, 50);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawText(Config.WINDOW_WIDTH/2-200, Config.WINDOW_HEIGHT/2+350, "Click to Start");
    }

    @Override
    public void update(double dt){
        alphaTimer += dt;

        if (alphaTimer > 0 && alphaTimer < 5) {
            alpha -= 10*dt;
        }
        if (alphaTimer > 5){
            alpha += 20*dt;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {

            System.out.println(e.getButton());
            Controller.switchScene(1);
        }
    }
}

