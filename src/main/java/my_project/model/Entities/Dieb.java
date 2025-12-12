package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.control.Controller;

import java.awt.*;

public class Dieb extends Enemy {
    private Controller controller;
    public Dieb() {
        super(100, 1000, 50, 0.7, 10, 20, "maron",50,50);

    }

    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos,ypos, width, height);
    }

    public void update(double dt) {
        super.update(dt);
    }

}