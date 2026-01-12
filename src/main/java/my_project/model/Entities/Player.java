package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.Config;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player extends Entity {

    private boolean isDownW;
    private boolean isDownA;
    private boolean isDownS;
    private boolean isDownD;
    private double knockbackStrength = 200;


    // Facing für 4 Richtungen
    private int facingX = 0;  // -1 links, +1 rechts
    private int facingY = 1;  // -1 oben, +1 unten (default unten)

    // Angriff
    private boolean attacking = false;
    private boolean hitDoneThisAttack = false;

    private double attackTimer = 0;
    private double attackDuration = 0.12;

    private double cooldownTimer = 0;
    private double attackCooldown = 0.35;

    private int attackDamage = 10;

    public Player() {
        super(100, 500, 500, 1, 1, 1, "hehe", 60, 110);
    }

    public void draw(DrawTool drawTool) {
        // Player
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawFilledRectangle(xpos , ypos , width , height );
        drawTool.drawRectangle(xpos, ypos, width, height);

        // Debug: rote Attackbox
        if (attacking) {
            Rectangle2D hb = getAttackHitbox();
            drawTool.setCurrentColor(new Color(255, 0, 0, 120));
            drawTool.drawFilledRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
        }
    }

    @Override
    public void update(double dt) {

        // Movement + Facing
        if (isDownW && ypos > 0) {
            ypos -= dt * 250;
            facingX = 0;
            facingY = -1;
        }
        if (isDownA && xpos > 0) {
            xpos -= dt * 250;
            facingX = -1;
            facingY = 0;
        }
        if (isDownS && ypos < Config.WINDOW_HEIGHT - height) {
            ypos += dt * 250;
            facingX = 0;
            facingY = 1;
        }
        if (isDownD && xpos < Config.WINDOW_WIDTH - width) {
            xpos += dt * 250;
            facingX = 1;
            facingY = 0;
        }

        // Cooldown
        if (cooldownTimer > 0) cooldownTimer -= dt;

        // Attack Timer
        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) {
                attacking = false;
            }
        }

        if (hp <= 0) {
            System.out.println("TOT");
        }
    }

    // Angriff starten
    public void startAttack() {
        if (cooldownTimer <= 0 && !attacking) {
            attacking = true;
            hitDoneThisAttack = false;
            attackTimer = attackDuration;
            cooldownTimer = attackCooldown;
        }
    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean canDealHitNow() {
        return attacking && !hitDoneThisAttack;
    }

    public void markHitDone() {
        hitDoneThisAttack = true;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    // Attack-Hitbox abhängig von Facing
    public Rectangle2D getAttackHitbox() {
        double hitW = 60;
        double hitH = 60;
        double offset = 10;

        double px = getXpos();
        double py = getYpos();
        double pw = getWidth();
        double ph = getHeight();

        double x = px + pw / 2.0 - hitW / 2.0;
        double y = py + ph / 2.0 - hitH / 2.0;

        if (facingX == 1) { // rechts
            x = px + pw + offset;
            y = py + ph / 2.0 - hitH / 2.0;
        } else if (facingX == -1) { // links
            x = px - hitW - offset;
            y = py + ph / 2.0 - hitH / 2.0;
        } else if (facingY == -1) { // oben
            x = px + pw / 2.0 - hitW / 2.0;
            y = py - hitH - offset;
        } else { // unten (default)
            x = px + pw / 2.0 - hitW / 2.0;
            y = py + ph + offset;
        }

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }
    public void updateCombat(double dt) {
        // Cooldown
        if (cooldownTimer > 0) cooldownTimer -= dt;

        // Attack Timer
        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) {
                attacking = false;
            }
        }
    }

    public void setFacing(int fx, int fy) {
        this.facingX = fx;
        this.facingY = fy;
    }



    public boolean setIsDownWTrue() { isDownW = true; return isDownW; }
    public boolean setIsDownWFalse() { isDownW = false; return isDownW; }
    public boolean setIsDownATrue() { isDownA = true; return isDownA; }
    public boolean setIsDownAFalse() { isDownA = false; return isDownA; }
    public boolean setIsDownSTrue() { isDownS = true; return isDownS; }
    public boolean setIsDownSFalse() { isDownS = false; return isDownS; }
    public boolean setIsDownDTrue() { isDownD = true; return isDownD; }
    public boolean setIsDownDFalse() { isDownD = false; return isDownD; }
    public int getFacingX() { return facingX; }
    public int getFacingY() { return facingY; }


    public double getKnockbackStrength() {
        return knockbackStrength;
    }

    public void setKnockbackStrength(double knockbackStrength) {
        this.knockbackStrength = knockbackStrength;
    }

}
