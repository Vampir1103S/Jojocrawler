package my_project.model.Entities;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Player;

public class Enemy extends Entity {

    private Player player;
    public Enemy(int xpos, int ypos, double hp, int speed, double stamina, int defense, String Name) {
        super(ypos, xpos, hp, speed, stamina, defense, Name);


    }
    public void hallo(){
        System.out.println(player.getXpos());
    }
    public void draw(DrawTool drawTool){
        drawTool.setCurrentColor(200,1,1,255);
        drawTool.drawFilledRectangle(xpos,ypos, 50, 50);
    }
    public void update(double dt){

    }



}
