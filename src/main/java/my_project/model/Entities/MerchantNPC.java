package my_project.model.Entities;

import KAGO_framework.view.DrawTool;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class MerchantNPC extends Entity {

    public MerchantNPC(double x, double y) {
        super(999, x, y, 0, 0, 0, "Merchant", 40, 60);
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(160, 80, 20));
        drawTool.drawFilledRectangle(xpos, ypos, width, height);
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawRectangle(xpos, ypos, width, height);

        drawTool.formatText("Arial", 0, 14);
        drawTool.drawText((int)xpos - 5, (int)ypos - 8, "SHOP");
    }

    @Override
    public void update(double dt) { }

    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(xpos, ypos, width, height);
    }
}
