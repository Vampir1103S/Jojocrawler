package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Player;
import my_project.view.Deathscreen;
import my_project.view.UI;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;
    private UI ui;
    private Deathscreen deathscreen;
    private Player player;
    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        player = new Player();
    }

    public void draw(DrawTool drawTool) {
        switch (scene){
            case 0:
                ui.draw(drawTool);
            break;
            case 1:
                if (ui.getStartscreen() == false) {
                    deathscreen.draw(drawTool);
                }
            break;
            case 2:
                player.draw(drawTool);
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
                break;
            case 1:
                deathscreen.update(dt);
                break;
            case 2:
                player.update(dt);
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
        deathscreen.mouseClicked(e);
    }
    public void keyPressed(KeyEvent e){

    }

}


