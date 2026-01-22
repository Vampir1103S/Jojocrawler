package my_project.model.Entities;
/**
 * Abstrakte Basisklasse für Boss-Gegner im Spiel.
 * <p>
 * Diese Klasse dient als gemeinsame Grundlage für alle
 * Boss-Implementierungen und erbt die grundlegende
 * Gegner-Logik aus der {@link Enemy}-Klasse.
 * Konkrete Boss-Gegner erweitern diese Klasse und
 * definieren eigenes Verhalten, besondere Fähigkeiten
 * oder angepasste Werte.
 */

abstract class Boss extends Enemy{
    public Boss() {
        super(0, 0, 100, 1, 1, 0, "Ganu", 0, 0);
    }
}
