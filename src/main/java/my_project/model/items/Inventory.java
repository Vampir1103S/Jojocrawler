package my_project.model.items;

import KAGO_framework.model.abitur.datenstrukturen.List;
import my_project.model.Entities.Player;
import my_project.model.items.Consumables.Potions;
import my_project.model.items.Consumables.HealingPotion;
import my_project.model.items.Consumables.SpeedPotion;
import my_project.model.items.Consumables.ResistancePotion;

import java.util.ArrayList;

public class Inventory {

    private final List<Item> items = new List<>();

    private final List<Weapons> weaponLoadout = new List<>();
    private final List<Potions> potionLoadout = new List<>();

    private int activeSlot = 1;
    private int weaponIndex = 0;
    private int potionIndex = 0;

    private int coins = 0;

    // ===== Coins =====
    public void addCoins(int amount) {
        coins += Math.max(0, amount);
    }

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

    // ===== Items =====
    public void addItem(Item item) {
        if (item == null) return;
        items.append(item);
    }

    public java.util.List<Item> getItemsAsJavaList() {
        return toJavaList(items);
    }

    public List<Item> getItemsDS() {
        return items;
    }

    // ===== Slot wählen =====
    public void setActiveSlot(int slot) {
        if (slot == 1 || slot == 2) activeSlot = slot;
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    // ===== Scroll =====
    public void scrollActiveSlot() {
        if (activeSlot == 1) {
            int s = sizeOf(weaponLoadout);
            if (s > 0) weaponIndex = (weaponIndex + 1) % s;
        } else {
            int s = sizeOf(potionLoadout);
            if (s > 0) potionIndex = (potionIndex + 1) % s;
        }
    }

    public Weapons getSelectedWeapon() {
        int s = sizeOf(weaponLoadout);
        if (s == 0) return null;
        weaponIndex = Math.max(0, Math.min(weaponIndex, s - 1));
        return getAt(weaponLoadout, weaponIndex);
    }

    public Potions getSelectedPotion() {
        int s = sizeOf(potionLoadout);
        if (s == 0) return null;
        potionIndex = Math.max(0, Math.min(potionIndex, s - 1));
        return getAt(potionLoadout, potionIndex);
    }

    // ===== Loadout Edit Support =====
    public java.util.List<Weapons> getWeaponLoadoutCopy() {
        return toJavaList(weaponLoadout);
    }

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

    public void setPotionLoadout(java.util.List<Potions> newLoadout) {
        clearDS(potionLoadout);
        if (newLoadout != null) {
            for (Potions p : newLoadout) if (p != null) potionLoadout.append(p);
        }
        if (potionIndex >= sizeOf(potionLoadout)) potionIndex = 0;
    }

    public void addWeaponToLoadout(Weapons w) {
        if (w == null) return;
        weaponLoadout.append(w);
        if (sizeOf(weaponLoadout) == 1) weaponIndex = 0;
    }

    public void addPotionToLoadout(Potions p) {
        if (p == null) return;
        potionLoadout.append(p);
        if (sizeOf(potionLoadout) == 1) potionIndex = 0;
    }

    public String getDisplayName(Object obj) {
        if (obj == null) return "-";
        return obj.getClass().getSimpleName();
    }

    // =========================================================
    // ✅ NEU: Potion benutzen -> Effekt + aus Inventar entfernen
    // =========================================================
    public void consumeSelectedPotion(Player player) {
        Potions p = getSelectedPotion();
        if (p == null) return;

        // Effekt anwenden
        if (player != null) {
            if (p instanceof HealingPotion) {
                player.heal(20); // heilt nur bis max (Player.heal macht Clamp)
            } else if (p instanceof SpeedPotion) {
                player.activateSpeedBoost(3.0, 2.0); // 3s x2 speed
            } else if (p instanceof ResistancePotion) {
                player.activateInvulnerability(2.0); // 2s unverwundbar
            }
        }

        // ✅ WICHTIG: genau 1 Instanz entfernen (aus items + loadout)
        removeFirstOccurrenceByRef(items, p);
        removeFirstOccurrenceByRef(potionLoadout, p);

        // Index reparieren
        int s = sizeOf(potionLoadout);
        if (s == 0) potionIndex = 0;
        else if (potionIndex >= s) potionIndex = s - 1;
    }

    // ===== DS Helpers =====
    private static <T> int sizeOf(List<T> list) {
        int c = 0;
        list.toFirst();
        while (list.hasAccess()) {
            c++;
            list.next();
        }
        return c;
    }

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

    private static <T> void clearDS(List<T> list) {
        list.toFirst();
        while (list.hasAccess()) list.remove();
    }

    private static <T> java.util.List<T> toJavaList(List<T> ds) {
        ArrayList<T> out = new ArrayList<>();
        ds.toFirst();
        while (ds.hasAccess()) {
            out.add(ds.getContent());
            ds.next();
        }
        return out;
    }

    // ✅ Entfernt GENAU das Objekt (Referenzvergleich) einmal
    private static <T> void removeFirstOccurrenceByRef(List<T> list, T target) {
        if (target == null) return;
        list.toFirst();
        while (list.hasAccess()) {
            if (list.getContent() == target) { // Referenzvergleich!
                list.remove();
                return;
            }
            list.next();
        }
    }
}
