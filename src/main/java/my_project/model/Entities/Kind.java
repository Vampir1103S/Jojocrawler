package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.geom.Rectangle2D;
/**
 * Konkreter Gegnertyp, der die allgemeine Gegner-Logik aus {@link Enemy} nutzt
 * und über ein eigenes SpriteSheet dargestellt wird.
 * <p>
 * {@code Kind} übernimmt Bewegung, KI und Angriffssystem von {@link Enemy},
 * enthält jedoch eine eigene grafische Darstellung. Während eines Angriffs
 * kann zusätzlich die Attack-Hitbox (Debug/Visualisierung) angezeigt werden.
 */

public class Kind extends Enemy {

    private int number = 0;
    private double timer = 0;
    private SpriteSheet spriteSheet1;

    public Kind(double xpos, double ypos, double hp, double speed, double stamina, int defense, String name, double width,double height) {
        super( xpos,  ypos,  hp, speed,  stamina,  defense, name,  width, height);
        spriteSheet1 = new SpriteSheet("Kind-Sprite.png", 4, 4);
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
