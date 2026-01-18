package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.Config;

import java.awt.geom.Rectangle2D;

public class Enemy extends Entity {

    // Controller-Referenz (du setzt sie in Controller: Enemy.setController(this);)
    protected static my_project.control.Controller controller;

    // Facing (für AttackHitbox Richtung)
    protected int facingX = 0;
    protected int facingY = 1;

    // Attack
    protected boolean attacking = false;
    protected boolean hitDoneThisAttack = false;
    protected double attackTimer = 0;
    protected double attackDuration = 0.12;
    protected double cooldownTimer = 0;
    protected double attackCooldown = 0.6;
    protected int attackDamage = 10;

    // Bewegung / KI
    protected double speed = 120;
    protected boolean runAway = false;

    public void setRunAway(boolean runAway) {
        this.runAway = runAway;
    }

    protected double runAwayTimer = 0;

    public Enemy(double hp, double xpos, double ypos,
                 double xspeed, double yspeed, int acc,
                 String name, double width, double height) {

        super(hp, xpos, ypos, xspeed, yspeed, acc, name, width, height);
    }

    public static void setController(my_project.control.Controller c) {
        controller = c;
    }

    @Override
    public void update(double dt) {
        if (controller == null) return;
        Player p = controller.getPlayer();
        if (p == null) return;

        // Timer
        if (cooldownTimer > 0) cooldownTimer -= dt;

        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) attacking = false;
        }

        // ✅ "Nah genug zum Angreifen?" (Box um den Spieler)
        Rectangle2D nearBox = p.getEnemyAggroBox();
        boolean inRange = nearBox.intersects(xpos, ypos, width, height);

        if (inRange) {
            // Nicht weiter laufen, nur ausrichten + attacken
            faceTowardsPlayer(p);
            startAttack();
            return;
        }

        // ✅ Wenn nicht in Range: zum Spieler laufen
        moveTowardsPlayer(p, dt);
    }

    public boolean getRunAway() {
        return runAway;
    }

    protected void moveTowardsPlayer(Player p, double dt) {
        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();

        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        dx /= dist;
        dy /= dist;

        // Facing setzen (damit Enemy beim Laufen in die Richtung schaut)
        setFacingFromVector(dx, dy);

        if (runAway){
            runAwayTimer = 0.3;
            runAway = false;
        }

        if (runAwayTimer > 0) {
            runAwayTimer -= dt;
            xpos -= dx * speed * dt;
            ypos -= dy * speed * dt;
        } else {
            xpos += dx * speed * dt;
            ypos += dy * speed * dt;
        }


        // ✅ FIX: Clamp nach Movement, sonst kann Enemy offscreen verschwinden
        clampToScreen();
    }

    protected void faceTowardsPlayer(Player p) {
        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();
        setFacingFromVector(dx, dy);
    }

    protected void setFacingFromVector(double dx, double dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) { facingX = 1; facingY = 0; }
            else { facingX = -1; facingY = 0; }
        } else {
            if (dy > 0) { facingX = 0; facingY = 1; }
            else { facingX = 0; facingY = -1; }
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

    // AttackHitbox wie beim Player: abhängig von facing
    public Rectangle2D getAttackHitbox() {
        double hitW = 40, hitH = 40, offset = 10;

        double x = xpos + width / 2 - hitW / 2;
        double y = ypos + height / 2 - hitH / 2;

        if (facingX == 1) x = xpos + width + offset;
        else if (facingX == -1) x = xpos - hitW - offset;
        else if (facingY == -1) y = ypos - hitH - offset;
        else y = ypos + height + offset;

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }

    // Debug Boxes (falls du das in Dieb.drawDebugBoxes(drawTool) nutzt)
    protected void drawDebugBoxes(DrawTool drawTool) {
        // Optional: hier später Enemy-eigene Debugboxen zeichnen
    }

    // ===== Knockback anwenden (vom Player-Hit) =====
    public void applyKnockback(double dx, double dy) {
        xpos += dx;
        ypos += dy;
        clampToScreen(); // ✅ clamp auch hier
    }

    // ✅ zentrale Methode: Enemy bleibt im Bildschirm
    protected void clampToScreen() {
        xpos = Math.max(0, Math.min(xpos, Config.WINDOW_WIDTH - width));
        ypos = Math.max(0, Math.min(ypos, Config.WINDOW_HEIGHT - height));
    }
}
