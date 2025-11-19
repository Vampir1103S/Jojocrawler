package my_project.view;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.control.Controller;

import java.awt.*;
import java.awt.event.MouseEvent;


public class Deathscreen extends InteractiveGraphicalObject {

    boolean deathscreen = false;
    private double alpha = 255;
    private double alphaTimer = 0;
    private double alphaAnimation = 255;
    private double xPos = Config.WINDOW_WIDTH/2;
    private double yPos = Config.WINDOW_HEIGHT/2;


    public Deathscreen() {

    }

    public void draw(DrawTool drawTool){
        drawStartScreen(drawTool);
        drawStartScreen(drawTool);
    }

    public void drawStartScreen(DrawTool drawTool){

        drawTool.setCurrentColor(0, 0, 0, (int) alphaAnimation);
        drawTool.drawFilledRectangle(0,0, 1920, 1080);
        drawTool.setCurrentColor(255,192,203, (int) 255);
        drawTool.formatText("Algerian", 2, 200);
        drawTool.drawText(xPos-300, yPos-150, "DEATH");
        drawTool.setCurrentColor(255, 255, 130, 255);
        drawTool.drawText(xPos-450, yPos+30, "CRAWLER");
        drawTool.formatText("Algerian", 4, 50);
        drawTool.setCurrentColor(255,255,255,(int) alpha);
        drawTool.drawText(xPos-200, yPos+350, "Click to Start");
    }

    @Override
    public void update(double dt){
        alphaTimer += dt;

        if (alphaTimer < 2) {
            alpha -= 95*dt;
        }
        if (alphaTimer >= 2) {
            alpha += 80*dt;
        }
        if (alphaTimer >= 4) {
            alphaTimer = 0;
        }

        if (deathscreen == true) {
            startScreenAn(dt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {
            deathscreen = false;
        }
    }

    public void startScreenAn(double dt){
        yPos -= 850*dt;
        alphaAnimation -= 220*dt;
        if (yPos < -400){
            Controller.switchScene(1);
            alphaAnimation = 0;
        }
    }
}

