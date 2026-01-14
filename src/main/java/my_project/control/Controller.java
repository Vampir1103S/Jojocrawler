package my_project.control;

import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.model.Entities.*;
import my_project.model.map.*;
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
    private Grünfläche[][] grünfläche;
    private BetonZaun[][] betonZaun;

    private int hoehe = 10;
    private int breite = 10;
    private int bhoehe = 2;
    private int bbreite = 10;
    private int zhoehe = 2;
    private int zbreite = 4;

    private Enemy dieb;
    private StoryTeller storytomole;

    // Tasten-Zustand für Sliding-Movement
    private boolean wDown, aDown, sDown, dDown;

    // Bewegungsgeschwindigkeit
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

        betonZaun = new BetonZaun[zbreite][zhoehe];
        for (int x = 0; x < zbreite; x++) {
            for (int y = 0; y < zhoehe; y++) {
                betonZaun[x][y] = new BetonZaun(130 + x * 70, 240 + y * 200);
            }
        }

        baum = new Baum[bbreite][bhoehe];
        grünfläche = new Grünfläche[bbreite][bhoehe];
        for (int x = 0; x < bbreite; x++) {
            for (int y = 0; y < bhoehe; y++) {
                baum[x][y] = new Baum(100 + x * 70, 100 + y * 200);
                grünfläche[x][y] = new Grünfläche(100 + x * 70, 145 + y * 200);
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
                        grünfläche[x][y].draw(drawTool);
                    }
                }
                if (player.getHP() > 0) player.draw(drawTool);
                dieb.draw(drawTool);
                storytomole.draw(drawTool);

                for (int x = 0; x < betonZaun.length; x++) {
                    for (int y = 0; y < betonZaun[x].length; y++) {
                        betonZaun[x][y].draw(drawTool);
                    }
                }
                for (int x = 0; x < baum.length; x++) {
                    for (int y = 0; y < baum[x].length; y++) {
                        baum[x][y].draw(drawTool);
                    }
                }
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
                // Combat/Attack Timer ohne Movement
                player.updateCombat(dt);
                player.update(dt);

                // Enemy
                dieb.update(dt);

                // Bewegung aus Eingaben
                double dx = 0, dy = 0;
                if (wDown) { dy -= moveSpeed * dt; player.setFacing(0, -1); player.setIsDownWTrue(); } else player.setIsDownWFalse();
                if (sDown) { dy += moveSpeed * dt; player.setFacing(0,  1); player.setIsDownSTrue(); }else player.setIsDownSFalse();
                if (aDown) { dx -= moveSpeed * dt; player.setFacing(-1, 0); player.setIsDownATrue(); }else player.setIsDownAFalse();
                if (dDown) { dx += moveSpeed * dt; player.setFacing( 1, 0); player.setIsDownDTrue(); }else player.setIsDownDFalse();

                // ✅ X-Achse bewegen + Screen-Grenze + Sliding
                if (dx != 0) {
                    double oldX = player.getXpos();
                    double newX = oldX + dx;

                    newX = Math.max(0, Math.min(newX, Config.WINDOW_WIDTH - player.getWidth()));

                    player.setXpos(newX);
                    if (playerHitsAnyTree()) {
                        player.setXpos(oldX);
                    }
                }

                // ✅ Y-Achse bewegen + Screen-Grenze + Sliding
                if (dy != 0) {
                    double oldY = player.getYpos();
                    double newY = oldY + dy;

                    newY = Math.max(0, Math.min(newY, Config.WINDOW_HEIGHT - player.getHeight()));

                    player.setYpos(newY);
                    if (playerHitsAnyTree()) {
                        player.setYpos(oldY);
                    }
                }

                // Angriff: Hitbox trifft Enemy -> Schaden + Knockback
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

    private boolean playerHitsAnyTree() {
        for (int x = 0; x < baum.length; x++) {
            for (int y = 0; y < baum[x].length; y++) {
                if (collisions.rectangleCollisions(player, baum[x][y])) {
                    return true;
                }
            }
        }

        for (int x = 0; x < betonZaun.length; x++) {
            for (int y = 0; y < betonZaun[x].length; y++) {
                if (collisions.rectangleCollisions(player, betonZaun[x][y])) {
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

        if (key == KeyEvent.VK_Q) {
            System.out.println("Q gedrückt");
            for (int x = 0; x < baum.length; x++) {
                for (int y = 0; y < baum[x].length; y++) {
                    System.out.println("Q gedrückt und überprüft");
                    System.out.println(baum[x][y].getHitboxX());
                    if (collisions.rectangleBreak(player, baum[x][y])) {
                        System.out.println("Q gedrückt und break");
                        baum[x][y].setNachRechts(true);
                        System.out.println(baum[x][y].getNachRechts());
                    }
                }
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
