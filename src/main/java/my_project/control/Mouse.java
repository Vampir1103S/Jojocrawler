package my_project.control;

import KAGO_framework.model.InteractiveGraphicalObject;
import com.sun.javafx.geom.Vec2d;

import java.awt.event.MouseEvent;
/**
 * Verwaltet Mausposition und Mausbutton-Zustände im Spiel.
 * <p>
 * Die Klasse dient als zentrale Schnittstelle für Mauseingaben
 * und speichert:
 * <ul>
 *   <li>die aktuelle Mausposition</li>
 *   <li>den Zustand der Mausbuttons (gedrückt / nicht gedrückt)</li>
 * </ul>
 *
 * Die Informationen können von anderen Klassen abgefragt werden,
 * um z.B. Klicks, Hover-Effekte oder UI-Interaktionen umzusetzen.
 */
public class Mouse extends InteractiveGraphicalObject {
    private static Vec2d position;
    private static boolean[] button;
    public Mouse() {
        position = new Vec2d();
        button = new boolean[6];
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        position.x = e.getX();
        position.y = e.getY();
    }
    @Override
    public void mousePressed(MouseEvent e) {
        button[e.getButton()] = true;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        button[e.getButton()] = false;
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        position.x = e.getX();
        position.y = e.getY();
    }
    public Vec2d getPosition() {
        return position;
    }
    public double getX() {
        return position.x ;
    }

    public double getY() {
        return position.y ;
    }
    public static boolean isDown(int b) {
        return button[b];
    }
}