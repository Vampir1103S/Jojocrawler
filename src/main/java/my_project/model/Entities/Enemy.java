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

    // Aggro / Stop-Distanz
    protected double stopFactorInAggro = 0.85; // 0.85 = bleibt etwas früher stehen

    // Facing (4 Richtungen)
    protected int facingX = 0;
    protected int facingY = 1;

    // Attack-Hitbox
    protected double hitW = 55;
    protected double hitH = 55;
    protected double hitOffset = 10;

    // Wenn er "schräg" steht: ein bisschen ausrichten, damit Hitbox trifft
    protected double alignFactor = 0.9;

    public Enemy(double xpos, double ypos, double hp, double speed, double stamina,
                 int defense, String name, double width, double height) {
        super(xpos, ypos, hp, speed, stamina, defense, name, width, height);
    }

    public void draw(DrawTool drawTool) {
        // leer (Dieb zeichnet selbst)
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

        // ===== Richtung zum Player =====
        double dxC = p.getCenterX() - this.getCenterX();
        double dyC = p.getCenterY() - this.getCenterY();

        if (Math.abs(dxC) > Math.abs(dyC)) {
            facingX = (dxC >= 0) ? 1 : -1;
            facingY = 0;
        } else {
            facingY = (dyC >= 0) ? 1 : -1;
            facingX = 0;
        }

        // ===== Aggro-Box vom Player =====
        Rectangle2D aggro = p.getEnemyAggroBox();

        // Enemy Rect
        Rectangle2D myRect = new Rectangle2D.Double(xpos, ypos, width, height);

        double move = dt * (100 * speed);

        // ===== 1) Wenn NICHT in Aggro: normal zum Player laufen =====
        if (!aggro.intersects(myRect)) {
            if (!attacking) {
                double dist = Math.sqrt(dxC * dxC + dyC * dyC);
                if (dist > 0) {
                    xpos += (dxC / dist) * move;
                    ypos += (dyC / dist) * move;
                }
            }
            return;
        }

        // ===== 2) Wenn IN Aggro: früher stoppen + ausrichten =====
        if (!attacking) {
            // Stop-Distanz = etwas innerhalb der Aggro-Kante
            // Wir nehmen als "Stop" nicht distanzbasiert, sondern:
            // -> Er bewegt sich nur noch, um die Hitbox sauber auszurichten.

            if (facingX != 0) {
                // links/rechts: Y ausrichten (sonst schlägt er "in die Luft")
                if (Math.abs(dyC) > 2) {
                    ypos += Math.signum(dyC) * (move * alignFactor);
                }
            } else {
                // oben/unten: X ausrichten
                if (Math.abs(dxC) > 2) {
                    xpos += Math.signum(dxC) * (move * alignFactor);
                }
            }
        }

        // ===== 3) Angriff nur starten, wenn Hitbox JETZT wirklich trifft =====
        if (!attacking && cooldownTimer <= 0) {
            Rectangle2D hb = getAttackHitbox();
            if (hb.intersects(p.getXpos(), p.getYpos(), p.getWidth(), p.getHeight())) {
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

        if (facingX == 1) {           // rechts
            x = px + pw + hitOffset;
            y = py + ph / 2.0 - hitH / 2.0;
        } else if (facingX == -1) {   // links
            x = px - hitW - hitOffset;
            y = py + ph / 2.0 - hitH / 2.0;
        } else if (facingY == -1) {   // oben
            x = px + pw / 2.0 - hitW / 2.0;
            y = py - hitH - hitOffset;
        } else {                      // unten
            x = px + pw / 2.0 - hitW / 2.0;
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
    protected void drawDebugBoxes(DrawTool drawTool) {
        if (controller == null || controller.getPlayer() == null) return;

        // 🟦 Aggro-Box (vom Player)
        var aggro = controller.getPlayer().getEnemyAggroBox();
        drawTool.setCurrentColor(new java.awt.Color(0, 0, 255, 60));
        drawTool.drawFilledRectangle(
                aggro.getX(), aggro.getY(),
                aggro.getWidth(), aggro.getHeight()
        );
        drawTool.setCurrentColor(java.awt.Color.BLUE);
        drawTool.drawRectangle(
                aggro.getX(), aggro.getY(),
                aggro.getWidth(), aggro.getHeight()
        );

        // 🔴 Attack-Box (nur wenn angreifend)
        if (attacking) {
            var hb = getAttackHitbox();
            drawTool.setCurrentColor(new java.awt.Color(255, 0, 0, 120));
            drawTool.drawFilledRectangle(
                    hb.getX(), hb.getY(),
                    hb.getWidth(), hb.getHeight()
            );
            drawTool.setCurrentColor(java.awt.Color.RED);
            drawTool.drawRectangle(
                    hb.getX(), hb.getY(),
                    hb.getWidth(), hb.getHeight()
            );
        }
    }

}
