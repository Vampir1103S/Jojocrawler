package my_project.model.map;

import KAGO_framework.view.DrawTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Baum extends Environment {

    private double x;
    private double y;

    private BufferedImage image;

    // Hitbox-Faktoren (nur Stamm ist solid)
    private final double hitboxXOffsetFactor = 0.35;
    private final double hitboxYOffsetFactor = 0.55;
    private final double hitboxWidthFactor   = 0.30;
    private final double hitboxHeightFactor  = 0.40;

    public Baum(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(new File("src/main/resources/graphic/Baum.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        if (image != null) {
            drawTool.drawImage(image, x, y);
            drawTool.drawRectangle(getHitboxX(), getHitboxY(), getHitboxWidth(), getHitboxHeight());

            // Debug-Hitbox (optional)
            // drawTool.drawRectangle(getHitboxX(), getHitboxY(), getHitboxWidth(), getHitboxHeight());
        }
    }

    @Override
    public void update(double dt) {
        // nichts
    }

    // ✅ Bildgröße direkt aus BufferedImage
    public int getImageWidth() {
        return (image != null) ? image.getWidth() : 0;
    }

    public int getImageHeight() {
        return (image != null) ? image.getHeight() : 0;
    }

    // ✅ Hitbox (aus Bildgröße berechnet)
    public double getHitboxX() {
        return x + getImageWidth() * hitboxXOffsetFactor;
    }

    public double getHitboxY() {
        return y + getImageHeight() * hitboxYOffsetFactor;
    }

    public double getHitboxWidth() {
        return getImageWidth() * hitboxWidthFactor;
    }

    public double getHitboxHeight() {
        return getImageHeight() * hitboxHeightFactor;
    }

    // Falls du Position brauchst
    public double getX() { return x; }
    public double getY() { return y; }
}
