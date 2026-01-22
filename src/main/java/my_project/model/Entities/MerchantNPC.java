package my_project.model.Entities;

import KAGO_framework.view.DrawTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * Repräsentiert einen Händler-NPC im Spiel.
 * <p>
 * Der {@code MerchantNPC} ist eine nicht-angreifende Spielfigur,
 * die als Interaktionspunkt für den Shop dient.
 * Er besitzt keine eigene Bewegungs- oder Kampflogik und wird
 * statisch an einer festen Position dargestellt.
 *
 * Die Klasse erbt von {@link Entity} und stellt zusätzlich
 * eine Hitbox für Interaktionen (z.B. Öffnen des Shops)
 * sowie eine grafische Darstellung über ein Bild bereit.
 */

public class MerchantNPC extends Entity {

    private BufferedImage image;

    public MerchantNPC(double x, double y) {
        super(x, y, 10, 0, 0, 0, "Merchant", 190, 100);
    }

    @Override
    public void draw(DrawTool drawTool) {

        try {
            image = ImageIO.read(new File("src/main/resources/graphic/Shop-fr.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        drawTool.drawTransformedImage(image,1500,500,0,3);
        drawTool.setCurrentColor(Color.BLACK);

    }

    @Override
    public void update(double dt) { }

    /**
     * Gibt die Körper-Hitbox der Entity zurück.
     * <p>
     * Die Hitbox entspricht der aktuellen Position und Größe
     * der Entity und wird für Kollisionen, Trefferabfragen
     * und Reichweitenprüfungen verwendet.
     *
     * @return ein {@link Rectangle2D}, das die Hitbox der Entity beschreibt
     */

    public Rectangle2D getHitBox() {
        return new Rectangle2D.Double(xpos, ypos, width, height);
    }
}
