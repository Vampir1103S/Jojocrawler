package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.control.Controller;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;

public class Dieb extends Enemy {
    private Controller controller;
    private SpriteSheet spriteSheet1;
    int nummer = 0;
    int richtung = 0;
    double timer = 0;
    public Dieb() {
        super(500, 200, 50, 1, 10, 20, "maron",50,100);
        setWidth(50);
        setHeight(100);

        spriteSheet1 = new SpriteSheet("Dieb-Sprite.png", 4, 4);
    }

    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos,ypos, 50, 100);
        spriteSheet1.draw(drawTool,xpos,ypos,5);
        spriteSheet1.setCurrent(richtung,nummer);
    }

    public void update(double dt) {
        super.update(dt);
        timer = timer + dt;

//        if() {
//
//            moveDieb(dt);
//        }else{
//            nummer = 0;
//            timer = 0;
//        }
    }

    private void moveDieb(double dt) {
        timer += dt;

        if (timer >= 0.15) {
            nummer++;
            timer = 0;

            if (nummer > 3) {
                nummer = 0;
            }
        }
    }
}