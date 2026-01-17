package my_project.model.items;

import my_project.model.items.Consumables.Potions;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    // Alles, was du besitzt (nur Aufzählung im Inventar)
    private final List<Item> items = new ArrayList<>();

    // Hotbar-Listen (NRW Standard: List / ArrayList)
    private final List<Weapons> weaponSlotList = new ArrayList<>();
    private final List<Potions> potionSlotList = new ArrayList<>();

    // Welcher Slot ist "in der Hand" aktiv? 1=Weapon, 2=Potion
    private int activeSlot = 1;

    // Scroll-Index für C
    private int weaponIndex = 0;
    private int potionIndex = 0;

    // ===== Items verwalten =====
    public void addItem(Item item) {
        if (item != null) items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    // ===== Slot-Listen füllen (über Swing Auswahl) =====
    public void addWeaponToSlot(Weapons w) {
        if (w != null) weaponSlotList.add(w);
        if (weaponSlotList.size() == 1) weaponIndex = 0;
    }

    public void addPotionToSlot(Potions p) {
        if (p != null) potionSlotList.add(p);
        if (potionSlotList.size() == 1) potionIndex = 0;
    }

    public List<Weapons> getWeaponSlotList() {
        return weaponSlotList;
    }

    public List<Potions> getPotionSlotList() {
        return potionSlotList;
    }

    // ===== Slot wählen (1/2) =====
    public void setActiveSlot(int slot) {
        if (slot == 1 || slot == 2) activeSlot = slot;
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    // ===== Scrollen (C) =====
    public void scrollActiveSlot() {
        if (activeSlot == 1) {
            if (!weaponSlotList.isEmpty()) {
                weaponIndex = (weaponIndex + 1) % weaponSlotList.size();
            }
        } else {
            if (!potionSlotList.isEmpty()) {
                potionIndex = (potionIndex + 1) % potionSlotList.size();
            }
        }
    }

    // ===== Aktuelles Item in Hand =====
    public Weapons getSelectedWeapon() {
        if (weaponSlotList.isEmpty()) return null;
        weaponIndex = Math.max(0, Math.min(weaponIndex, weaponSlotList.size() - 1));
        return weaponSlotList.get(weaponIndex);
    }

    public Potions getSelectedPotion() {
        if (potionSlotList.isEmpty()) return null;
        potionIndex = Math.max(0, Math.min(potionIndex, potionSlotList.size() - 1));
        return potionSlotList.get(potionIndex);
    }

    // ===== Anzeige-Name (ohne Item-Klasse ändern zu müssen) =====
    public String getDisplayName(Object obj) {
        if (obj == null) return "-";
        return obj.getClass().getSimpleName();
    }
}
