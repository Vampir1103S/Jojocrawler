package my_project;

import my_project.model.Entities.StoryTeller;
import my_project.model.items.Inventory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SwingUI {

    public JTextField outputField;
    public JComponent mainPanel;


    public SwingUI(StoryTeller storytomole) {
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
}
