package my_project.model.Entities;

import KAGO_framework.view.DrawTool;
import my_project.Config;

import java.awt.geom.Rectangle2D;

public class Enemy extends Entity {

    protected static my_project.control.Controller controller;


    protected int facingX = 0;
    protected int facingY = 1;


    protected boolean attacking = false;
    protected boolean hitDoneThisAttack = false;
    protected double attackTimer = 0;
    protected double attackDuration = 0.12;
    protected double cooldownTimer = 0;
    protected double attackCooldown = 0.6;
    protected int attackDamage = 10;
    protected int direction = 0;


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

        if (!attacking) {
            lookAtPlayer(p);
        }


        if (cooldownTimer > 0) cooldownTimer -= dt;

        if (attacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) attacking = false;
        }


        Rectangle2D nearBox = p.getEnemyAggroBox();
        boolean inRange = nearBox.intersects(xpos, ypos, width, height);

        if (inRange) {
            faceTowardsPlayer(p);
            startAttack();
            return;
        }


        moveTowardsPlayer(p, dt);
    }

    public boolean getRunAway() {
        return runAway;
    }

    /**
     * Bewegt den Gegner relativ zum Spieler – entweder auf ihn zu oder kurzzeitig von ihm weg.
     * <p>
     * Ablauf der Methode:
     * <ol>
     *   <li>Berechnet den Richtungsvektor vom Gegner zum Spieler (dx, dy)</li>
     *   <li>Normiert den Vektor, sodass die Bewegung unabhängig von der Entfernung ist</li>
     *   <li>Setzt die Blickrichtung (Facing) des Gegners passend zur Bewegungsrichtung</li>
     *   <li>Bewegt den Gegner mit seiner Geschwindigkeit framerate-unabhängig</li>
     *   <li>Optional: Gegner kann für kurze Zeit vom Spieler weglaufen (Run-Away-Logik)</li>
     *   <li>Begrenzt die Position auf den sichtbaren Spielbereich</li>
     * </ol>
     *
     * Die Methode wird im {@link #update(double)}-Zyklus aufgerufen,
     * solange sich der Spieler nicht in Angriffsreichweite befindet.
     *
     * @param p  der Spieler, auf den sich der Gegner zubewegt
     * @param dt Delta Time (Zeit seit dem letzten Frame),
     *           um gleichmäßige Bewegung unabhängig von der Framerate zu ermöglichen
     */
    protected void moveTowardsPlayer(Player p, double dt) {
        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();

        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        dx /= dist;
        dy /= dist;

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

        clampToScreen();
    }

    /**
     * Richtet den Gegner so aus, dass er den Spieler anschaut.
     * <p>
     * Es wird ein Richtungsvektor vom Gegner zum Spieler berechnet.
     * Dieser Vektor wird anschließend genutzt, um die Blickrichtung
     * (Facing) des Gegners zu setzen.
     *
     * Die Methode verändert **nur die Ausrichtung**, nicht die Position.
     * Sie wird z.B. verwendet, wenn der Gegner in Angriffsreichweite ist
     * und nicht mehr auf den Spieler zuläuft.
     *
     * @param p der Spieler, auf den sich der Gegner ausrichten soll
     */
    protected void faceTowardsPlayer(Player p) {
        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();
        setFacingFromVector(dx, dy);
    }


    /**
     * Setzt die Blickrichtung (Facing) des Gegners anhand eines Richtungsvektors.
     * <p>
     * Der Vektor {@code (dx, dy)} beschreibt die Richtung zu einem Ziel
     * (z.B. zum Spieler). Die Methode entscheidet, ob die horizontale oder
     * vertikale Komponente dominiert, und richtet den Gegner entsprechend aus:
     * <ul>
     *   <li>Horizontale Dominanz → links oder rechts</li>
     *   <li>Vertikale Dominanz → oben oder unten</li>
     * </ul>
     *
     * Dadurch wird sichergestellt, dass der Gegner immer eindeutig
     * in eine der vier Hauptrichtungen blickt.
     *
     * @param dx Richtungsanteil auf der X-Achse
     * @param dy Richtungsanteil auf der Y-Achse
     */

    protected void setFacingFromVector(double dx, double dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) { facingX = 1; facingY = 0; }
            else { facingX = -1; facingY = 0; }
          } else {
             if (dy > 0) { facingX = 0; facingY = 1; }
            else { facingX = 0; facingY = -1; }
        }


    }


    /**
     * Startet einen Angriff des Gegners.
     * <p>
     * Ein Angriff kann nur gestartet werden, wenn der Gegner aktuell
     * nicht angreift und kein Cooldown aktiv ist.
     * Beim Start des Angriffs werden:
     * <ul>
     *   <li>der Angriffsstatus aktiviert</li>
     *   <li>der Trefferstatus zurückgesetzt</li>
     *   <li>die Angriffsdauer gesetzt</li>
     *   <li>der Cooldown-Timer gestartet</li>
     * </ul>
     *
     * Die eigentliche Trefferprüfung erfolgt anschließend über
     * {@link #canDealHitNow()} und die Attack-Hitbox.
     */
    public void startAttack() {
        if (!attacking && cooldownTimer <= 0) {
            attacking = true;
            hitDoneThisAttack = false;
            attackTimer = attackDuration;
            cooldownTimer = attackCooldown;
        }
    }

    /**
     * Prüft, ob der Gegner in diesem Moment Schaden verursachen darf.
     * <p>
     * Ein Treffer ist nur möglich, wenn:
     * <ul>
     *   <li>der Gegner sich aktuell in einem Angriff befindet</li>
     *   <li>für diesen Angriff noch kein Treffer registriert wurde</li>
     * </ul>
     *
     * Dadurch wird verhindert, dass ein einzelner Angriff
     * mehrfach Schaden im selben Angriffszyklus verursacht.
     *
     * @return {@code true}, wenn der Gegner jetzt Schaden verursachen darf,
     *         sonst {@code false}
     */


    /**
     * Prüft, ob der Gegner in diesem Moment Schaden verursachen darf.
     * <p>
     * Ein Treffer ist nur möglich, wenn:
     * <ul>
     *   <li>der Gegner sich aktuell in einem Angriff befindet</li>
     *   <li>für diesen Angriff noch kein Treffer registriert wurde</li>
     * </ul>
     *
     * Dadurch wird verhindert, dass ein einzelner Angriff
     * mehrfach Schaden im selben Angriffszyklus verursacht.
     *
     * @return {@code true}, wenn der Gegner jetzt Schaden verursachen darf,
     *         sonst {@code false}
     */

    public boolean canDealHitNow() {
        return attacking && !hitDoneThisAttack;
    }

    /**
     * Markiert den aktuellen Angriff als bereits getroffen.
     * <p>
     * Nach dem Aufruf dieser Methode kann der Gegner im laufenden
     * Angriffszyklus keinen weiteren Schaden mehr verursachen.
     * Dies verhindert Mehrfachtreffer innerhalb eines einzelnen Angriffs.
     */

    public void markHitDone() {
        hitDoneThisAttack = true;
    }


    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * Erzeugt und liefert die Attack-Hitbox des Gegners.
     * <p>
     * Die Attack-Hitbox ist ein rechteckiger Bereich vor dem Gegner,
     * in dem ein Angriff Schaden verursachen kann.
     * Ihre Position hängt von der aktuellen Blickrichtung
     * ({@code facingX}, {@code facingY}) ab.
     * <ul>
     *   <li>rechts / links: Hitbox wird seitlich neben dem Gegner platziert</li>
     *   <li>oben / unten: Hitbox wird ober- bzw. unterhalb des Gegners platziert</li>
     * </ul>
     *
     * Die Größe der Hitbox ist fest definiert und unabhängig von der
     * Größe des Gegners.
     *
     * @return ein {@link Rectangle2D}, das den Angriffsbereich des Gegners beschreibt
     */

    public Rectangle2D getAttackHitbox() {
        double hitW = 80, hitH = 80, offset = 10;

        double x = xpos + width / 2 - hitW / 2;
        double y = ypos + height / 2 - hitH / 2;

        if (facingX == 1) x = xpos + width + offset;
        else if (facingX == -1) x = xpos - hitW - offset;
        else if (facingY == -1) y = ypos - hitH - offset;
        else y = ypos + height + offset;

        return new Rectangle2D.Double(x, y, hitW, hitH);
    }


    /**
     * Wendet einen Rückstoß (Knockback) auf den Gegner an.
     * <p>
     * Die übergebenen Verschiebungswerte werden direkt zur aktuellen
     * Position addiert, um den Gegner durch einen Treffer
     * zurückzustoßen.
     * Anschließend wird die Position auf den sichtbaren
     * Spielbereich begrenzt.
     *
     * @param dx Verschiebung auf der X-Achse
     * @param dy Verschiebung auf der Y-Achse
     */

    public void applyKnockback(double dx, double dy) {
        xpos += dx;
        ypos += dy;
        clampToScreen();
    }

    /**
     * Begrenzt die Position des Gegners auf den sichtbaren Spielbereich.
     * <p>
     * Die Methode stellt sicher, dass sich der Gegner nicht
     * außerhalb des Fensters bewegen kann.
     * Dabei werden die aktuellen Koordinaten so angepasst,
     * dass der Gegner vollständig innerhalb der Fenstergrenzen bleibt.
     */

    protected void clampToScreen() {
        xpos = Math.max(0, Math.min(xpos, Config.WINDOW_WIDTH - width));
        ypos = Math.max(0, Math.min(ypos, Config.WINDOW_HEIGHT - height));
    }


    /**
     * Richtet den Gegner visuell und logisch auf den Spieler aus.
     * <p>
     * Anhand der relativen Position des Spielers wird bestimmt,
     * ob der Gegner horizontal oder vertikal auf den Spieler blickt.
     * Zusätzlich zur Blickrichtung werden auch die Animations-
     * bzw. Richtungswerte gesetzt.
     *
     * Die Methode verändert nur die Ausrichtung des Gegners
     * und keine Position oder Bewegung.
     *
     * @param p der Spieler, auf den sich der Gegner ausrichten soll
     */

    protected void lookAtPlayer(Player p) {
        if (p == null) return;

        double dx = p.getCenterX() - getCenterX();
        double dy = p.getCenterY() - getCenterY();

        // horizontale vs vertikale Dominanz
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                facingX = 1; facingY = 0;
                direction = 1; // rechts
            } else {
                facingX = -1; facingY = 0;
                direction = 2; // links
            }
        } else {
            if (dy > 0) {
                facingX = 0; facingY = 1;
                direction = 0; // unten
            } else {
                facingX = 0; facingY = -1;
                direction = 3; // oben
            }
        }
    }

}
