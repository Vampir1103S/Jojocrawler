package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Dieb extends Enemy {

    private SpriteSheet spriteSheet1;

    public Dieb() {
        super(500, 200, 50, 1, 10, 20, "maron", 50, 100);
        spriteSheet1 = new SpriteSheet("Dieb-Sprite.png", 4, 4);
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.GREEN);
        drawTool.drawFilledRectangle(xpos, ypos, width, height);
        drawTool.drawRectangle(xpos, ypos, width, height);

        spriteSheet1.draw(drawTool, xpos, ypos, 5);

        // 🔴 Attackbox NUR beim Angriff
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
    }
}
