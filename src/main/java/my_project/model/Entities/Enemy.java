package my_project.model.Entities;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Player;
import my_project.control.Controller;

public class Enemy extends Entity {
    private int direction ;
    private Controller controller;

    private Player player;
    public Enemy(int xpos, int ypos, double hp, int speed, double stamina, int defense, String Name, Controller controller) {
        super(ypos, xpos, hp, speed, stamina, defense, Name);
        this.controller = controller;

    }


    public void draw(DrawTool drawTool){

    }
    public void update(double dt){
        if(controller.followplayerX() > xpos){
            xpos = xpos + (dt * 100);
            System.out.println(dt);
        }
        if(controller.followplayerX() < xpos){
            xpos = xpos + (dt * 100);
            System.out.println(dt);
        }
        if(controller.followplayerY() > ypos){
            xpos = xpos + (dt * 100);
            System.out.println(dt);
        }
        if(controller.followplayerY() < ypos){
            xpos = xpos + (dt * 100);
            System.out.println(dt);
        }

    }



}

