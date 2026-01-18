package my_project.model.map;

import KAGO_framework.view.DrawTool;

public class Gate extends Environment {

    private final double gateX;
    private final double gateY;
    private final double gateWidth;
    private final double gateHeight;

    public Gate(double gateX, double gateY, double gateWidth, double gateHeight) {
        this.gateX = gateX;
        this.gateY = gateY;
        this.gateWidth = gateWidth;
        this.gateHeight = gateHeight;
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(255, 0, 255, 255);
        drawTool.drawFilledRectangle(gateX, gateY, gateWidth, gateHeight);
    }

    @Override
    public double getHitboxX() { return gateX; }

    @Override
    public double getHitboxY() { return gateY; }

    @Override
    public double getHitboxWidth() { return gateWidth; }

    @Override
    public double getHitboxHeight() { return gateHeight; }
}
