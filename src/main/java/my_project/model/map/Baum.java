package my_project.model.map;

import KAGO_framework.view.DrawTool;
import my_project.control.Collisions;

public class Baum extends Environment{

    private int x;
    private int y;

    public Baum(int x, int y){
        this.x = x;
        this.y = y;
        this.setNewImage("src/main/resources/graphic/Baum.png");
    }

    @Override
    public void draw(DrawTool drawTool){
        drawTool.drawImage(getMyImage(), x, y);
    }

    @Override
    public void update(double dt) {

    }
}
