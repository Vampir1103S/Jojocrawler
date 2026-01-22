package my_project.model.Entities;
/**
 * Abstrakte Basisklasse für alle nicht-spielbaren Charaktere (NPCs).
 * <p>
 * Die Klasse {@code Npcs} dient als gemeinsame Oberklasse für
 * alle Charaktere, die nicht aktiv am Kampf teilnehmen,
 * z.B. Händler, Erzähler oder andere Story-Figuren.
 *
 * Sie erbt grundlegende Eigenschaften wie Position, Größe
 * und Statuswerte von {@link Entity} und ermöglicht eine
 * klare Trennung zwischen spielbaren Figuren, Gegnern
 * und NPCs innerhalb der Spielarchitektur.
 */

public abstract class Npcs extends Entity{
    public Npcs(int xpos ,int ypos, double hp, int speed, double stamina, int defense, String Name, double width, double height) {
        super(xpos, ypos, hp, speed, stamina, defense, Name,  width, height);

    }
}