package my_project.model.items.Consumables;

import my_project.model.items.Item;
/**
 * Abstrakte Basisklasse für alle verbrauchbaren Items im Spiel.
 * <p>
 * {@code Consumable} dient als gemeinsame Oberklasse für Items,
 * die beim Benutzen eine einmalige Wirkung haben und anschließend
 * aus dem Inventar entfernt werden (z.B. Tränke).
 *
 * Konkrete verbrauchbare Items wie Heil- oder Effekttränke
 * erweitern diese Klasse und implementieren ihre spezifische
 * Wirkung.
 */

public abstract class Consumable extends Item {
}
