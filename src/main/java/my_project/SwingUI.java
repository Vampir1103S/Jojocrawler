package my_project;

import my_project.model.Entities.Player;
import my_project.model.Entities.StoryTeller;

import my_project.model.items.Inventory;
import my_project.model.items.Item;

// Weapons
import my_project.model.items.Sword;
import my_project.model.items.Dagger;
import my_project.model.items.Spear;
import my_project.model.items.Axe;
import my_project.model.items.Weapons;

// Potions
import my_project.model.items.Consumables.Potions;
import my_project.model.items.Consumables.HealingPotion;
import my_project.model.items.Consumables.SpeedPotion;
import my_project.model.items.Consumables.ResistancePotion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SwingUI {

    public JTextField outputField;
    public JComponent mainPanel;

    // ====== NPC / Story SwingUI ======
    public SwingUI(StoryTeller storytomole) {
        if (outputField == null) outputField = new JTextField();

        outputField.setText("Du musst die Taste E drücken.");
        outputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    String dialog = storytomole.speak();
                    outputField.setText(dialog);
                }
            }
        });
    }

    // =========================================================
    // ✅ LOADOUT EDITOR (Waffen)
    // =========================================================
    public static List<Weapons> editWeaponsLoadout(List<Weapons> available, List<Weapons> current) {
        return editLoadoutGeneric("Waffen-Loadout bearbeiten", available, current);
    }

    // =========================================================
    // ✅ LOADOUT EDITOR (Potions)
    // =========================================================
    public static List<Potions> editPotionsLoadout(List<Potions> available, List<Potions> current) {
        return editLoadoutGeneric("Potions-Loadout bearbeiten", available, current);
    }

    // =========================================================
    // ✅ Generic Editor (2 Listen + Add/Remove/Up/Down)
    // ✅ FIX: echtes Verschieben statt Duplizieren
    // =========================================================
    private static <T> List<T> editLoadoutGeneric(String title, List<T> available, List<T> current) {
        if (available == null) available = new ArrayList<>();
        if (current == null) current = new ArrayList<>();

        // Links = "verfügbar" (alles was NICHT im Loadout ist)
        DefaultListModel<T> availableModel = new DefaultListModel<>();
        for (T t : available) {
            if (!containsRef(current, t)) {
                availableModel.addElement(t);
            }
        }

        // Rechts = Loadout (ohne Duplikate)
        DefaultListModel<T> loadoutModel = new DefaultListModel<>();
        for (T t : current) {
            if (t != null && !modelContainsRef(loadoutModel, t)) {
                loadoutModel.addElement(t);
            }
        }

        JList<T> availableList = new JList<>(availableModel);
        JList<T> loadoutList = new JList<>(loadoutModel);

        availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadoutList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        availableList.setCellRenderer(simpleRenderer());
        loadoutList.setCellRenderer(simpleRenderer());

        JButton addBtn = new JButton("Add →");
        JButton removeBtn = new JButton("← Remove");
        JButton upBtn = new JButton("↑");
        JButton downBtn = new JButton("↓");

        // ✅ Move: links -> rechts
        addBtn.addActionListener(e -> {
            int idx = availableList.getSelectedIndex();
            if (idx < 0) return;

            T sel = availableModel.getElementAt(idx);
            if (sel == null) return;

            availableModel.remove(idx);

            if (!modelContainsRef(loadoutModel, sel)) {
                loadoutModel.addElement(sel);
                loadoutList.setSelectedIndex(loadoutModel.getSize() - 1);
            }
        });

        // ✅ Move: rechts -> links
        removeBtn.addActionListener(e -> {
            int idx = loadoutList.getSelectedIndex();
            if (idx < 0) return;

            T sel = loadoutModel.getElementAt(idx);
            if (sel == null) return;

            loadoutModel.remove(idx);

            if (!modelContainsRef(availableModel, sel)) {
                availableModel.addElement(sel);
                availableList.setSelectedIndex(availableModel.getSize() - 1);
            }
        });

        upBtn.addActionListener(e -> {
            int idx = loadoutList.getSelectedIndex();
            if (idx > 0) {
                T val = loadoutModel.get(idx);
                loadoutModel.remove(idx);
                loadoutModel.add(idx - 1, val);
                loadoutList.setSelectedIndex(idx - 1);
            }
        });

        downBtn.addActionListener(e -> {
            int idx = loadoutList.getSelectedIndex();
            if (idx >= 0 && idx < loadoutModel.getSize() - 1) {
                T val = loadoutModel.get(idx);
                loadoutModel.remove(idx);
                loadoutModel.add(idx + 1, val);
                loadoutList.setSelectedIndex(idx + 1);
            }
        });

        JPanel center = new JPanel(new GridLayout(1, 3, 10, 10));
        center.add(new JScrollPane(availableList));

        JPanel midButtons = new JPanel(new GridLayout(4, 1, 5, 5));
        midButtons.add(addBtn);
        midButtons.add(removeBtn);
        midButtons.add(upBtn);
        midButtons.add(downBtn);
        center.add(midButtons);

        center.add(new JScrollPane(loadoutList));

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.add(new JLabel("Links: Verfügbar | Rechts: Loadout (Verschieben, nicht kopieren)"), BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(null, root, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return null;

        List<T> result = new ArrayList<>();
        for (int i = 0; i < loadoutModel.getSize(); i++) {
            result.add(loadoutModel.get(i));
        }
        return result;
    }

    private static DefaultListCellRenderer simpleRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "null" : value.getClass().getSimpleName());
                return this;
            }
        };
    }

    // ===== Helpers: Referenz-basierte Checks =====
    private static <T> boolean containsRef(List<T> list, T target) {
        if (list == null) return false;
        for (T t : list) {
            if (t == target) return true;
        }
        return false;
    }

    private static <T> boolean modelContainsRef(DefaultListModel<T> model, T target) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.get(i) == target) return true;
        }
        return false;
    }

    // =========================================================
    // ✅ SHOP UI: Waffen + Potions + Stat-Upgrades
    // =========================================================

    private enum Category { WEAPONS, POTIONS, UPGRADES }

    private static class ShopEntry {
        final Category category;
        final String name;
        final int price;
        final Runnable buyAction;

        ShopEntry(Category category, String name, int price, Runnable buyAction) {
            this.category = category;
            this.name = name;
            this.price = price;
            this.buyAction = buyAction;
        }

        @Override
        public String toString() {
            String cat =
                    category == Category.WEAPONS ? "Waffe" :
                            category == Category.POTIONS ? "Potion" : "Upgrade";
            return "[" + cat + "] " + name + " (" + price + " Coins)";
        }
    }

    // ✅ Shop: jetzt mit teuren / besseren Waffen
    public static void openShop(Inventory inventory, Player player) {
        if (inventory == null) return;

        boolean upgradesEnabled = (player != null);

        List<ShopEntry> offer = new ArrayList<>();

        // =======================
        // ===== Waffen (teuer) ===
        // =======================
        // Fists NICHT kaufen (Startwaffe) -> nicht anbieten

        // Preise: bessere Waffe = teurer
        offer.add(new ShopEntry(Category.WEAPONS, "Dagger (schnell, wenig dmg)", 35,
                () -> inventory.addItem(new Dagger())));

        offer.add(new ShopEntry(Category.WEAPONS, "Sword (balanced)", 55,
                () -> inventory.addItem(new Sword())));

        offer.add(new ShopEntry(Category.WEAPONS, "Spear (viel Reichweite)", 80,
                () -> inventory.addItem(new Spear())));

        offer.add(new ShopEntry(Category.WEAPONS, "Axe (sehr viel dmg, langsam)", 110,
                () -> inventory.addItem(new Axe())));

        // =======================
        // ===== Potions =========
        // =======================
        offer.add(new ShopEntry(Category.POTIONS, "HealingPotion (+20 HP)", 25,
                () -> inventory.addItem(new HealingPotion())));

        offer.add(new ShopEntry(Category.POTIONS, "SpeedPotion (temporär schneller)", 45,
                () -> inventory.addItem(new SpeedPotion())));

        offer.add(new ShopEntry(Category.POTIONS, "ResistancePotion (kurz kein Schaden)", 65,
                () -> inventory.addItem(new ResistancePotion())));

        // =======================
        // ===== Upgrades ========
        // =======================
        if (upgradesEnabled) {
            offer.add(new ShopEntry(Category.UPGRADES, "Speed Boost +10% (permanent)", 120,
                    () -> player.addMoveSpeedMultiplier(0.10)));

            offer.add(new ShopEntry(Category.UPGRADES, "Max HP +20 (permanent)", 140,
                    () -> player.increaseMaxHP(20)));

            // Optional: noch ein Upgrade, damit es sich lohnt
            offer.add(new ShopEntry(Category.UPGRADES, "Max HP +50 (permanent)", 300,
                    () -> player.increaseMaxHP(50)));
        }

        DefaultListModel<ShopEntry> shopModel = new DefaultListModel<>();
        for (ShopEntry e : offer) shopModel.addElement(e);

        DefaultListModel<String> invModel = new DefaultListModel<>();
        for (Item it : inventory.getItemsAsJavaList()) {
            invModel.addElement(it.getClass().getSimpleName());
        }

        JList<ShopEntry> shopList = new JList<>(shopModel);
        shopList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JList<String> invList = new JList<>(invModel);

        JLabel coinLabel = new JLabel("Coins: " + inventory.getCoins());
        JLabel hintLabel = new JLabel(upgradesEnabled
                ? "Upgrades wirken sofort auf den Spieler."
                : "Upgrades deaktiviert (Player=null).");

        JButton buyBtn = new JButton("Kaufen →");
        buyBtn.addActionListener(ev -> {
            int idx = shopList.getSelectedIndex();
            if (idx < 0) return;

            ShopEntry entry = shopModel.get(idx);

            if (!inventory.spendCoins(entry.price)) {
                JOptionPane.showMessageDialog(null,
                        "Nicht genug Coins!\nPreis: " + entry.price,
                        "Shop",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            entry.buyAction.run();

            // rechts aktualisieren
            invModel.clear();
            for (Item it : inventory.getItemsAsJavaList()) {
                invModel.addElement(it.getClass().getSimpleName());
            }

            coinLabel.setText("Coins: " + inventory.getCoins());
        });

        JPanel center = new JPanel(new GridLayout(1, 3, 10, 10));
        center.add(new JScrollPane(shopList));
        center.add(buyBtn);
        center.add(new JScrollPane(invList));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(coinLabel);
        top.add(hintLabel);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, root, "Händler-Shop", JOptionPane.PLAIN_MESSAGE);
    }
}
