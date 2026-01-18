package my_project.model.map;

import KAGO_framework.view.DrawTool;

public class LevelOne extends Level {

    public LevelOne() {
        super(20, 1.0, 20, 20);
    }
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(255, 0, 255, 255);
        drawTool.drawRectangle(0,0,1000,1200);
    }
}
