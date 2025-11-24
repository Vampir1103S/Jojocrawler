package my_project.model.Entities;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import KAGO_framework.view.DrawTool;

public class Player extends Entity{
    private int directionx ;
    private int directiony ;
    private boolean isDownW ;
    private boolean isDownA ;
    private boolean isDownS ;
    private boolean isDownD ;

    public Player() {
        super(1,1,1,1,1,1,"hehe");
    this.directionx = 1;
    this.directiony = 1;
    xpos = 200;
    ypos = 200;
    }


    public void draw(DrawTool drawTool){

        drawTool.drawFilledRectangle(xpos,ypos, 50, 100);
    }




    public void update(double dt){

        if(isDownW){
            ypos -= dt * (100 * speed);
        }
        if(isDownA){
            xpos -= dt * (100 * speed);
        }
        if(isDownS){
            ypos += dt * (100 * speed);
        }
        if(isDownD){
            xpos += dt * (100 * speed);
        }
        System.out.println(isDownW+" "+isDownA+" "+isDownS+" "+isDownD);


    }
    public int addDirectionx(int d){
        directionx = directionx + d;
        return d;
    }
    public int addDirectiony(int d){
        directiony = directiony + d;
        return d;
    }
    public int setDirectionx(int d){
        directionx = d;
        return d;
    }
    public int setDirectiony(int d){
        directiony = d;
        return d;
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

}
