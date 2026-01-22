package my_project;

import my_project.model.Entities.Player;
import my_project.model.Entities.StoryTeller;

import my_project.model.items.Inventory;
import my_project.model.items.Item;


import my_project.model.items.Sword;
import my_project.model.items.Dagger;
import my_project.model.items.Spear;
import my_project.model.items.Axe;
import my_project.model.items.Weapons;

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

/**
 * Grafische Benutzeroberfläche (Swing) für Dialoge, Loadout-Verwaltung
 * und den Händler-Shop.
 * <p>
 * Die Klasse {@code SwingUI} stellt mehrere Swing-basierte Dialoge bereit:
 * <ul>
 *   <li>NPC-Dialoge (z.B. StoryTeller)</li>
 *   <li>Editoren zum Bearbeiten von Waffen- und Potion-Loadouts</li>
 *   <li>Einen Shop zum Kaufen von Waffen, Tränken und Upgrades</li>
 * </ul>
 *
 * Sie fungiert als Brücke zwischen der Ingame-Logik (Controller, Inventory,
 * Player) und externen Swing-Fenstern. Die eigentliche Spiellogik bleibt dabei
 * im Spielcode, während {@code SwingUI} ausschließlich für Darstellung und
 * Benutzereingaben zuständig ist.
 *
 * Die Kommunikation erfolgt über klar definierte Methodenaufrufe
 * (z.B. Rückgabe bearbeiteter Loadouts oder Ausführen von Kaufaktionen).
 */

public class SwingUI {

    public JTextField outputField;
    public JComponent mainPanel;


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

    /**
     * Öffnet den Loadout-Editor für Waffen.
     * <p>
     * Delegiert an den generischen Editor {@link #editLoadoutGeneric(String, List, List)} und
     * gibt die vom Nutzer bestätigte Reihenfolge (rechts) als neue Loadout-Liste zurück.
     * <p>
     * Falls der Dialog abgebrochen wird, wird {@code null} zurückgegeben.
     *
     * @param available alle im Inventar verfügbaren Waffen (Kandidaten-Liste)
     * @param current   aktuell ausgerüstetes Waffen-Loadout (Reihenfolge relevant)
     * @return neue Loadout-Liste bei OK, sonst {@code null}
     */
    public static List<Weapons> editWeaponsLoadout(List<Weapons> available, List<Weapons> current) {
        return editLoadoutGeneric("Waffen-Loadout bearbeiten", available, current);
    }

    /**
     * Öffnet den Loadout-Editor für Potions.
     * <p>
     * Delegiert an den generischen Editor {@link #editLoadoutGeneric(String, List, List)} und
     * gibt die vom Nutzer bestätigte Reihenfolge (rechts) als neue Loadout-Liste zurück.
     * <p>
     * Falls der Dialog abgebrochen wird, wird {@code null} zurückgegeben.
     *
     * @param available alle im Inventar verfügbaren Potions (Kandidaten-Liste)
     * @param current   aktuell ausgerüstetes Potion-Loadout (Reihenfolge relevant)
     * @return neue Loadout-Liste bei OK, sonst {@code null}
     */
    public static List<Potions> editPotionsLoadout(List<Potions> available, List<Potions> current) {
        return editLoadoutGeneric("Potions-Loadout bearbeiten", available, current);
    }

