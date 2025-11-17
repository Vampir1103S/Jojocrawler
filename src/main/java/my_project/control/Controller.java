package my_project.control;

import KAGO_framework.model.GraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.view.UI;

import java.awt.*;

public class Controller extends GraphicalObject {
    private static int scene = 0;
    public Controller() {}

    public void draw(DrawTool drawTool) {
        switch (scene){
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
        switch (scene){
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
    public static void switchScene(int newSzene){
        scene = newSzene;
    }

}


