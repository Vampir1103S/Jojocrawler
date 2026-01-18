package my_project.control;

import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.SwingUI;
import my_project.model.Entities.*;
import my_project.model.items.Inventory;
import my_project.model.items.Item;
import my_project.model.items.Weapons;
import my_project.model.items.Consumables.Potions;
import my_project.model.items.Consumables.HealingPotion;
import my_project.model.map.*;
import my_project.view.Deathscreen;
import my_project.view.UI;

import javax.swing.*; // ✅ FEHLTE (SwingUtilities, JFrame, etc.)

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;

    private UI ui;
    private Player player;

    private Deathscreen deathscreen;
    private Collisions collisions;
    private HealingPotion healingPotion;

    private Bürgersteig[][] bürgersteig;
    private Baum[][] baum;
    private Grünfläche[][] grünfläche;
    private BetonZaun[][] betonZaun;

    //Level

    private Gate gate;
    private LevelOne level1;

    private int hoehe = 10;
    private int breite = 10;
    private int bhoehe = 2;
    private int bbreite = 10;
    private int zhoehe = 2;
    private int zbreite = 4;

    private Enemy dieb;
    private Enemy enemies[][];
    private StoryTeller storytomole;

    // Movement (sliding)
    private boolean wDown, aDown, sDown, dDown;
    private final double moveSpeed = 250;

    // ===== INVENTAR / HOTBAR =====
    private final Inventory inventory = new Inventory();
    private boolean inventoryOpen = false;

    // Mausposition für "Hover + E"
    private int mouseX = 0;
    private int mouseY = 0;

    // Inventar UI Bereiche (2 Felder / Buttons)
    private Rectangle2D weaponField = new Rectangle2D.Double(80, 750, 320, 70);
    private Rectangle2D potionField = new Rectangle2D.Double(80, 840, 320, 70);

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        player = new Player();





        storytomole = new StoryTeller(500, 500, 10, 5, 10, 100, "Tomole", 30, 20);
        storytomole.addDialogLine("Hallo!");
        storytomole.addDialogLine("Ich bin Tomole.");
        storytomole.addDialogLine("Drücke E für den nächsten Satz.");
        collisions = new Collisions();

        //Level
        level1 = new LevelOne();
        gate = new Gate(800, 200, 200, 200);


        //Enemy
        enemies= new Enemy[4][4];
        SpawnEnemies(1);
        dieb = enemies[0][0];

        Enemy.setController(this);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SwingUI");

            SwingUI ui = new SwingUI(storytomole);

            frame.setContentPane(ui.outputField);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(600, 600));
            frame.pack();
            frame.setVisible(true);
        });

        healingPotion = new HealingPotion();

        // Beispiel: Potion liegt im Inventar (damit Swing was anzeigen kann)
        inventory.addItem(healingPotion);

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

    private Enemy createEnemyByType(int enemyType,int x ,int y) {
        if (enemyType == 1) {
            // Dieb-Constructor wie bei dir:
            return new Dieb(x, y, 20, 1, 5, 20, "Dieb", 30, 30);
        }

        // if (enemyType == 2) return new Ninja(); oder andere Enemey Typen

        return null;
    }

    private void SpawnEnemies(int enemyType) {

        int rows = enemies.length;
        int cols = enemies[0].length;

        double startX = 400;
        double startY = 200;

        double swarmX = 120;
        double swarmY = 120;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                double x = startX + j * swarmX;
                double y = startY + i * swarmY;

                enemies[i][j] = createEnemyByType(enemyType, (int) x,(int) y);
            }
        }
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
                gate.draw(drawTool);
                // ===== Hotbar immer anzeigen =====
                drawHotbar(drawTool);

                // ===== Inventar Overlay =====
                if (inventoryOpen) drawInventoryOverlay(drawTool);

                break;

            case 3:
                deathscreen.draw(drawTool);

            case 4:
                level1.draw(drawTool);

                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        if (enemies[i][j].getHP() > 0) {
                            if (enemies[i][j] != null) enemies[i][j].draw(drawTool);
                        }
                    }
                }
                player.draw(drawTool);
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
                // Player/Combat
                player.update(dt);



            case 2:

                // ===== Movement nur wenn Inventar NICHT offen =====
                if (!inventoryOpen) {
                    double dx = 0, dy = 0;
                    if (wDown) { dy -= moveSpeed * dt; player.setFacing(0, -1); player.setIsDownWTrue(); } else player.setIsDownWFalse();
                    if (sDown) { dy += moveSpeed * dt; player.setFacing(0,  1); player.setIsDownSTrue(); } else player.setIsDownSFalse();
                    if (aDown) { dx -= moveSpeed * dt; player.setFacing(-1, 0); player.setIsDownATrue(); } else player.setIsDownAFalse();
                    if (dDown) { dx += moveSpeed * dt; player.setFacing( 1, 0); player.setIsDownDTrue(); } else player.setIsDownDFalse();

                    // X-Achse + Screen-Grenze + Sliding
                    if (dx != 0) {
                        double oldX = player.getXpos();
                        double newX = oldX + dx;
                        newX = Math.max(0, Math.min(newX, Config.WINDOW_WIDTH - player.getWidth()));
                        player.setXpos(newX);
                        if (playerHitsAnyTree()) player.setXpos(oldX);
                    }

                    // Y-Achse + Screen-Grenze + Sliding
                    if (dy != 0) {
                        double oldY = player.getYpos();
                        double newY = oldY + dy;
                        newY = Math.max(0, Math.min(newY, Config.WINDOW_HEIGHT - player.getHeight()));
                        player.setYpos(newY);
                        if (playerHitsAnyTree()) player.setYpos(oldY);
                    }
                }

                // ===== Potion Effekt (dein vorhandenes System) =====
                if (healingPotion.getHealing()) {
                    if (healingPotion.getAmount() > 0) {
                        player.setHP(player.getHP() + 20);
                        healingPotion.setHealing(false);
                        healingPotion.setAmount(healingPotion.getAmount() - 1);
                    } else {
                        healingPotion.setHealing(false);
                    }
                }


                // Dialog
                if (collisions.rectangleCollisions(player, storytomole) && storytomole.getETrue()) {
                    storytomole.speak();
                }

                if (collisions.rectangleCollisions(player, gate) ) {
                    switchScene(4);
                }
                break;

            case 3:
                deathscreen.update(dt);

            case 4:
                player.update(dt);

                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        if (enemies[i][j].getHP() > 0) {
                            if (enemies[i][j] != null) enemies[i][j].update(dt);
                        }
                    }
                }

                if (player.canDealHitNow()) {
                    Rectangle2D hitbox = player.getAttackHitbox();
                    boolean hitSomeone = false;

                    for (int i = 0; i < enemies.length; i++) {
                        for (int j = 0; j < enemies[0].length; j++) {

                            Enemy e = enemies[i][j];
                            if (e == null) continue;

                            if (hitbox.intersects(player.getXpos(), player.getYpos(), player.getWidth(), player.getHeight())) {
                            }

                            if (hitbox.intersects(e.getXpos(), e.getYpos(), e.getWidth(), e.getHeight())) {

                                e.setHP(e.getHP() - player.getAttackDamage());

                                // Knockback Enemy weg vom Player
                                double kx = e.getCenterX() - player.getCenterX();
                                double ky = e.getCenterY() - player.getCenterY();
                                double dist = Math.sqrt(kx*kx + ky*ky);
                                if (dist != 0) { kx /= dist; ky /= dist; }

                                double kb = player.getKnockbackStrength();
                                e.applyKnockback(kx * kb, ky * kb);

                                hitSomeone = true;
                            }
                        }
                    }

                    if (hitSomeone) player.markHitDone();
                }


                // ===== Movement nur wenn Inventar NICHT offen =====
                if (!inventoryOpen) {
                    double dx = 0, dy = 0;

                    if (wDown) { dy -= moveSpeed * dt; player.setFacing(0, -1); player.setIsDownWTrue(); } else player.setIsDownWFalse();
                    if (sDown) { dy += moveSpeed * dt; player.setFacing(0,  1); player.setIsDownSTrue(); } else player.setIsDownSFalse();
                    if (aDown) { dx -= moveSpeed * dt; player.setFacing(-1, 0); player.setIsDownATrue(); } else player.setIsDownAFalse();
                    if (dDown) { dx += moveSpeed * dt; player.setFacing( 1, 0); player.setIsDownDTrue(); } else player.setIsDownDFalse();

                    // X-Achse + Screen-Grenze + Sliding
                    if (dx != 0) {
                        double oldX = player.getXpos();
                        double newX = oldX + dx;
                        newX = Math.max(0, Math.min(newX, Config.WINDOW_WIDTH - player.getWidth()));
                        player.setXpos(newX);
                        if (playerHitsAnyTree()) player.setXpos(oldX);
                    }

                    // Y-Achse + Screen-Grenze + Sliding
                    if (dy != 0) {
                        double oldY = player.getYpos();
                        double newY = oldY + dy;
                        newY = Math.max(0, Math.min(newY, Config.WINDOW_HEIGHT - player.getHeight()));
                        player.setYpos(newY);
                        if (playerHitsAnyTree()) player.setYpos(oldY);
                    }
                }

                // ===== Potion Effekt =====
                if (healingPotion.getHealing()) {
                    if (healingPotion.getAmount() > 0) {
                        player.setHP(player.getHP() + 20);
                        healingPotion.setHealing(false);
                        healingPotion.setAmount(healingPotion.getAmount() - 1);
                    } else {
                        healingPotion.setHealing(false);
                    }
                }


                if (player.canDealHitNow()) {
                    var hitbox = player.getAttackHitbox();
                    if (hitbox.intersects(dieb.getXpos(), dieb.getYpos(), dieb.getWidth(), dieb.getHeight())) {
                        dieb.setHP(dieb.getHP() - player.getAttackDamage());

                        double kx = dieb.getCenterX() - player.getCenterX();
                        double ky = dieb.getCenterY() - player.getCenterY();
                        double dist = Math.sqrt(kx * kx + ky * ky);
                        if (dist != 0) { kx /= dist; ky /= dist; }

                        double knockback = player.getKnockbackStrength();
                        dieb.applyKnockback(kx * knockback, ky * knockback);

                        player.markHitDone();
                    }
                }


                break;


        }
    }

    // ===== Hotbar zeichnen =====
    private void drawHotbar(DrawTool drawTool) {
        int y = 980;
        int slotW = 220;
        int slotH = 70;

        // Slot 1 (Waffe)
        drawTool.setCurrentColor(new Color(0, 0, 0, 160));
        drawTool.drawFilledRectangle(40, y, slotW, slotH);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(40, y, slotW, slotH);

        // Slot 2 (Potion)
        drawTool.setCurrentColor(new Color(0, 0, 0, 160));
        drawTool.drawFilledRectangle(280, y, slotW, slotH);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(280, y, slotW, slotH);

        // Texte
        drawTool.formatText("Arial", 0, 18);
        drawTool.drawText(55, y + 25, "Slot 1 (Waffe) [1]");
        drawTool.drawText(295, y + 25, "Slot 2 (Potion) [2]");

        Weapons w = inventory.getSelectedWeapon();
        Potions p = inventory.getSelectedPotion();

        drawTool.drawText(55, y + 50, ">> " + inventory.getDisplayName(w));
        drawTool.drawText(295, y + 50, ">> " + inventory.getDisplayName(p));

        // Markierung aktiver Slot
        if (inventory.getActiveSlot() == 1) {
            drawTool.setCurrentColor(Color.YELLOW);
            drawTool.drawRectangle(40, y, slotW, slotH);
        } else {
            drawTool.setCurrentColor(Color.YELLOW);
            drawTool.drawRectangle(280, y, slotW, slotH);
        }
    }

    // ===== Inventar Overlay zeichnen =====
    private void drawInventoryOverlay(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(0, 0, 0, 180));
        drawTool.drawFilledRectangle(40, 40, 600, 920);

        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(40, 40, 600, 920);

        drawTool.formatText("Arial", 1, 26);
        drawTool.drawText(70, 80, "INVENTAR (I zum Schließen)");

        drawTool.formatText("Arial", 0, 18);
        drawTool.drawText(70, 120, "Aufzählung Items:");
        int y = 150;

        for (Item item : inventory.getItems()) {
            drawTool.drawText(80, y, "- " + inventory.getDisplayName(item));
            y += 22;
            if (y > 700) break;
        }

        // Felder für "E zum Öffnen"
        boolean overWeapon = weaponField.contains(mouseX, mouseY);
        boolean overPotion = potionField.contains(mouseX, mouseY);

        drawTool.setCurrentColor(overWeapon ? new Color(255, 255, 255, 90) : new Color(255, 255, 255, 40));
        drawTool.drawFilledRectangle(weaponField.getX(), weaponField.getY(), weaponField.getWidth(), weaponField.getHeight());
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(weaponField.getX(), weaponField.getY(), weaponField.getWidth(), weaponField.getHeight());
        drawTool.drawText((int)weaponField.getX() + 15, (int)weaponField.getY() + 40, "Waffe zu Slot 1 hinzufügen (Hover + E)");

        drawTool.setCurrentColor(overPotion ? new Color(255, 255, 255, 90) : new Color(255, 255, 255, 40));
        drawTool.drawFilledRectangle(potionField.getX(), potionField.getY(), potionField.getWidth(), potionField.getHeight());
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(potionField.getX(), potionField.getY(), potionField.getWidth(), potionField.getHeight());
        drawTool.drawText((int)potionField.getX() + 15, (int)potionField.getY() + 40, "Potion zu Slot 2 hinzufügen (Hover + E)");

        drawTool.drawText(70, 930, "1/2 = Slot in Hand | C = scroll | X = benutzen");
    }

    // ===== Hover + E öffnet Swing Auswahl =====
    private void tryOpenSwingForHoveredField() {
        if (!inventoryOpen) return;

        boolean overWeapon = weaponField.contains(mouseX, mouseY);
        boolean overPotion = potionField.contains(mouseX, mouseY);

        if (overWeapon) {
            List<Weapons> options = collectWeaponsFromInventory();
            Weapons chosen = SwingUI.chooseWeapon(options);
            if (chosen != null) inventory.addWeaponToSlot(chosen);
        } else if (overPotion) {
            List<Potions> options = collectPotionsFromInventory();
            Potions chosen = SwingUI.choosePotion(options);
            if (chosen != null) inventory.addPotionToSlot(chosen);
        }
    }

    private List<Weapons> collectWeaponsFromInventory() {
        List<Weapons> list = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            if (item instanceof Weapons) list.add((Weapons) item);
        }
        return list;
    }

    private List<Potions> collectPotionsFromInventory() {
        List<Potions> list = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            if (item instanceof Potions) list.add((Potions) item);
        }
        return list;
    }

    // ===== Item benutzen (X) =====
    private void useActiveSlotItem() {
        if (inventory.getActiveSlot() == 1) {
            player.startAttack();
        } else {
            Potions p = inventory.getSelectedPotion();
            if (p == null) return;

            if (p instanceof HealingPotion) {
                healingPotion.setHealing(true);
            }
        }
    }

    private boolean playerHitsAnyTree() {
        /*for (int x = 0; x < baum.length; x++) {
            for (int y = 0; y < baum[x].length; y++) {
                if (collisions.rectangleCollisions(player, baum[x][y])) return true;
            }
        }
        for (int x = 0; x < betonZaun.length; x++) {
            for (int y = 0; y < betonZaun[x].length; y++) {
                if (collisions.rectangleCollisions(player, betonZaun[x][y])) return true;
            }
        }*/
        return false;
    }

    public static void switchScene(int newSzene) {
        scene = newSzene;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (scene == 0) ui.mouseClicked(e);
        healingPotion.mouseClicked(e);

        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_I) {
            inventoryOpen = !inventoryOpen;
            return;
        }

        if (key == KeyEvent.VK_1) inventory.setActiveSlot(1);
        if (key == KeyEvent.VK_2) inventory.setActiveSlot(2);

        if (key == KeyEvent.VK_C) inventory.scrollActiveSlot();

        if (key == KeyEvent.VK_X) useActiveSlotItem();

        if (key == KeyEvent.VK_E) {
            if (inventoryOpen) {
                tryOpenSwingForHoveredField();
            } else {
                if (collisions.rectangleCollisions(player, storytomole)) {
                    storytomole.speak();
                }
            }
        }

        if (!inventoryOpen) {
            if (key == KeyEvent.VK_W) wDown = true;
            if (key == KeyEvent.VK_A) aDown = true;
            if (key == KeyEvent.VK_S) sDown = true;
            if (key == KeyEvent.VK_D) dDown = true;
        }
    }

    @Override
    public void keyReleased(int key) {
        if (key == KeyEvent.VK_W) wDown = false;
        if (key == KeyEvent.VK_A) aDown = false;
        if (key == KeyEvent.VK_S) sDown = false;
        if (key == KeyEvent.VK_D) dDown = false;
    }
}
