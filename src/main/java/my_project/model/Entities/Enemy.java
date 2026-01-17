package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.control.Controller;

import java.awt.*;
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

    // ===== AGGRO-BOX (Rechteck) =====
    // -> Damit steuerst du die Größe der Aggrobox
    protected double aggroW = 100;
    protected double aggroH = 220;

    // Facing (4 Richtungen)
    protected int facingX = 0;
    protected int facingY = 1;

    // Attack-Hitbox
    protected double hitW = 55;
    protected double hitH = 55;
    protected double hitOffset = 10;

    // Debug-Rendering
    protected boolean showAggroBox = true;     // blaues Aggro-Rechteck
    protected boolean showAttackBox = true;    // rote Attackbox NUR beim Angriff

    public Enemy(double xpos, double ypos, double hp, double speed, double stamina,
                 int defense, String name, double width, double height) {
        super(xpos, ypos, hp, speed, stamina, defense, name, width, height);
    }

    // ===== Helper: Enemy-Körper als Rechteck =====
    protected Rectangle2D getBodyRect() {
        return new Rectangle2D.Double(xpos, ypos, width, height);
    }

    // ===== Helper: Aggro-Box um den Spieler (zentriert) =====
    public Rectangle2D getAggroBox(Player p) {
        double x = p.getCenterX() - aggroW / 2.0;
        double y = p.getCenterY() - aggroH / 2.0;
        return new Rectangle2D.Double(x, y, aggroW, aggroH);
    }

    // ===== Helper: Attack-Hitbox abhängig von Facing =====
    public Rectangle2D getAttackHitbox() {
        double px = xpos;
        double py = ypos;
        double pw = width;
        double ph = height;

        double x = px + pw / 2.0 - hitW / 2.0;
        double y = py + ph / 2.0 - hitH / 2.0;

        if (facingX == 1) { // rechts
            x = px + pw + hitOffset;
        } else if (facingX == -1) { // links
            x = px - hitW - hitOffset;
        } else if (facingY == -1) { // oben
            y = py - hitH - hitOffset;
        } else { // unten
            y = py + ph + hitOffset;
        }

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }

    // ===== Debug Zeichnen (Aggro + Attack) =====
    public void drawDebugBoxes(DrawTool drawTool) {
        if (controller == null || controller.getPlayer() == null) return;
        Player p = controller.getPlayer();

        if (showAggroBox) {
            Rectangle2D ag = getAggroBox(p);
            drawTool.setCurrentColor(new Color(0, 0, 255, 80));
            drawTool.drawFilledRectangle(ag.getX(), ag.getY(), ag.getWidth(), ag.getHeight());
            drawTool.setCurrentColor(new Color(0, 0, 255, 160));
            drawTool.drawRectangle(ag.getX(), ag.getY(), ag.getWidth(), ag.getHeight());
        }

        // Attackbox nur sichtbar, wenn er gerade angreift
        if (attacking && showAttackBox) {
            Rectangle2D hb = getAttackHitbox();
            drawTool.setCurrentColor(new Color(255, 0, 0, 120));
            drawTool.drawFilledRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
        }
    }

    @Override
    public void update(double dt) {
        if (controller == null || controller.getPlayer() == null) return;
        Player p = controller.getPlayer();

        // ===== Timer =====
        if (cooldownTimer > 0) cooldownTimer -= dt;

        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) attacking = false;
        }

        // ===== Facing Richtung Player (4-way) =====
        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();

        if (Math.abs(dx) > Math.abs(dy)) {
            facingX = (dx >= 0) ? 1 : -1;
            facingY = 0;
        } else {
            facingY = (dy >= 0) ? 1 : -1;
            facingX = 0;
        }

        Rectangle2D aggro = getAggroBox(p);
        Rectangle2D myBody = getBodyRect();

        boolean inAggro = aggro.intersects(myBody.getX(), myBody.getY(), myBody.getWidth(), myBody.getHeight());

        double move = dt * (100 * speed);

        // ===== 1) Nicht in Aggro -> verfolgen =====
        if (!inAggro && !attacking) {
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 0) {
                xpos += (dx / dist) * move;
                ypos += (dy / dist) * move;
            }
            return;
        }

        // ===== 2) In Aggro -> stehen bleiben + ggf. leicht ausrichten =====
        // (hilft gegen Eckfälle, damit Attackbox wirklich trifft)
        if (!attacking) {
            double alignSpeed = move * 0.6;

            if (facingX != 0) {
                // links/rechts: Y ausrichten
                if (Math.abs(dy) > 3) ypos += Math.signum(dy) * alignSpeed;
            } else {
                // oben/unten: X ausrichten
                if (Math.abs(dx) > 3) xpos += Math.signum(dx) * alignSpeed;
            }

            // ===== 3) Attack START nur wenn Attackbox JETZT den Player trifft =====
            if (cooldownTimer <= 0) {
                Rectangle2D atk = getAttackHitbox();
                boolean hitPossible = atk.intersects(p.getXpos(), p.getYpos(), p.getWidth(), p.getHeight());

                if (hitPossible) {
                    startAttack();
                }
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

    public void applyKnockback(double fx, double fy) {
        xpos += fx;
        ypos += fy;
    }

    public static void setController(Controller con) {
        controller = con;
    }

    // Optional: Aggrobox kleiner/größer machen
    public void setAggroSize(double w, double h) {
        this.aggroW = w;
        this.aggroH = h;
    }
}
