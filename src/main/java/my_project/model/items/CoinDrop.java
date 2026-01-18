package my_project.model.items;

import KAGO_framework.view.DrawTool;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CoinDrop {

    public static final double SIZE = 22;

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

    // ✅ damit dein Controller NICHT mehr getX/getY Fehler hat
    public double getX() { return x; }
    public double getY() { return y; }

    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(x, y, SIZE, SIZE);
    }

    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(255, 215, 0));
        drawTool.drawFilledCircle(x + SIZE / 2.0, y + SIZE / 2.0, SIZE / 2.0);
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawCircle(x + SIZE / 2.0, y + SIZE / 2.0, SIZE / 2.0);

        drawTool.formatText("Arial", 0, 12);
        drawTool.drawText((int) x + 4, (int) y + 14, "" + value);
    }
}
