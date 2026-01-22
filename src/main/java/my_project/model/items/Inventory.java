package my_project.model.items;

import KAGO_framework.model.abitur.datenstrukturen.List;
import my_project.model.Entities.Player;
import my_project.model.items.Consumables.Potions;
import my_project.model.items.Consumables.HealingPotion;
import my_project.model.items.Consumables.SpeedPotion;
import my_project.model.items.Consumables.ResistancePotion;

import java.util.ArrayList;
/**
 * Repräsentiert das Inventar des Spielers.
 * <p>
 * Das Inventar verwaltet:
 * <ul>
 *   <li>gesammelte Items</li>
 *   <li>Waffen- und Potion-Loadouts</li>
 *   <li>Coins und Kaufvorgänge</li>
 * </ul>
 *
 * Intern wird eine KAGO-eigene Datenstruktur verwendet.
 * Für Benutzeroberflächen werden Adapter-Methoden
 * zu {@code java.util.List} bereitgestellt.
 */
public class Inventory {

    private final List<Item> items = new List<>();

    private final List<Weapons> weaponLoadout = new List<>();
    private final List<Potions> potionLoadout = new List<>();

    private int activeSlot = 1;
    private int weaponIndex = 0;
    private int potionIndex = 0;

    private int coins = 99999;

    /**
     * Erhöht die Anzahl der Coins im Inventar.
     * <p>
     * Es werden nur positive Werte berücksichtigt.
     * Negative Beträge haben keinen Effekt und werden ignoriert.
     *
     * @param amount Anzahl der Coins, die hinzugefügt werden sollen
     */

    public void addCoins(int amount) {
        coins += Math.max(0, amount);
    }
    /**
     * Versucht, eine bestimmte Anzahl an Coins auszugeben.
     * <p>
     * Ist der angegebene Betrag kleiner oder gleich null,
     * gilt der Vorgang automatisch als erfolgreich.
     * Reichen die vorhandenen Coins aus, wird der Betrag
     * abgezogen und die Methode liefert {@code true} zurück.
     *
     * @param amount Anzahl der Coins, die ausgegeben werden sollen
     * @return {@code true}, wenn der Betrag erfolgreich ausgegeben wurde,
     *         sonst {@code false}
     */

