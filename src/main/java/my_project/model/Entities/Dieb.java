package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Dieb extends Enemy {

    private SpriteSheet spriteSheet1;
    private double timer = 0;
    private int number = 0;

    public Dieb(double xpos, double ypos, double hp, double speed, double stamina, int defense, String name, double width,double height) {
        super( xpos,  ypos,  hp, speed,  stamina,  defense, name,  width, height);
        spriteSheet1 = new SpriteSheet("Dieb-Sprite.png", 4, 4);


        this.attackDamage = 10;
        this.attackCooldown = 0.6;
        this.attackDuration = 0.12;
        this.speed = 140;
    }

    @Override
    public void draw(DrawTool drawTool) {


        spriteSheet1.setCurrent(direction, number);
        spriteSheet1.draw(drawTool, xpos, ypos, 5);

        if (attacking) {
            Rectangle2D hb = getAttackHitbox();
            drawTool.setCurrentColor(new Color(255, 0, 0, 120));
            drawTool.drawFilledRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
            drawTool.setCurrentColor(Color.RED);
            drawTool.drawRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
        }


    }

    @Override
    public void update(double dt) {
        super.update(dt);
            animateDieb(dt);
    }

    private void animateDieb(double dt) {
        timer += dt;
        if (timer >= 0.15) {
            number = (number + 1) % 4;
            timer = 0;
        }
    }
}
