package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;
import my_project.model.items.Inventory;
import my_project.model.items.Weapons;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player extends Entity {

    private static final boolean DEBUG_DRAW_NEAR_BOX = true;

    private boolean isDownW, isDownA, isDownS, isDownD;
    private double knockbackStrength = 200;

    private SpriteSheet spriteSheet2;
    private int number = 0;
    private int direction = 0;
    private double timer = 0;

    private int facingX = 0;
    private int facingY = 1;

    private boolean attacking = false;
    private boolean hitDoneThisAttack = false;
    private double attackTimer = 0;
    private double attackDuration = 0.12;
    private double cooldownTimer = 0;
    private double attackCooldown = 0.35;

    private int fallbackDamage = 10;
    private Inventory inventory;

    public Player() {
        super(100, 500, 500, 30, 1, 1, "hehe", 60, 110);
        spriteSheet2 = new SpriteSheet("Player-Sprite.png", 4, 4);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawFilledRectangle(xpos, ypos, width, height);
        drawTool.drawRectangle(xpos, ypos, width, height);

        spriteSheet2.setCurrent(direction, number);
        spriteSheet2.draw(drawTool, xpos - 12, ypos - 10, 5);

        if (DEBUG_DRAW_NEAR_BOX) {
            Rectangle2D near = getEnemyAggroBox();
            drawTool.setCurrentColor(new Color(0, 80, 255, 60));
            drawTool.drawFilledRectangle(near.getX(), near.getY(), near.getWidth(), near.getHeight());
            drawTool.setCurrentColor(new Color(0, 80, 255, 200));
            drawTool.drawRectangle(near.getX(), near.getY(), near.getWidth(), near.getHeight());
        }

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
        if (cooldownTimer > 0) cooldownTimer -= dt;

        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) attacking = false;
        }

        if (isDownW || isDownA || isDownS || isDownD) {
            if (isDownW) direction = 3;
            else if (isDownA) direction = 2;
            else if (isDownS) direction = 0;
            else if (isDownD) direction = 1;
            animatePlayer(dt);
        } else {
            number = 0;
            timer = 0;
        }
    }

    private void animatePlayer(double dt) {
        timer += dt;
        if (timer >= 0.15) {
            number = (number + 1) % 4;
            timer = 0;
        }
    }

    public Rectangle2D getEnemyAggroBox() {
        double rangeX = 60;
        double rangeY = 60;

        return new Rectangle2D.Double(
                xpos - rangeX,
                ypos - rangeY,
                width + rangeX * 2,
                height + rangeY * 2
        );
    }

    public void startAttack() {
        if (!attacking && cooldownTimer <= 0) {
            attacking = true;
            hitDoneThisAttack = false;
            attackTimer = attackDuration;

            double cd = attackCooldown;
            Weapons w = (inventory != null) ? inventory.getSelectedWeapon() : null;
            if (w != null) cd *= w.getCooldownMultiplier();
            cooldownTimer = cd;
        }
    }

    public boolean canDealHitNow() { return attacking && !hitDoneThisAttack; }
    public void markHitDone() { hitDoneThisAttack = true; }

    public int getAttackDamage() {
        Weapons w = (inventory != null) ? inventory.getSelectedWeapon() : null;
        if (w != null) return w.getDamage();
        return fallbackDamage;
    }

    public Rectangle2D getAttackHitbox() {
        double hitW = 60, hitH = 60, offset = 10;

        Weapons w = (inventory != null) ? inventory.getSelectedWeapon() : null;
        if (w != null) {
            hitW = w.getHitW();
            hitH = w.getHitH();
            offset = w.getOffset();
        }

        double x = xpos + width / 2 - hitW / 2;
        double y = ypos + height / 2 - hitH / 2;

        if (facingX == 1) x = xpos + width + offset;
        else if (facingX == -1) x = xpos - hitW - offset;
        else if (facingY == -1) y = ypos - hitH - offset;
        else y = ypos + height + offset;

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }

    public void setFacing(int fx, int fy) { facingX = fx; facingY = fy; }
    public double getKnockbackStrength() { return knockbackStrength; }

    public void setIsDownWTrue() { isDownW = true; }
    public void setIsDownWFalse() { isDownW = false; }
    public void setIsDownATrue() { isDownA = true; }
    public void setIsDownAFalse() { isDownA = false; }
    public void setIsDownSTrue() { isDownS = true; }
    public void setIsDownSFalse() { isDownS = false; }
    public void setIsDownDTrue() { isDownD = true; }
    public void setIsDownDFalse() { isDownD = false; }
}
