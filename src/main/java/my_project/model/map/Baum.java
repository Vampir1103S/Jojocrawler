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

    private final double hitboxXOffsetFactorb = 1.35;
    private final double hitboxYOffsetFactorb = 1.55;
    private final double hitboxWidthFactorb   = 1.30;
    private final double hitboxHeightFactorb  = 1.40;



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
            drawTool.drawImage(image, this.x, y);
            drawTool.drawRectangle(getHitboxX(), getHitboxY(), getHitboxWidth(), getHitboxHeight());


        }
    }

    @Override
    public void update(double dt) {

    }




    public int getFullImageWidth() {
        return (image != null) ? image.getWidth() : 0;
    }

    public int getFullImageHeight() {
        return (image != null) ? image.getHeight() : 0;
    }


    public double getHitboxXb() {
        return x + getImageWidth();
    }

    public double getHitboxYb() {
        return y + getImageHeight();
    }

    public double getHitboxWidthb() {
        return getImageWidth();
    }

    public double getHitboxHeightb() {
        return getImageHeight();
    }


    public int getImageWidth() {
        return (image != null) ? image.getWidth() : 0;
    }

    public int getImageHeight() {
        return (image != null) ? image.getHeight() : 0;
    }


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

    public double getX() { return x; }
    public double getY() { return y; }
}
