package my_project.control;

import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.*;
import my_project.model.map.Baum;
import my_project.model.map.Bürgersteig;
import my_project.view.Deathscreen;
import my_project.view.UI;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;

    private UI ui;
    private Player player;
    private Deathscreen deathscreen;
    private Collisions collisions;

    private Bürgersteig[][] bürgersteig;
    private Baum[][] baum;

    private int hoehe = 10;
    private int breite = 10;
    private int bhoehe = 2;
    private int bbreite = 10;

    // Enemies
    private Enemy dieb;
    private StoryTeller storytomole;

    // ✅ Controller verwaltet Tasten-Zustand (wichtig für Sliding)
    private boolean wDown, aDown, sDown, dDown;

    // ✅ gleiche Geschwindigkeit wie vorher im Player (dt * 250)
    private final double moveSpeed = 250;

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        player = new Player();
        dieb = new Dieb();

        storytomole = new StoryTeller(500, 500, 10, 5, 10, 100, "Tomole", 30, 20);
        storytomole.addDialogLine("Hallo!");
        storytomole.addDialogLine("Ich bin Tomole.");
        storytomole.addDialogLine("Drücke E für den nächsten Satz.");

        collisions = new Collisions();
        Enemy.setController(this);

        bürgersteig = new Bürgersteig[breite][hoehe];
        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < hoehe; y++) {
                bürgersteig[x][y] = new Bürgersteig(100 + x * 30, 100 + y * 30);
            }
        }

        baum = new Baum[bbreite][bhoehe];
        for (int x = 0; x < bbreite; x++) {
            for (int y = 0; y < bhoehe; y++) {
                baum[x][y] = new Baum(100 + x * 70, 100 + y * 200);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void draw(DrawTool drawTool) {
        switch (scene) {
            case 0:
                ui.draw(drawTool);
                break;

            case 1:
                for (int x = 0; x < bürgersteig.length; x++) {
                    for (int y = 0; y < bürgersteig[x].length; y++) {
                        bürgersteig[x][y].draw(drawTool);
                    }
                }

                for (int x = 0; x < baum.length; x++) {
                    for (int y = 0; y < baum[x].length; y++) {
                        baum[x][y].draw(drawTool);
                    }
                }

                if (player.getHP() > 0) player.draw(drawTool);
                dieb.draw(drawTool);
                storytomole.draw(drawTool);
                break;

            case 3:
                deathscreen.draw(drawTool);
                break;
        }
    }

    @Override
    public void update(double dt) {
        switch (scene) {
            case 0:
                ui.update(dt);
                break;

            case 1:
                // ✅ Player Combat/Attack Timer updaten (OHNE Bewegung)
                player.updateCombat(dt);

                // Enemy bewegt sich normal
                dieb.update(dt);

                // ✅ gewünschte Bewegung aus Tasten berechnen
                double dx = 0;
                double dy = 0;

                if (wDown) { dy -= moveSpeed * dt; player.setFacing(0, -1); }
                if (sDown) { dy += moveSpeed * dt; player.setFacing(0,  1); }
                if (aDown) { dx -= moveSpeed * dt; player.setFacing(-1, 0); }
                if (dDown) { dx += moveSpeed * dt; player.setFacing( 1, 0); }

                // ✅ SLIDING: erst X bewegen und prüfen
                if (dx != 0) {
                    double newX = player.getXpos() + dx;
                    double oldX = player.getXpos();

                    player.setXpos(newX);
                    if (playerHitsAnyTree()) {
                        player.setXpos(oldX); // nur X blockieren
                    }
                }

                // ✅ SLIDING: dann Y bewegen und prüfen
                if (dy != 0) {
                    double newY = player.getYpos() + dy;
                    double oldY = player.getYpos();

                    player.setYpos(newY);
                    if (playerHitsAnyTree()) {
                        player.setYpos(oldY); // nur Y blockieren
                    }
                }

                // ✅ Angriff: Hitbox trifft Enemy -> Schaden + Knockback
                if (player.canDealHitNow()) {
                    var hitbox = player.getAttackHitbox();

                    if (hitbox.intersects(dieb.getXpos(), dieb.getYpos(), dieb.getWidth(), dieb.getHeight())) {

                        dieb.setHP(dieb.getHP() - player.getAttackDamage());

                        double kx = dieb.getCenterX() - player.getCenterX();
                        double ky = dieb.getCenterY() - player.getCenterY();
                        double dist = Math.sqrt(kx * kx + ky * ky);

                        if (dist != 0) {
                            kx /= dist;
                            ky /= dist;
                        }

                        double knockback = player.getKnockbackStrength();
                        dieb.applyKnockback(kx * knockback, ky * knockback);

                        player.markHitDone();
                    }
                }

                // Dialog (optional)
                if (collisions.rectangleCollisions(player, storytomole) && storytomole.getETrue()) {
                    storytomole.speak();
                }
                break;

            case 3:
                deathscreen.update(dt);
                break;
        }
    }

    // ✅ helper: prüft Player gegen alle Bäume (2D Array)
    private boolean playerHitsAnyTree() {
        for (int x = 0; x < baum.length; x++) {
            for (int y = 0; y < baum[x].length; y++) {
                if (collisions.rectangleCollisions(player, baum[x][y])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void switchScene(int newSzene) {
        scene = newSzene;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (scene == 0) ui.mouseClicked(e);
    }

    @Override
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_X) player.startAttack();

        if (key == KeyEvent.VK_E) {
            if (collisions.rectangleCollisions(player, storytomole)) {
                storytomole.speak();
            }
        }

        if (key == KeyEvent.VK_W) wDown = true;
        if (key == KeyEvent.VK_A) aDown = true;
        if (key == KeyEvent.VK_S) sDown = true;
        if (key == KeyEvent.VK_D) dDown = true;
    }

    @Override
    public void keyReleased(int key) {
        if (key == KeyEvent.VK_W) wDown = false;
        if (key == KeyEvent.VK_A) aDown = false;
        if (key == KeyEvent.VK_S) sDown = false;
        if (key == KeyEvent.VK_D) dDown = false;
    }

    public double followPlayerX(Entity e) {
        if (player == null || e == null) return 0;
        return player.getXpos() - e.getXpos();
    }

    public double followPlayerY(Entity e) {
        if (player == null || e == null) return 0;
        return player.getYpos() - e.getYpos();
    }
}
