package my_project.model.Entities;

import javax.xml.namespace.QName;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity{
    private int direction ;

    public Player() {
        super(100,100,100,1,100,1,"Kwadori");


    }
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_LEFT) {
            direction = 0;
        }
    }


    public void update(double dt){
        switch(direction) {
            case 0:
                xpos = xpos + 100 * dt;
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
    


}
