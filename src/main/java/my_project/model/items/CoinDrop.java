package my_project.model.items;

import KAGO_framework.view.DrawTool;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CoinDrop {

    public static final int SIZE = 18;

    private double x;
    private double y;
    private int value;

    public CoinDrop(double x, double y, int value) {
        this.x = x;
        this.y = y;
        this.value = Math.max(1, value);
    }

    public int getValue() {
        return value;
    }

    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(x, y, SIZE, SIZE);
    }

    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(255, 215, 0));
        drawTool.drawFilledRectangle(x, y, SIZE, SIZE);
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawRectangle(x, y, SIZE, SIZE);

        drawTool.formatText("Arial", 0, 12);
        drawTool.drawText((int)x + 3, (int)y + 13, "" + value);
    }
}
