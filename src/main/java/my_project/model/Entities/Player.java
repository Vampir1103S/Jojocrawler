package my_project.model.Entities;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import KAGO_framework.view.DrawTool;

public class Player extends Entity{
    private int direction ;

    public Player() {
        super(1,1,1,1,1,1,"hehe");


    }

    public void draw(DrawTool drawTool){
        drawTool.drawTool.setCurrentColor(100, 100, 100, 255);
        drawTool.drawFilledRectangle(xpos,ypos, 50, 100);
    }



    public void update(double dt){


        switch(direction) {
            case 0:
                xpos += 100 * dt;
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
    public int setDirection(int d){
        direction = d;
        return d;
    }


}
