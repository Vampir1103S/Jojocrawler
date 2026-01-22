package my_project.view;

import KAGO_framework.control.SoundController;
import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.control.Controller;

import java.awt.event.MouseEvent;

public class UI extends InteractiveGraphicalObject {

    private boolean startscreen = true;


    private double alpha = 255;
    private double alphaTimer = 0;


    private double alphaAnimation = 255;

    private double xPos = Config.WINDOW_WIDTH / 2.0;
    private double yPos = Config.WINDOW_HEIGHT / 2.0;

    public UI() {
        SoundController.playSound("Inspiration");
        SoundController.stopSound("Timeline");
        SoundController.stopSound("Evasion");
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawStartScreen(drawTool);
    }

    private void drawStartScreen(DrawTool drawTool) {

        int bgA = clampAlpha(alphaAnimation);

        drawTool.setCurrentColor(0, 0, 0, bgA);
        drawTool.drawFilledRectangle(0, 0, 1920, 1080);

        drawTool.setCurrentColor(255, 192, 203, 255);
        drawTool.formatText("Algerian", 2, 200);
        drawTool.drawText(xPos - 300, yPos - 150, "JOJO'S");

        drawTool.setCurrentColor(255, 255, 130, 255);
        drawTool.drawText(xPos - 450, yPos + 30, "CRAWLER");

        drawTool.formatText("Algerian", 4, 50);

        int textA = clampAlpha(alpha);
        drawTool.setCurrentColor(255, 255, 255, textA);
        drawTool.drawText(xPos - 200, yPos + 350, "Click to Start");
    }

    @Override
    public void update(double dt) {
        alphaTimer += dt;


        if (alphaTimer < 2) {
            alpha -= 95 * dt;
        }
        if (alphaTimer >= 2) {
            alpha += 90 * dt;
        }
        if (alphaTimer >= 4) {
            alphaTimer = 0;
            alpha = 255;
        }

        if (!startscreen) {
            startScreenAn(dt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            startscreen = false;
        }
    }

    private void startScreenAn(double dt) {
        yPos -= 850 * dt;
        alphaAnimation -= 220 * dt;


        if (yPos < -400) {
            Controller.switchScene(1);
            SoundController.stopSound("Inspiration");
            SoundController.playSound("Timeline");
            SoundController.stopSound("Evasion");
            alphaAnimation = 0;
        }
    }

    public boolean getStartscreen() {
        return startscreen;
    }

    /**
     * Begrenzt einen Alpha-Wert auf den gültigen Bereich für Transparenz.
     * <p>
     * Der Wert wird auf den Bereich von 0 bis 255 eingeschränkt,
     * wie er bei Farb- und Transparenzwerten verwendet wird.
     *
     * @param a der zu prüfende Alpha-Wert
     * @return ein ganzzahliger Alpha-Wert im Bereich von 0 bis 255
     */

    private static int clampAlpha(double a) {
        if (a < 0) return 0;
        if (a > 255) return 255;
        return (int) a;
    }
}
