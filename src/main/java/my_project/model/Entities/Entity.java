package my_project.model.Entities;

import KAGO_framework.model.GraphicalObject;
/**
 * Abstrakte Basisklasse für alle spielbaren und nicht-spielbaren Objekte mit Position und Statuswerten.
 * <p>
 * {@code Entity} speichert grundlegende Eigenschaften, die im Spiel für viele Objekte gleich sind:
 * <ul>
 *   <li>Position ({@code xpos}, {@code ypos}) und Größe ({@code width}, {@code height})</li>
 *   <li>Lebenspunkte ({@code hp})</li>
 *   <li>Bewegungs-/Statuswerte wie Geschwindigkeit, Ausdauer und Verteidigung</li>
 * </ul>
 *
 * Die Klasse stellt Getter/Setter bereit und bietet zusätzlich Hilfsmethoden wie
 * {@link #getCenterX()} und {@link #getCenterY()} für die Berechnung der Objektmitte
 * (z.B. für Distanzberechnungen, Knockback oder Ausrichtung).
 */

public abstract class  Entity extends GraphicalObject {

    protected double xpos;
    protected double ypos;
    protected double hp;
    protected double speed;
    protected double stamina;
    protected int defense;
    protected String name;


    protected double width;
    protected double height;

    public Entity(double xpos, double ypos, double hp, double speed, double stamina,
                  int defense, String name, double width, double height) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.hp = hp;
        this.speed = speed;
        this.stamina = stamina;
        this.defense = defense;
        this.name = name;

        this.width = width;
        this.height = height;
    }

    public double getXpos() { return xpos; }
    public void setXpos(double xpos) { this.xpos = xpos; }

    public double getYpos() { return ypos; }
    public void setYpos(double ypos) { this.ypos = ypos; }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getCenterX() { return xpos + width / 2.0; }
    public double getCenterY() { return ypos + height / 2.0; }

    public double getHP() { return hp; }
    public void setHP(double hp) { this.hp = hp; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getStamina() { return stamina; }
    public void setStamina(double stamina) { this.stamina = stamina; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public String getName() { return name; }
}
