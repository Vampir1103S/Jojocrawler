package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.view.UI;

import java.awt.*;

public class Controller extends GraphicalObject {
    private int szene = 0;
    public Controller() {}

    public void draw(DrawTool drawTool) {
        switch (szene){
            case 0:
                new UI().draw(drawTool);
            break;
            case 1:

            break;
            case 2:

            break;

            default:

             break;
        }
    }


    @Override
    public void update(double dt){
        switch (szene){
            case 0:
                new UI().update(dt);
                break;
            case 1:

                break;
            case 2:

                break;

            default:

                break;
        }
    }
    public void switchSzene(int newSzene){
        szene = newSzene;
    }

}