    public boolean spendCoins(int amount) {
        if (amount <= 0) return true;
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public int getCoins() {
        return coins;
    }

    /**
     * Fügt dem Inventar ein neues Item hinzu.
     * <p>
     * Das Item wird in der internen Datenstruktur gespeichert
     * und steht anschließend für Nutzung, Ausrüstung
     * oder Anzeige im Inventar zur Verfügung.
     * Null-Werte werden ignoriert.
     *
     * @param item das Item, das dem Inventar hinzugefügt werden soll
     */

    public void addItem(Item item) {
        if (item == null) return;
        items.append(item);
    }
    /**
     * Gibt alle Inventar-Items als {@link java.util.List} zurück.
     * <p>
     * Diese Methode dient als Adapter zwischen der internen
     * KAGO-Datenstruktur und externen Komponenten wie z.B.
     * Swing-Oberflächen, die mit {@code java.util.List} arbeiten.
     *
     * @return eine {@link java.util.List} mit allen Items im Inventar
     */

    public java.util.List<Item> getItemsAsJavaList() {
        return toJavaList(items);
    }
    /**
     * Gibt die interne Datenstruktur des Inventars zurück.
     * <p>
     * Die zurückgegebene Liste basiert auf der KAGO-eigenen
     * Datenstruktur und wird für die Spiellogik verwendet.
     *
     * @return die interne KAGO-List mit allen Items
     */

    public List<Item> getItemsDS() {
        return items;
    }

    /**
     * Setzt den aktuell aktiven Inventar-Slot.
     * <p>
     * Erlaubt sind nur die Slots 1 (Waffe) und 2 (Potion).
     * Ungültige Werte werden ignoriert.
     *
     * @param slot der Slot, der aktiviert werden soll
     */

    public void setActiveSlot(int slot) {
        if (slot == 1 || slot == 2) activeSlot = slot;
    }
    /**
     * Gibt den aktuell aktiven Inventar-Slot zurück.
     *
     * @return der aktive Slot (1 = Waffe, 2 = Potion)
     */

    public int getActiveSlot() {
        return activeSlot;
    }

    /**
     * Wechselt innerhalb des aktuell aktiven Slots zum nächsten Item.
     * <p>
     * Bei aktiviertem Waffen-Slot wird durch die Waffen im Loadout
     * gewechselt, bei aktiviertem Potion-Slot entsprechend durch
     * die verfügbaren Tränke.
     * Das Scrollen erfolgt zyklisch, sodass nach dem letzten Element
     * wieder beim ersten begonnen wird.
     */

    public void scrollActiveSlot() {
        if (activeSlot == 1) {
            int s = sizeOf(weaponLoadout);
            if (s > 0) weaponIndex = (weaponIndex + 1) % s;
        } else {
            int s = sizeOf(potionLoadout);
            if (s > 0) potionIndex = (potionIndex + 1) % s;
        }
    }
    /**
     * Gibt die aktuell ausgewählte Waffe aus dem Waffen-Loadout zurück.
     * <p>
     * Existieren keine Waffen im Loadout, wird {@code null} zurückgegeben.
     * Der interne Index wird zusätzlich auf einen gültigen Bereich begrenzt,
     * um Zugriffe außerhalb der Liste zu vermeiden.
     *
     * @return die aktuell ausgewählte {@link Weapons} oder {@code null},
     *         falls keine Waffe verfügbar ist
     */

    public Weapons getSelectedWeapon() {
        int s = sizeOf(weaponLoadout);
        if (s == 0) return null;
        weaponIndex = Math.max(0, Math.min(weaponIndex, s - 1));
        return getAt(weaponLoadout, weaponIndex);
    }

    /**
     * Gibt den aktuell ausgewählten Trank aus dem Potion-Loadout zurück.
     * <p>
     * Existieren keine Tränke im Loadout, wird {@code null} zurückgegeben.
     * Der interne Index wird auf einen gültigen Bereich begrenzt,
     * um ungültige Zugriffe zu verhindern.
     *
     * @return der aktuell ausgewählte {@link Potions} oder {@code null},
     *         falls kein Trank verfügbar ist
     */

    public Potions getSelectedPotion() {
        int s = sizeOf(potionLoadout);
        if (s == 0) return null;
        potionIndex = Math.max(0, Math.min(potionIndex, s - 1));
        return getAt(potionLoadout, potionIndex);
    }

    /**
     * Gibt eine Kopie des aktuellen Waffen-Loadouts als {@link java.util.List} zurück.
     * <p>
     * Diese Methode dient als Adapter zur Weitergabe der Waffen
     * an externe Komponenten (z.B. Swing-UI), die mit
     * {@code java.util.List} arbeiten.
     * Änderungen an der zurückgegebenen Liste haben keinen direkten
     * Einfluss auf das interne Loadout.
     *
     * @return eine {@link java.util.List} mit den Waffen des aktuellen Loadouts
     */

    public java.util.List<Weapons> getWeaponLoadoutCopy() {
        return toJavaList(weaponLoadout);
    }

    /**
     * Ersetzt das aktuelle Waffen-Loadout durch ein neues.
     * <p>
     * Das bestehende Loadout wird vollständig geleert und anschließend
     * mit den Waffen aus der übergebenen Liste neu aufgebaut.
     * Null-Werte werden dabei ignoriert.
     * Der interne Auswahlindex wird angepasst, um gültig zu bleiben.
     *
     * @param newLoadout eine {@link java.util.List} mit den neuen Waffen
     *                   für das Loadout
     */

    public void setWeaponLoadout(java.util.List<Weapons> newLoadout) {
        clearDS(weaponLoadout);
        if (newLoadout != null) {
            for (Weapons w : newLoadout) if (w != null) weaponLoadout.append(w);
        }
        if (weaponIndex >= sizeOf(weaponLoadout)) weaponIndex = 0;
    }

    public java.util.List<Potions> getPotionLoadoutCopy() {
        return toJavaList(potionLoadout);
    }

    /**
     * Ersetzt das aktuelle Potion-Loadout durch ein neues.
     * <p>
     * Das bestehende Loadout wird vollständig geleert und anschließend
     * mit den Tränken aus der übergebenen Liste neu aufgebaut.
     * Null-Werte werden dabei ignoriert.
     * Der interne Auswahlindex wird angepasst, um gültig zu bleiben.
     *
     * @param newLoadout eine {@link java.util.List} mit den neuen Tränken
     *                   für das Loadout
     */

    public void setPotionLoadout(java.util.List<Potions> newLoadout) {
        clearDS(potionLoadout);
        if (newLoadout != null) {
            for (Potions p : newLoadout) if (p != null) potionLoadout.append(p);
        }
        if (potionIndex >= sizeOf(potionLoadout)) potionIndex = 0;
    }


    public String getDisplayName(Object obj) {
        if (obj == null) return "-";
        return obj.getClass().getSimpleName();
    }

    /**
     * Verwendet den aktuell ausgewählten Trank und wendet dessen Effekt auf den Spieler an.
     * <p>
     * Abhängig vom Typ des Tranks wird ein entsprechender Effekt ausgelöst:
     * <ul>
     *   <li>{@link HealingPotion}: heilt den Spieler (maximal bis zur Lebensobergrenze)</li>
     *   <li>{@link SpeedPotion}: erhöht für kurze Zeit die Bewegungsgeschwindigkeit</li>
     *   <li>{@link ResistancePotion}: macht den Spieler temporär unverwundbar</li>
     * </ul>
     *
     * Nach der Anwendung wird genau eine Instanz des verwendeten Tranks
     * sowohl aus dem Inventar als auch aus dem Potion-Loadout entfernt.
     * Anschließend wird der interne Auswahlindex angepasst, um gültig zu bleiben.
     *
     * @param player der Spieler, auf den der Trank-Effekt angewendet werden soll
     */

    public void consumeSelectedPotion(Player player) {
        Potions p = getSelectedPotion();
        if (p == null) return;


        if (player != null) {
            if (p instanceof HealingPotion) {
                player.heal(20);
            } else if (p instanceof SpeedPotion) {
                player.activateSpeedBoost(3.0, 2.0);
            } else if (p instanceof ResistancePotion) {
                player.activateInvulnerability(2.0);
            }
        }


        removeFirstOccurrenceByRef(items, p);
        removeFirstOccurrenceByRef(potionLoadout, p);


        int s = sizeOf(potionLoadout);
        if (s == 0) potionIndex = 0;
        else if (potionIndex >= s) potionIndex = s - 1;
    }


    /**
     * Ermittelt die Anzahl der Elemente in einer KAGO-List.
     * <p>
     * Da die verwendete KAGO-Datenstruktur keine direkte
     * {@code size()}-Methode besitzt, wird die Liste vollständig
     * durchlaufen und die Elemente werden gezählt.
     *
     * @param list die KAGO-List, deren Größe bestimmt werden soll
     * @param <T>  der Typ der Elemente in der Liste
     * @return die Anzahl der Elemente in der Liste
     */

    private static <T> int sizeOf(List<T> list) {
        int c = 0;
        list.toFirst();
        while (list.hasAccess()) {
            c++;
            list.next();
        }
        return c;
    }

    /**
     * Gibt das Element an einer bestimmten Position aus einer KAGO-List zurück.
     * <p>
     * Da die KAGO-Datenstruktur keinen direkten indexbasierten Zugriff bietet,
     * wird die Liste sequentiell durchlaufen, bis der gewünschte Index erreicht ist.
     * Ist der Index ungültig, wird {@code null} zurückgegeben.
     *
     * @param list  die KAGO-List, aus der ein Element gelesen werden soll
     * @param index der gewünschte Index (beginnend bei 0)
     * @param <T>   der Typ der Elemente in der Liste
     * @return das Element an der angegebenen Position oder {@code null},
     *         falls der Index nicht existiert
     */

    private static <T> T getAt(List<T> list, int index) {
        int i = 0;
        list.toFirst();
        while (list.hasAccess()) {
            if (i == index) return list.getContent();
            i++;
            list.next();
        }
        return null;
    }

    /**
     * Entfernt alle Elemente aus einer KAGO-List.
     * <p>
     * Die Liste wird vollständig geleert, indem alle vorhandenen
     * Elemente nacheinander entfernt werden.
     * Diese Methode dient als Ersatz für {@code clear()},
     * das in der KAGO-Datenstruktur nicht vorhanden ist.
     *
     * @param list die KAGO-List, die geleert werden soll
     * @param <T>  der Typ der Elemente in der Liste
     */

    private static <T> void clearDS(List<T> list) {
        list.toFirst();
        while (list.hasAccess()) list.remove();
    }
    /**
     * Wandelt eine KAGO-List in eine {@link java.util.List} um.
     * <p>
     * Diese Methode dient als Adapter zwischen der internen
     * KAGO-Datenstruktur und externen Komponenten (z.B. Swing),
     * die mit {@code java.util.List} arbeiten.
     * Die zurückgegebene Liste ist eine Kopie der Daten.
     *
     * @param ds  die KAGO-List, die umgewandelt werden soll
     * @param <T> der Typ der Elemente in der Liste
     * @return eine {@link java.util.List} mit allen Elementen der KAGO-List
     */

    private static <T> java.util.List<T> toJavaList(List<T> ds) {
        ArrayList<T> out = new ArrayList<>();
        ds.toFirst();
        while (ds.hasAccess()) {
            out.add(ds.getContent());
            ds.next();
        }
        return out;
    }

    /**
     * Entfernt das erste Vorkommen eines bestimmten Objekts aus einer KAGO-List.
     * <p>
     * Der Vergleich erfolgt über Referenzgleichheit ({@code ==}),
     * nicht über {@code equals()}, sodass exakt die übergebene
     * Objektinstanz entfernt wird.
     * Wird das Objekt nicht gefunden oder ist das Ziel {@code null},
     * bleibt die Liste unverändert.
     *
     * @param list   die KAGO-List, aus der ein Element entfernt werden soll
     * @param target das Objekt, dessen erste Referenz entfernt werden soll
     * @param <T>    der Typ der Elemente in der Liste
     */

    private static <T> void removeFirstOccurrenceByRef(List<T> list, T target) {
        if (target == null) return;
        list.toFirst();
        while (list.hasAccess()) {
            if (list.getContent() == target) {
                list.remove();
                return;
            }
            list.next();
        }
    }
}
