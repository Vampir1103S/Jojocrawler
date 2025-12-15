package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Dieb;
import my_project.view.Deathscreen;
import my_project.view.UI;
import my_project.model.Entities.Player;
import my_project.model.Entities.Enemy;
import my_project.model.Entities.Entity;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;
    private UI ui;
    private Player player;
    private Deathscreen deathscreen;
    private Entity entity;


    //Enemies
    private Enemy enemy;
    private Enemy dieb;

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        player = new Player();
        dieb = new Dieb();
        Enemy.setController(this);
        this.setNewImage("src/main/resources/graphic/map101.png");

    }


    public void draw(DrawTool drawTool) {
        switch (scene){
            case 0:
                ui.draw(drawTool);

            break;
            case 1:
                drawTool.drawImage(getMyImage(), 0, 0);
                if (player.getHP() > 0) {
                    player.draw(drawTool);
                }
                dieb.draw(drawTool);

            break;
            case 2:


            break;
            case 3:
                deathscreen.draw(drawTool);
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
                dieb.update(dt);
                break;
            case 2:


                break;
            case 3:
                deathscreen.update(dt);
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
        switch (scene){
            case 0:
                ui.mouseClicked(e);
                break;
            case 1:
                //deathscreen.mouseClicked(e);
                break;
            case 2:
                break;

                default:
                    break;
        }

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
    public double followplayerX(Entity e){
        double xV = 0;
        if (player != null && e != null) {
            xV = player.getXpos() - e.getXpos();
        }

       return xV;
    }
    public double followplayerY(Entity e){
        double yV = 0;
        if (player != null && e != null) {
            yV = player.getYpos() - e.getYpos();
        }
        return yV;
    }



}




