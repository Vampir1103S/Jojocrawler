package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Bosslehrer extends Enemy {

    private SpriteSheet spriteSheet1;
    private int timer = 0;
    private int number = 0;
    private int direction = 0;

    public Bosslehrer(double xpos, double ypos, double hp, double speed, double stamina, int defense, String name, double width,double height) {
        super( xpos,  ypos,  hp, speed,  stamina,  defense, name,  width, height);
        spriteSheet1 = new SpriteSheet("Dieb-Sprite.png", 4, 4);

        // Optional feinjustieren
        this.attackDamage = 30;
        this.attackCooldown = 0.8;
        this.attackDuration = 0.12;
        this.speed = 120;
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos, ypos, width, height);
        drawTool.drawRectangle(xpos, ypos, width, height);

        spriteSheet1.setCurrent(direction, number);
        spriteSheet1.draw(drawTool, xpos, ypos, 5);

        if (attacking) {
            Rectangle2D hb = getAttackHitbox();
            drawTool.setCurrentColor(new Color(255, 0, 0, 120));
            drawTool.drawFilledRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
            drawTool.setCurrentColor(Color.RED);
            drawTool.drawRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
        }

        drawDebugBoxes(drawTool);
    }

    @Override
    public void update(double dt) {
        super.update(dt);


        if (facingX == 1 && facingY == 0) direction = 3;    //oben
        else if (facingX == -1 && facingY == 0) direction = 2;    //links
        else if (facingX == 0 && facingY == -1) direction = 0;    //unten
        else if (facingX == 0 && facingY == 1) direction = 1;    //rechts
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
