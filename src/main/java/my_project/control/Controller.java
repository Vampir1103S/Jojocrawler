package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Dieb;
import my_project.model.map.Baum;
import my_project.model.map.Bürgersteig;
import my_project.view.Deathscreen;
import my_project.view.UI;
import my_project.model.Entities.Player;
import my_project.model.Entities.Enemy;
import my_project.model.Entities.Entity;
import my_project.control.Collisions;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;
    private UI ui;
    private Player player;
    private Deathscreen deathscreen;
    private Entity entity;
    private Collisions collisions;
    private Bürgersteig[][] bürgersteig;
    private Baum[][] baum;
    private int hoehe = 10;
    private int breite = 10;
    private int bhoehe = 2;
    private int bbreite = 10;


    //Enemies
    private Enemy enemy;
    private Enemy dieb;

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        player = new Player();
        dieb = new Dieb();
        collisions = new Collisions();
        Enemy.setController(this);

        bürgersteig = new Bürgersteig[breite][hoehe];
        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < hoehe; y++) {
                bürgersteig[x][y] = new Bürgersteig(100 + x * 30, 100 + y * 30);
            }
        }

        baum = new Baum[bbreite][bhoehe];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 2; y++) {
                baum[x][y] = new Baum(100 + x * 70, 100 + y * 200);
            }
        }
    }


    public void draw(DrawTool drawTool) {
        switch (scene){
            case 0:
                ui.draw(drawTool);

            break;
            case 1:
                for (int x = 0; x < bürgersteig.length; x++) {
                    for (int y = 0; y < bürgersteig[x].length; y++) {
                        bürgersteig[x][y].draw(drawTool);
                    }
                }
                for (int x = 0; x < baum.length; x++) {
                    for (int y = 0; y < baum[x].length; y++) {
                        baum[x][y].draw(drawTool);
                    }
                }

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
                System.out.println(collisions.calculateColisions(player, dieb));
                if (collisions.calculateColisions(player, dieb)){
                    player.setHP(0);
                    System.out.println(player.getHP());
                }
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




