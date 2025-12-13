package my_project.model.map;

import KAGO_framework.view.DrawTool;

public class Bürgersteig extends Environment{

    private int x;
    private int y;

    public Bürgersteig(int x, int y){
        this.x = x;
        this.y = y;
        this.setNewImage("src/main/resources/graphic/Bürgersteig-Boden.png");
    }

    @Override
    public void draw(DrawTool drawTool){
        drawTool.drawImage(getMyImage(), x, y);
    }
}
