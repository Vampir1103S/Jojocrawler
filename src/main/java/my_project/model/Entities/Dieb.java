package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.control.Controller;

import java.awt.*;

public class Dieb extends Enemy {
    private Controller controller;
    private double height;
    private double width;
    public Dieb() {
        super(100, 200, 50, 0.7, 10, 20, "maron",50,100);
        this.width = width;
        this.height = height;
    }



    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos,ypos, 50, 100);

    }

    public void update(double dt) {
        super.update(dt);

    }

}