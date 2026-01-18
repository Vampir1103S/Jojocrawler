package my_project.model.items;

import my_project.model.items.Consumables.Potions;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    // Besitzliste (Inventar-Aufzählung)
    private final List<Item> items = new ArrayList<>();

    // Slot 1 (Waffen-Loadout)
    private final List<Weapons> weaponLoadout = new ArrayList<>();

    // Slot 2 (Potion-Loadout)
    private final List<Potions> potionLoadout = new ArrayList<>();

    private int activeSlot = 1;

    private int weaponIndex = 0;
    private int potionIndex = 0;

    // ===== Items =====
    public void addItem(Item item) {
        if (item != null) items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    // ===== Active Slot =====
    public void setActiveSlot(int slot) {
        if (slot == 1 || slot == 2) activeSlot = slot;
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    // ===== Loadout Copy/Set (für Swing Editor V1) =====
    public List<Weapons> getWeaponLoadoutCopy() {
        return new ArrayList<>(weaponLoadout);
    }

    public void setWeaponLoadout(List<Weapons> newOrder) {
        weaponLoadout.clear();
        if (newOrder != null) {
            for (Weapons w : newOrder) {
                if (w != null && !weaponLoadout.contains(w)) weaponLoadout.add(w); // keine Duplikate
            }
        }
        weaponIndex = Math.min(weaponIndex, Math.max(0, weaponLoadout.size() - 1));
        if (weaponIndex < 0) weaponIndex = 0;
    }

    public List<Potions> getPotionLoadoutCopy() {
        return new ArrayList<>(potionLoadout);
    }

    public void setPotionLoadout(List<Potions> newOrder) {
        potionLoadout.clear();
        if (newOrder != null) {
            for (Potions p : newOrder) {
                if (p != null) potionLoadout.add(p); // Potions dürfen doppelt
            }
        }
        potionIndex = Math.min(potionIndex, Math.max(0, potionLoadout.size() - 1));
        if (potionIndex < 0) potionIndex = 0;
    }

    // ===== Für Hotbar Anzeige =====
    public Weapons getSelectedWeapon() {
        if (weaponLoadout.isEmpty()) return null;
        weaponIndex = Math.max(0, Math.min(weaponIndex, weaponLoadout.size() - 1));
        return weaponLoadout.get(weaponIndex);
    }

    public Potions getSelectedPotion() {
        if (potionLoadout.isEmpty()) return null;
        potionIndex = Math.max(0, Math.min(potionIndex, potionLoadout.size() - 1));
        return potionLoadout.get(potionIndex);
    }

    // ===== Scroll (C) =====
    public void scrollActiveSlot() {
        if (activeSlot == 1) {
            if (weaponLoadout.isEmpty()) return;
            weaponIndex = (weaponIndex + 1) % weaponLoadout.size();
        } else {
            if (potionLoadout.isEmpty()) return;
            potionIndex = (potionIndex + 1) % potionLoadout.size();
        }
    }

    // ===== Anzeige-Name =====
    public String getDisplayName(Object obj) {
        if (obj == null) return "-";
        return obj.toString();
    }

    // Optional Helper (wenn du schnell default loadout setzen willst)
    public void addWeaponToLoadout(Weapons w) {
        if (w == null) return;
        if (!weaponLoadout.contains(w)) weaponLoadout.add(w);
    }

    public void addPotionToLoadout(Potions p) {
        if (p == null) return;
        potionLoadout.add(p);
    }
}
