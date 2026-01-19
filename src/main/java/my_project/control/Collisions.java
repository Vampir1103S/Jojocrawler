package my_project.control;

import my_project.model.Entities.Entity;
import my_project.model.map.Environment;

/**
 * Die Klasse {@code Collisions} ist für die Kollisionsabfrage im Spiel zuständig.
 * <p>
 * Sie überprüft, ob sich zwei rechteckige Hitboxen überschneiden.
 * Diese Klasse wird unter anderem für:
 * <ul>
 *   <li>Kollisionen zwischen Spieler und Gegnern</li>
 *   <li>Kollisionen zwischen Entities und der Umgebung (z.B. Bäume, Wände)</li>
 *   <li>Kollisionen mit unsichtbaren Rechtecken (Trigger, Portale, Zonen)</li>
 * </ul>
 *
 * Die Kollisionsberechnung basiert auf dem Axis-Aligned Bounding Box (AABB) Prinzip.
 */
public class Collisions {

    /**
     * Erstellt ein neues {@code Collisions}-Objekt.
     * <p>
     * Die Klasse speichert keinen Zustand und dient nur zur Berechnung,
     * daher ist der Konstruktor leer.
     */
    public Collisions() {}

    /**
     * Prüft eine rechteckige Kollision zwischen zwei {@link Entity}-Objekten.
     * <p>
     * Wird verwendet für:
     * <ul>
     *   <li>Spieler ↔ Gegner</li>
     *   <li>Gegner ↔ Gegner</li>
     *   <li>Spieler ↔ NPCs</li>
     * </ul>
     *
     * Die Methode vergleicht Position und Größe der Hitboxen beider Entities.
     *
     * @param e1 die erste Entity (z.B. Spieler)
     * @param e2 die zweite Entity (z.B. Gegner)
     * @return {@code true}, wenn sich die Hitboxen überschneiden,
     *         sonst {@code false}
     */
    public boolean rectangleCollisions(Entity e1, Entity e2) {
        if (e1 == null || e2 == null) return false;

        return e1.getXpos() < e2.getXpos() + e2.getWidth() &&
                e1.getXpos() + e1.getWidth() > e2.getXpos() &&
                e1.getYpos() < e2.getYpos() + e2.getHeight() &&
                e1.getYpos() + e1.getHeight() > e2.getYpos();
    }


    /**
     * Prüft eine rechteckige Kollision zwischen einer {@link Entity}
     * und einem {@link Environment}-Objekt.
     * <p>
     * Diese Methode wird genutzt, um zu verhindern, dass sich der Spieler
     * oder Gegner durch feste Objekte wie Bäume, Wände oder Hindernisse bewegen.
     *
     * @param e   die Entity, die überprüft wird (z.B. Spieler)
     * @param env das Umgebungsobjekt mit eigener Hitbox
     * @return {@code true}, wenn eine Kollision vorliegt,
     *         sonst {@code false}
     */
    public boolean rectangleCollisions(Entity e, Environment env) {
        if (e == null || env == null) return false;

        return e.getXpos() < env.getHitboxX() + env.getHitboxWidth() &&
                e.getXpos() + e.getWidth() > env.getHitboxX() &&
                e.getYpos() < env.getHitboxY() + env.getHitboxHeight() &&
                e.getYpos() + e.getHeight() > env.getHitboxY();
    }


    /**
     * Prüft eine rechteckige Kollision zwischen einer {@link Entity}
     * und einem frei definierten Rechteck.
     * <p>
     * Diese Methode ist besonders flexibel und wird z.B. verwendet für:
     * <ul>
     *   <li>Portale</li>
     *   <li>Trigger-Zonen</li>
     *   <li>Shop- oder Interaktionsbereiche</li>
     * </ul>
     *
     * @param e        die Entity, die überprüft wird
     * @param rx       X-Position des Rechtecks
     * @param ry       Y-Position des Rechtecks
     * @param rWidth   Breite des Rechtecks
     * @param rHeight  Höhe des Rechtecks
     * @return {@code true}, wenn sich Entity und Rechteck überschneiden,
     *         sonst {@code false}
     */
    public boolean rectangleCollisions(
            Entity e,
            double rx, double ry,
            double rWidth, double rHeight
    ) {
        if (e == null) return false;

        return e.getXpos() < rx + rWidth &&
                e.getXpos() + e.getWidth() > rx &&
                e.getYpos() < ry + rHeight &&
                e.getYpos() + e.getHeight() > ry;
    }
}
