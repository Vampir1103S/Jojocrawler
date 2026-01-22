package my_project.view;

import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.control.Controller;

import java.awt.event.MouseEvent;

public class Deathscreen extends InteractiveGraphicalObject {

    private boolean deathscreen = true;

    private double alphaAnimation = 255;
    private double xPos = Config.WINDOW_WIDTH / 2.0;
    private double yPos = Config.WINDOW_HEIGHT / 2.0;

    public Deathscreen() { }

    @Override
    public void draw(DrawTool drawTool) {
        drawDeathScreen(drawTool);
    }

    private void drawDeathScreen(DrawTool drawTool) {
        int a = clampAlpha(alphaAnimation);

        drawTool.setCurrentColor(0, 0, 0, a);
        drawTool.drawFilledRectangle(0, 0, 1920, 1080);

        drawTool.setCurrentColor(145, 0, 1, 255);
        drawTool.formatText("Algerian", 2, 200);
        drawTool.drawText(xPos - 300, yPos, "DEATH");

        drawTool.formatText("Algerian", 4, 50);
        drawTool.setCurrentColor(255, 255, 255, 255);
        drawTool.drawText(xPos - 240, yPos + 450, "Close to Restart");
    }

    @Override
    public void update(double dt) {
        if (!deathscreen) {
            startDeathScreenAn(dt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            deathscreen = false;
        }
    }

    private void startDeathScreenAn(double dt) {
        yPos -= 850 * dt;
        alphaAnimation -= 220 * dt;

        if (yPos < -400) {

            Controller.switchScene(1);


            alphaAnimation = 255;
            yPos = Config.WINDOW_HEIGHT / 2.0;
            deathscreen = true;
        }
    }

    /**
     * Begrenzt einen Alpha-Wert auf den gültigen Bereich.
     * <p>
     * Der übergebene Wert wird auf den Bereich von 0 bis 255
     * beschränkt, wie er für Transparenzwerte in der
     * Farbdarstellung verwendet wird.
     *
     * @param a der zu begrenzende Alpha-Wert
     * @return der begrenzte Alpha-Wert im Bereich von 0 bis 255
     */

    private static int clampAlpha(double a) {
        if (a < 0) return 0;
        if (a > 255) return 255;
        return (int) a;
    }
}