    /**
     * Öffnet einen generischen Swing-Editor zum Bearbeiten eines Loadouts.
     * <p>
     * UI-Aufbau:
     * <ul>
     *   <li>Links: "Verfügbar" (alles, was nicht im aktuellen Loadout ist)</li>
     *   <li>Rechts: Loadout (ohne Duplikate, Reihenfolge ist wichtig)</li>
     * </ul>
     * Bedienung:
     * <ul>
     *   <li>{@code Add →}: verschiebt selektiertes Element von links nach rechts</li>
     *   <li>{@code ← Remove}: verschiebt selektiertes Element von rechts nach links</li>
     *   <li>{@code ↑}/{@code ↓}: verschiebt die Reihenfolge innerhalb des Loadouts</li>
     * </ul>
     * <p>
     * Duplikate werden über Referenzvergleich verhindert (nicht über {@code equals()}).
     * Bei {@code OK} wird die rechte Liste als neue {@link java.util.ArrayList} zurückgegeben,
     * bei Abbruch {@code null}.
     *
     * @param title     Fenstertitel des Dialogs
     * @param available alle verfügbaren Elemente (kann {@code null} sein)
     * @param current   aktuell ausgerüstetes Loadout (kann {@code null} sein)
     * @param <T>       Elementtyp (z.B. Weapons oder Potions)
     * @return neue Loadout-Liste bei OK, sonst {@code null}
     */
    private static <T> List<T> editLoadoutGeneric(String title, List<T> available, List<T> current) {
        if (available == null) available = new ArrayList<>();
        if (current == null) current = new ArrayList<>();


        DefaultListModel<T> availableModel = new DefaultListModel<>();
        for (T t : available) {
            if (!containsRef(current, t)) {
                availableModel.addElement(t);
            }
        }


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
    /**
     * Erstellt einen einfachen {@link DefaultListCellRenderer} für {@link JList}-Einträge.
     * <p>
     * Der Renderer zeigt nicht {@code toString()}, sondern den Klassennamen
     * des Objekts ({@code getClass().getSimpleName()}). Für {@code null} wird "null" angezeigt.
     *
     * @return ein {@link DefaultListCellRenderer} zur Anzeige der Elemente
     */
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

    /**
     * Prüft, ob eine Liste ein Element per Referenzvergleich enthält.
     * <p>
     * Es wird {@code ==} verwendet (gleiche Objektinstanz), nicht {@code equals()}.
     *
     * @param list   Liste, in der gesucht wird (kann {@code null} sein)
     * @param target gesuchtes Objekt (Referenz)
     * @param <T>    Elementtyp
     * @return {@code true}, wenn die exakte Objektinstanz enthalten ist, sonst {@code false}
     */
    private static <T> boolean containsRef(List<T> list, T target) {
        if (list == null) return false;
        for (T t : list) {
            if (t == target) return true;
        }
        return false;
    }
    /**
     * Prüft, ob ein {@link DefaultListModel} ein Element per Referenzvergleich enthält.
     * <p>
     * Es wird {@code ==} verwendet (gleiche Objektinstanz), nicht {@code equals()}.
     *
     * @param model  Model, in dem gesucht wird
     * @param target gesuchtes Objekt (Referenz)
     * @param <T>    Elementtyp
     * @return {@code true}, wenn die exakte Objektinstanz enthalten ist, sonst {@code false}
     */
    private static <T> boolean modelContainsRef(DefaultListModel<T> model, T target) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.get(i) == target) return true;
        }
        return false;
    }



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

    /**
     * Öffnet den Händler-Shop als Swing-Dialog.
     * <p>
     * Der Shop bietet kaufbare Einträge aus den Kategorien:
     * <ul>
     *   <li>Waffen</li>
     *   <li>Potions</li>
     *   <li>Upgrades (nur wenn {@code player != null})</li>
     * </ul>
     * Beim Kauf wird:
     * <ol>
     *   <li>geprüft, ob genug Coins vorhanden sind ({@link Inventory#spendCoins(int)})</li>
     *   <li>die Kaufaktion ausgeführt (Item hinzufügen oder Player-Stat erhöhen)</li>
     *   <li>die Inventar-Anzeige im UI aktualisiert</li>
     * </ol>
     *
     * @param inventory Inventar, aus dem Coins abgezogen und in das Items gelegt werden
     * @param player    Spieler für Upgrades; wenn {@code null}, werden Upgrades deaktiviert
     */
    public static void openShop(Inventory inventory, Player player) {
        if (inventory == null) return;

        boolean upgradesEnabled = (player != null);

        List<ShopEntry> offer = new ArrayList<>();


        offer.add(new ShopEntry(Category.WEAPONS, "Dagger (schnell, wenig dmg)", 35,
                () -> inventory.addItem(new Dagger())));

        offer.add(new ShopEntry(Category.WEAPONS, "Sword (balanced)", 55,
                () -> inventory.addItem(new Sword())));

        offer.add(new ShopEntry(Category.WEAPONS, "Spear (viel Reichweite)", 80,
                () -> inventory.addItem(new Spear())));

        offer.add(new ShopEntry(Category.WEAPONS, "Axe (sehr viel dmg, langsam)", 110,
                () -> inventory.addItem(new Axe())));


        offer.add(new ShopEntry(Category.POTIONS, "HealingPotion (+20 HP)", 25,
                () -> inventory.addItem(new HealingPotion())));

        offer.add(new ShopEntry(Category.POTIONS, "SpeedPotion (temporär schneller)", 45,
                () -> inventory.addItem(new SpeedPotion())));

        offer.add(new ShopEntry(Category.POTIONS, "ResistancePotion (kurz kein Schaden)", 65,
                () -> inventory.addItem(new ResistancePotion())));


        if (upgradesEnabled) {
            offer.add(new ShopEntry(Category.UPGRADES, "Speed Boost +10% (permanent)", 120,
                    () -> player.addMoveSpeedMultiplier(0.10)));

            offer.add(new ShopEntry(Category.UPGRADES, "Max HP +20 (permanent)", 140,
                    () -> player.increaseMaxHP(20)));


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
