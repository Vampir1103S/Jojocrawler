package my_project.model.Entities;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import KAGO_framework.view.DrawTool;
import my_project.Config;

public class Player extends Entity{
    private int directionx ;
    private int directiony ;
    private boolean isDownW ;
    private boolean isDownA ;
    private boolean isDownS ;
    private boolean isDownD ;


    public Player() {
        super(1,1,1,1,1,1,"hehe",50,100);
    this.directionx = 1;
    this.directiony = 1;


    xpos = 200;
    ypos = 200;
    width = 50;
    height = 100;
    }

    public void draw(DrawTool drawTool){
        drawTool.drawFilledRectangle(xpos,ypos, width, height);
    }

    public void update(double dt){

        if(isDownW && ypos > 0){
            ypos -= dt*250;
        }
        if(isDownA && xpos > 0){
            xpos -= dt*250;
        }
        if(isDownS && ypos < Config.WINDOW_HEIGHT - height){
            ypos += dt*250;
        }
        if(isDownD &&  xpos < Config.WINDOW_WIDTH -  width){
            xpos += dt*250;
        }

        if (this.hp == 0){
            System.out.println("TOT");
        }
    }

    public boolean setIsDownWTrue() {
            isDownW = true;
        return isDownW;
    }

    public boolean setIsDownWFalse() {
        isDownW = false;
        return isDownW;
    }

    public boolean setIsDownATrue() {
        isDownA = true;
        return isDownA;
    }

    public boolean setIsDownAFalse() {
        isDownA = false;
        return isDownA;
    }

    public boolean setIsDownSTrue() {
        isDownS = true;
        return isDownS;
    }

    public boolean setIsDownSFalse() {
        isDownS = false;
        return isDownS;
    }

    public boolean setIsDownDTrue() {
        isDownD = true;
        return isDownD;
    }

    public boolean setIsDownDFalse() {
        isDownD = false;
        return isDownD;
    }
    public double getXpos(){
        return xpos;
    }
    public double getYpos(){
        return ypos;
    }


}
