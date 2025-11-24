package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.view.UI;
import my_project.model.Entities.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;
    private UI ui;
    private Player player;
    public Controller() {

        ui = new UI();
        player = new Player();
    }


    public void draw(DrawTool drawTool) {
        switch (scene){
            case 0:
                ui.draw(drawTool);

            break;
            case 1:
                player.draw(drawTool);
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
                break;
            case 1:
                player.update(dt);
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
    @Override
    public void keyPressed(int key){
        if(key == KeyEvent.VK_W){
            player.setIsDownWTrue();
        }
        if(key == KeyEvent.VK_A){
            player.setIsDownATrue();
        }
        if(key == KeyEvent.VK_S){
            player.setIsDownSTrue();
        }
        if(key == KeyEvent.VK_D){
            player.setIsDownDTrue();
        }
    }
    public void keyReleased(int key){
        if(key == KeyEvent.VK_W){
            player.setIsDownWFalse();
        }
        if(key == KeyEvent.VK_A){
            player.setIsDownAFalse();
        }
        if(key == KeyEvent.VK_S){
            player.setIsDownSFalse();
        }
        if(key == KeyEvent.VK_D){
            player.setIsDownDFalse();
        }
    }
}




