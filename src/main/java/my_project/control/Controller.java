package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.view.Deathscreen;
import my_project.view.UI;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;
    private UI ui;
    private Deathscreen deathscreen;
    public Controller() {
        ui = new UI();
    }

    public void draw(DrawTool drawTool) {
        switch (scene){
            case 0:
                ui.draw(drawTool);
                if (ui.getStartscreen() == false) {
                    deathscreen.draw(drawTool);
                }
            break;
            case 1:

            break;
            case 2:

            break;

            default:

             break;
        }
    }


    @Override
    public void update(double dt){
        switch (scene){
            case 0:
                ui.update(dt);
                deathscreen.update(dt);
                break;
            case 1:

                break;
            case 2:

                break;

            default:

                break;
        }
    }
    public static void switchScene(int newSzene){
        scene = newSzene;
    }
    @Override
    public void mouseClicked(MouseEvent e){
        ui.mouseClicked(e);
    }
    public void keyPressed(KeyEvent e){

    }

}


