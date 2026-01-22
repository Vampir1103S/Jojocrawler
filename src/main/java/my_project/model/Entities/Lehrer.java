package my_project.model.Entities;
/**
 * Repräsentiert einen einfachen, nicht-angreifenden Lehrer-Charakter im Spiel.
 * <p>
 * Die Klasse erbt von {@link Entity} und dient als statische Figur
 * (z.B. NPC oder Platzhalter) ohne eigene Bewegungs-, Kampf- oder
 * Interaktionslogik.
 * Sie kann für Story-Elemente, Dekoration oder spätere Erweiterungen
 * genutzt werden.
 */


public class Lehrer extends Entity{
    public Lehrer() {
        super(0, 0, 0, 0, 0, 0, "Feldus", 0, 0);

    }
}
