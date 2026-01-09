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
    private boolean collisionW;
    private boolean collisionA;
    private boolean collisionS;
    private boolean collisionD;
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
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 2; y++) {
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
                player.update(dt);
                dieb.update(dt);

                //  Angriff: Hitbox trifft Enemy -> Schaden + Knockback
                if (player.canDealHitNow()) {
                    var hitbox = player.getAttackHitbox();

                    if (hitbox.intersects(dieb.getXpos(), dieb.getYpos(), dieb.getWidth(), dieb.getHeight())) {

                        // Schaden
                        dieb.setHP(dieb.getHP() - player.getAttackDamage());

                        // Knockback Richtung: weg vom Spieler
                        double dx = dieb.getCenterX() - player.getCenterX();
                        double dy = dieb.getCenterY() - player.getCenterY();
                        double dist = Math.sqrt(dx * dx + dy * dy);

                        if (dist != 0) {
                            dx /= dist;
                            dy /= dist;
                        }

                        double knockback = player.getKnockbackStrength();
                        dieb.applyKnockback(dx * knockback, dy * knockback);


                        // pro Angriff nur 1 Hit
                        player.markHitDone();

                        System.out.println("HIT! Dieb HP: " + dieb.getHP());

                        /*if (collisions.rectangleCollisions(player, baum)){
                            if (player.getFacingX() == 0 && player.getFacingY() == -1) {
                                collisionW = false;
                            }else if (player.getFacingX() == -1 && player.getFacingY() == 0) {
                                collisionA = false;
                            }else if (player.getFacingX() == 0 && player.getFacingY() == 1) {
                                collisionW = false;
                            }else if (player.getFacingX() == 1 && player.getFacingY() == 0) {
                                collisionD = false;
                            }
                        }*/
                    }
                }

                // deine bestehenden Collision Checks
                if (collisions.rectangleCollisions(player, dieb)) {
                    //player.setHP(0);
                }

                if (collisions.rectangleCollisions(player, storytomole) && storytomole.getETrue()) {
                    storytomole.speak();
                }
                break;

            case 3:
                deathscreen.update(dt);
                break;
        }
    }

    public static void switchScene(int newSzene) {
        scene = newSzene;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (scene) {
            case 0:
                ui.mouseClicked(e);
                break;
        }
    }

    @Override
    public void keyPressed(int key) {

        //  X = Angriff
        if (key == KeyEvent.VK_X) {
            player.startAttack();
        }

        // Dialog
        if (key == KeyEvent.VK_E) {
            if (collisions.rectangleCollisions(player, storytomole)) {
                storytomole.speak();
            }
        }
        if(collisionW) {
            if (key == KeyEvent.VK_W) player.setIsDownWTrue();
        }
        if(collisionA) {
            if (key == KeyEvent.VK_A) player.setIsDownATrue();
        }
        if(collisionS) {
            if (key == KeyEvent.VK_S) player.setIsDownSTrue();
        }
        if(collisionD) {
            if (key == KeyEvent.VK_D) player.setIsDownDTrue();
        }
    }

    @Override
    public void keyReleased(int key) {
        if (key == KeyEvent.VK_W) player.setIsDownWFalse();
        if (key == KeyEvent.VK_A) player.setIsDownAFalse();
        if (key == KeyEvent.VK_S) player.setIsDownSFalse();
        if (key == KeyEvent.VK_D) player.setIsDownDFalse();
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
