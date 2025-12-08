package my_project.model.Entities;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Player;
import my_project.control.Controller;

public abstract class Enemy extends Entity {
    private int direction ;
    protected static Controller controller;


    private Player player;
    public Enemy(int xpos, int ypos, double hp, double speed, double stamina, int defense, String Name) {
        super(ypos, xpos, hp, speed, stamina, defense, Name);


    }


    public void draw(DrawTool drawTool){

    }
    public void update(double dt){
        if(controller.followplayerX(this) > 0){
            xpos = xpos + (dt * (100 * speed ));

        }
        if(controller.followplayerX(this) < 0){
            xpos = xpos - (dt * (100 * speed ));

        }
        if(controller.followplayerY(this) > 0){
            ypos = ypos + (dt * (100 * speed ));

        }
        if(controller.followplayerY(this) < 0){
            ypos = ypos - (dt * (100 * speed ));

        }

    }
    public static void setController(Controller con) {
        controller = con;
    }

}

