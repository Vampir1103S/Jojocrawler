package my_project;

import my_project.model.Entities.StoryTeller;
import my_project.model.items.Weapons;
import my_project.model.items.Consumables.Potions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class SwingUI {

    public JTextField outputField;
    public JComponent mainPanel;

    // ====== NPC / Story SwingUI (wie vorher) ======
    public SwingUI(StoryTeller storytomole) {
        // Wichtig: outputField/mainPanel werden meist durch IntelliJ .form initialisiert
        // -> Wenn du kein .form nutzt, musst du outputField selbst erstellen.

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

    // ====== INVENTAR-SWING: Waffen auswählen ======
    public static Weapons chooseWeapon(List<Weapons> options) {
        if (options == null || options.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Keine Waffen im Inventar gefunden!",
                    "Waffen-Auswahl",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return null;
        }

        DefaultListModel<Weapons> model = new DefaultListModel<>();
        for (Weapons w : options) model.addElement(w);

        JList<Weapons> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(jList, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("null");
                } else {
                    // Anzeige-Name: falls toString nicht überschrieben ist -> Klassenname
                    String txt = value.toString();
                    if (txt == null || txt.trim().isEmpty() || txt.contains("@")) {
                        txt = value.getClass().getSimpleName();
                    }
                    setText(txt);
                }
                return this;
            }
        });

        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(420, 260));

        int result = JOptionPane.showConfirmDialog(
                null,
                pane,
                "Waffe zu Slot 1 hinzufügen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return list.getSelectedValue();
        }
        return null;
    }

    // ====== INVENTAR-SWING: Potions auswählen ======
    public static Potions choosePotion(List<Potions> options) {
        if (options == null || options.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Keine Potions im Inventar gefunden!",
                    "Potion-Auswahl",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return null;
        }

        DefaultListModel<Potions> model = new DefaultListModel<>();
        for (Potions p : options) model.addElement(p);

        JList<Potions> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(jList, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("null");
                } else {
                    String txt = value.toString();
                    if (txt == null || txt.trim().isEmpty() || txt.contains("@")) {
                        txt = value.getClass().getSimpleName();
                    }
                    setText(txt);
                }
                return this;
            }
        });

        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(420, 260));

        int result = JOptionPane.showConfirmDialog(
                null,
                pane,
                "Potion zu Slot 2 hinzufügen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return list.getSelectedValue();
        }
        return null;
    }
}
