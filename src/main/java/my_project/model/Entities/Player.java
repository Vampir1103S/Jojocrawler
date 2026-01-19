package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.view.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player extends Entity {

    private boolean isDownW, isDownA, isDownS, isDownD;
    private double knockbackStrength = 200;

    private SpriteSheet spriteSheet2;
    private int number = 0;
    private int direction = 0;
    private double timer = 0;

    // Facing
    private int facingX = 0;
    private int facingY = 1;

    // Angriff
    private boolean attacking = false;
    private boolean hitDoneThisAttack = false;
    private double attackTimer = 0;
    private double attackDuration = 0.12;

    private double cooldownTimer = 0;

    // Baseline
    private final double baseAttackCooldown = 0.35;
    private double attackCooldown = baseAttackCooldown;

    private int attackDamage = 10;

    // Inventory
    private my_project.model.items.Inventory inventory;

    // ===== Stats / Upgrades / Buffs =====
    private double maxHP = 100;

    private double baseMoveSpeed = 250;
    private double permanentSpeedMult = 1.0;
    private double tempSpeedMult = 1.0;
    private double tempSpeedTimer = 0;

    private double invulnerableTimer = 0;

    public Player() {
        super(100, 500, 100, 30, 1, 1, "hehe", 60, 110);
        spriteSheet2 = new SpriteSheet("Player-Sprite.png", 4, 4);
        maxHP = 100;
    }

    public void setInventory(my_project.model.items.Inventory inv) {
        this.inventory = inv;
    }

    public double getMaxHP() { return maxHP; }

    public void increaseMaxHP(double amount) {
        if (amount <= 0) return;
        maxHP += amount;
        setHP(Math.min(getHP(), maxHP));
    }

    public void addMoveSpeedMultiplier(double add) {
        permanentSpeedMult = Math.max(0.2, permanentSpeedMult + add);
    }

    public double getMoveSpeed() {
        return baseMoveSpeed * permanentSpeedMult * tempSpeedMult;
    }

    public void activateSpeedBoost(double durationSeconds, double multiplier) {
        tempSpeedMult = Math.max(1.0, multiplier);
        tempSpeedTimer = Math.max(0, durationSeconds);
    }

    public void activateInvulnerability(double durationSeconds) {
        invulnerableTimer = Math.max(invulnerableTimer, Math.max(0, durationSeconds));
    }

    public boolean isInvulnerable() { return invulnerableTimer > 0; }

    public void heal(double amount) {
        if (amount <= 0) return;
        setHP(Math.min(getHP() + amount, maxHP));
    }

    private my_project.model.items.Weapons getEquippedWeapon() {
        if (inventory == null) return null;
        return inventory.getSelectedWeapon();
    }

    @Override
    public void draw(DrawTool drawTool) {



        spriteSheet2.setCurrent(direction, number);
        spriteSheet2.draw(drawTool, xpos - 12, ypos - 10, 5);

        if (attacking) {
            Rectangle2D hb = getAttackHitbox();
            drawTool.setCurrentColor(new Color(255, 0, 0, 120));
            drawTool.drawFilledRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
            drawTool.setCurrentColor(Color.RED);
            drawTool.drawRectangle(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
        }

        if (isInvulnerable()) {
            drawTool.setCurrentColor(new Color(0, 180, 255, 80));
            drawTool.drawFilledRectangle(xpos, ypos, width, height);
        }
    }
    public void resetPlayerPosition(double xpos, double ypos) {
        this.xpos = xpos;
        this.ypos = ypos;
    }



    @Override
    public void update(double dt) {
        if (cooldownTimer > 0) cooldownTimer -= dt;

        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) attacking = false;
        }

        if (tempSpeedTimer > 0) {
            tempSpeedTimer -= dt;
            if (tempSpeedTimer <= 0) {
                tempSpeedTimer = 0;
                tempSpeedMult = 1.0;
            }
        }

        if (invulnerableTimer > 0) {
            invulnerableTimer -= dt;
            if (invulnerableTimer < 0) invulnerableTimer = 0;
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
        double rangeX = 10;
        double rangeY = 10;

        return new Rectangle2D.Double(
                xpos - rangeX,
                ypos - rangeY,
                width + rangeX * 2,
                height + rangeY * 2
        );
    }

    public void startAttack() {
        if (!attacking && cooldownTimer <= 0) {
            my_project.model.items.Weapons w = getEquippedWeapon();
            if (w != null) {
                attackDamage = w.getDamage();
                attackCooldown = baseAttackCooldown * w.getCooldownMultiplier();
            } else {
                attackDamage = 10;
                attackCooldown = baseAttackCooldown;
            }

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

    // ✅ FIX: "Rotiert" Hitbox bei Up/Down (W/H tauschen)
    public Rectangle2D getAttackHitbox() {
        double hitW = 60, hitH = 60, offset = 10; // Fallback

        my_project.model.items.Weapons w = getEquippedWeapon();
        if (w != null) {
            hitW = w.getHitW();
            hitH = w.getHitH();
            offset = w.getOffset();
        }

        // ✅ Wenn vertikal (oben/unten): Hitbox drehen => Breite/Höhe tauschen
        boolean vertical = (facingY != 0);
        if (vertical) {
            double tmp = hitW;
            hitW = hitH;
            hitH = tmp;
        }

        double x = xpos + width / 2 - hitW / 2;
        double y = ypos + height / 2 - hitH / 2;

        if (facingX == 1) x = xpos + width + offset;
        else if (facingX == -1) x = xpos - hitW - offset;
        else if (facingY == -1) y = ypos - hitH - offset;
        else y = ypos + height + offset;

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }

    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(xpos, ypos, width, height);
    }

    public void setFacing(int fx, int fy) {
        facingX = fx;
        facingY = fy;
    }

    public double getKnockbackStrength() {
        return knockbackStrength;
    }

    public void setIsDownWTrue() { isDownW = true; }
    public void setIsDownWFalse() { isDownW = false; }
    public void setIsDownATrue() { isDownA = true; }
    public void setIsDownAFalse() { isDownA = false; }
    public void setIsDownSTrue() { isDownS = true; }
    public void setIsDownSFalse() { isDownS = false; }
    public void setIsDownDTrue() { isDownD = true; }
    public void setIsDownDFalse() { isDownD = false; }
}
