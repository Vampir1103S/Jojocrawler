package my_project;

import my_project.model.Entities.StoryTeller;
import my_project.model.items.Weapons;
import my_project.model.items.Consumables.Potions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SwingUI {

    public JTextField outputField;
    public JComponent mainPanel;

    // ====== NPC / Story SwingUI (wie vorher) ======
    public SwingUI(StoryTeller storytomole) {
        // Falls du kein IntelliJ .form benutzt: sicherstellen, dass outputField existiert
        if (outputField == null) {
            outputField = new JTextField();
        }

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

    // ============================================================
    // ====== LOADOUT EDITOR (V1): Zwei Listen + Buttons ===========
    // ============================================================

    // Waffen-Loadout bearbeiten: Inventar links, Slot-Reihenfolge rechts
    public static List<Weapons> editWeaponsLoadout(List<Weapons> available, List<Weapons> current) {
        DefaultListModel<Weapons> left = new DefaultListModel<>();
        DefaultListModel<Weapons> right = new DefaultListModel<>();

        if (available != null) for (Weapons w : available) left.addElement(w);
        if (current != null) for (Weapons w : current) right.addElement(w);

        JList<Weapons> invList = new JList<>(left);
        JList<Weapons> slotList = new JList<>(right);

        invList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // schöner Renderer (falls toString nicht sauber ist)
        invList.setCellRenderer(simpleRenderer());
        slotList.setCellRenderer(simpleRenderer());

        JButton add = new JButton(">> Add");
        JButton remove = new JButton("<< Remove");
        JButton up = new JButton("Up");
        JButton down = new JButton("Down");

        add.addActionListener(e -> {
            Weapons w = invList.getSelectedValue();
            if (w == null) return;
            if (!modelContains(right, w)) right.addElement(w); // keine Duplikate bei Waffen
        });

        remove.addActionListener(e -> {
            Weapons w = slotList.getSelectedValue();
            if (w == null) return;
            right.removeElement(w);
        });

        up.addActionListener(e -> {
            int i = slotList.getSelectedIndex();
            if (i <= 0) return;
            Weapons w = right.getElementAt(i);
            right.remove(i);
            right.add(i - 1, w);
            slotList.setSelectedIndex(i - 1);
        });

        down.addActionListener(e -> {
            int i = slotList.getSelectedIndex();
            if (i < 0 || i >= right.getSize() - 1) return;
            Weapons w = right.getElementAt(i);
            right.remove(i);
            right.add(i + 1, w);
            slotList.setSelectedIndex(i + 1);
        });

        JPanel buttonsMid = new JPanel(new GridLayout(4, 1, 6, 6));
        buttonsMid.add(add);
        buttonsMid.add(remove);
        buttonsMid.add(up);
        buttonsMid.add(down);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(wrap("Inventar (Waffen)", invList), BorderLayout.WEST);
        panel.add(buttonsMid, BorderLayout.CENTER);
        panel.add(wrap("Slot 1 Loadout (C-scroll)", slotList), BorderLayout.EAST);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Waffen-Loadout bearbeiten",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return null;

        List<Weapons> out = new ArrayList<>();
        for (int i = 0; i < right.getSize(); i++) out.add(right.getElementAt(i));
        return out;
    }

    // Potions-Loadout bearbeiten: Duplikate sind erlaubt (z.B. 2 Heiltränke)
    public static List<Potions> editPotionsLoadout(List<Potions> available, List<Potions> current) {
        DefaultListModel<Potions> left = new DefaultListModel<>();
        DefaultListModel<Potions> right = new DefaultListModel<>();

        if (available != null) for (Potions p : available) left.addElement(p);
        if (current != null) for (Potions p : current) right.addElement(p);

        JList<Potions> invList = new JList<>(left);
        JList<Potions> slotList = new JList<>(right);

        invList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        invList.setCellRenderer(simpleRenderer());
        slotList.setCellRenderer(simpleRenderer());

        JButton add = new JButton(">> Add");
        JButton remove = new JButton("<< Remove");
        JButton up = new JButton("Up");
        JButton down = new JButton("Down");

        add.addActionListener(e -> {
            Potions p = invList.getSelectedValue();
            if (p == null) return;
            right.addElement(p); // Potions dürfen doppelt
        });

        remove.addActionListener(e -> {
            Potions p = slotList.getSelectedValue();
            if (p == null) return;
            right.removeElement(p);
        });

        up.addActionListener(e -> {
            int i = slotList.getSelectedIndex();
            if (i <= 0) return;
            Potions p = right.getElementAt(i);
            right.remove(i);
            right.add(i - 1, p);
            slotList.setSelectedIndex(i - 1);
        });

        down.addActionListener(e -> {
            int i = slotList.getSelectedIndex();
            if (i < 0 || i >= right.getSize() - 1) return;
            Potions p = right.getElementAt(i);
            right.remove(i);
            right.add(i + 1, p);
            slotList.setSelectedIndex(i + 1);
        });

        JPanel buttonsMid = new JPanel(new GridLayout(4, 1, 6, 6));
        buttonsMid.add(add);
        buttonsMid.add(remove);
        buttonsMid.add(up);
        buttonsMid.add(down);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(wrap("Inventar (Potions)", invList), BorderLayout.WEST);
        panel.add(buttonsMid, BorderLayout.CENTER);
        panel.add(wrap("Slot 2 Loadout (C-scroll)", slotList), BorderLayout.EAST);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Potion-Loadout bearbeiten",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return null;

        List<Potions> out = new ArrayList<>();
        for (int i = 0; i < right.getSize(); i++) out.add(right.getElementAt(i));
        return out;
    }

    // ===== Helper =====
    private static <T> boolean modelContains(DefaultListModel<T> model, T value) {
        for (int i = 0; i < model.size(); i++) {
            if (model.getElementAt(i) == value) return true;
        }
        return false;
    }

    private static JComponent wrap(String title, JList<?> list) {
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(320, 280));
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private static DefaultListCellRenderer simpleRenderer() {
        return new DefaultListCellRenderer() {
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
        };
    }
}
