package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.control.Controller;

import java.awt.geom.Rectangle2D;

public abstract class Enemy extends Entity {

    protected static Controller controller;

    // ===== Angriffssystem =====
    protected boolean attacking = false;
    protected boolean hitDoneThisAttack = false;

    protected double attackTimer = 0;
    protected double attackDuration = 0.12;

    protected double cooldownTimer = 0;
    protected double attackCooldown = 0.9;

    protected int attackDamage = 6;

    // Distanz, ab der Enemy angreift
    protected double attackRange = 70;

    // Facing (4 Richtungen)
    protected int facingX = 0;
    protected int facingY = 1;

    // Attack-Hitbox
    protected double hitW = 55;
    protected double hitH = 55;
    protected double hitOffset = 10;

    public Enemy(double xpos, double ypos, double hp, double speed, double stamina,
                 int defense, String name, double width, double height) {
        super(xpos, ypos, hp, speed, stamina, defense, name, width, height);
    }

    public void draw(DrawTool drawTool) {

    }

    @Override
    public void update(double dt) {
        if (controller == null || controller.getPlayer() == null) return;

        Player p = controller.getPlayer();


        if (cooldownTimer > 0) cooldownTimer -= dt;

        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) attacking = false;
        }


        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;


        if (Math.abs(dx) > Math.abs(dy)) {
            facingX = (dx >= 0) ? 1 : -1;
            facingY = 0;
        } else {
            facingY = (dy >= 0) ? 1 : -1;
            facingX = 0;
        }


        double move = dt * (100 * speed);

        if (!attacking) {
            double stopDist = attackRange * 0.85;

            if (dist > stopDist) {
                xpos += (dx / dist) * move;
                ypos += (dy / dist) * move;
            }

            if (cooldownTimer <= 0 && dist <= attackRange) {
                startAttack();
            }
        }
    }


    public void startAttack() {
        if (!attacking && cooldownTimer <= 0) {
            attacking = true;
            hitDoneThisAttack = false;
            attackTimer = attackDuration;
            cooldownTimer = attackCooldown;
        }
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

    public Rectangle2D getAttackHitbox() {
        double px = xpos;
        double py = ypos;
        double pw = width;
        double ph = height;

        double x = px + pw / 2.0 - hitW / 2.0;
        double y = py + ph / 2.0 - hitH / 2.0;

        if (facingX == 1) {
            x = px + pw + hitOffset;
        } else if (facingX == -1) {
            x = px - hitW - hitOffset;
        } else if (facingY == -1) {
            y = py - hitH - hitOffset;
        } else {
            y = py + ph + hitOffset;
        }

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }

    public void applyKnockback(double fx, double fy) {
        xpos += fx;
        ypos += fy;
    }

    public static void setController(Controller con) {
        controller = con;
    }
}
