package my_project.control;

import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.SwingUI;
import my_project.model.Entities.*;
import my_project.model.items.*;
import my_project.model.items.Consumables.HealingPotion;
import my_project.model.items.Consumables.Potions;
import my_project.model.map.*;
import my_project.view.Deathscreen;
import my_project.view.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;

    private UI ui;
    private Player player;

    private Deathscreen deathscreen;
    private Background background;
    private Collisions collisions;

    // Items / Inventory
    private Inventory inventory;
    private HealingPotion healingPotion;
    private Fists fists;
    private Sword sword;

    // Map
    private Bürgersteig[][] bürgersteig;
    private Baum[][] baum;
    private Grünfläche[][] grünfläche;
    private BetonZaun[][] betonZaun;

    // Level
    private Gate gate;
    private LevelOne level1;

    private int hoehe = 10;
    private int breite = 10;
    private int bhoehe = 2;
    private int bbreite = 10;
    private int zhoehe = 2;
    private int zbreite = 4;

    private Enemy dieb;
    private Enemy kind;
    private Enemy enemies[][];
    private Enemy secenemies[][];
    private StoryTeller storytomole;

    // Movement
    private boolean wDown, aDown, sDown, dDown;
    private final double moveSpeed = 250;

    // ===== INVENTAR / HOTBAR =====
    private boolean inventoryOpen = false;
    private int mouseX = 0;
    private int mouseY = 0;

    private Rectangle2D weaponField = new Rectangle2D.Double(80, 750, 320, 70);
    private Rectangle2D potionField = new Rectangle2D.Double(80, 840, 320, 70);

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        collisions = new Collisions();

        player = new Player();
        background = new Background();

        fists = new Fists();
        sword = new Sword();

        //inventory.addItem(fists);
        //inventory.addItem(sword);

        // Start: Spieler hat Fists im Loadout
        //inventory.addWeaponToLoadout(fists);

        healingPotion = new HealingPotion();
        //inventory.addItem(healingPotion);

        // Player bekommt Inventory (falls Player es nutzt)
        player.setInventory(inventory);

        // ===== NPC =====
        storytomole = new StoryTeller(500, 500, 10, 5, 10, 100, "Tomole", 30, 20);
        storytomole.addDialogLine("Hallo!");
        storytomole.addDialogLine("Ich bin Tomole.");
        storytomole.addDialogLine("Drücke E für den nächsten Satz.");
        collisions = new Collisions();

        // ===== Level =====
        level1 = new LevelOne();
        gate = new Gate(800, 200, 200, 200);


        //Enemy
        enemies = new Enemy[4][4];
        secenemies = new Enemy[4][4];
        kind = enemies[0][0];
        //spawnEnemies(1);
        spawnEnemies(3);

        Enemy.setController(this);

        // NPC Swing Fenster (wie bei dir)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SwingUI");
            SwingUI ui = new SwingUI(storytomole);
            frame.setContentPane(ui.outputField);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setPreferredSize(new Dimension(600, 120));
            frame.pack();
            frame.setVisible(true);
        });

        healingPotion = new HealingPotion();

        // Beispiel: Potion liegt im Inventar (damit Swing was anzeigen kann)
        //inventory.addItem(healingPotion);

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
            return new Dieb(x, y, 50, 1, 5, 20, "Dieb", 30, 30);
        }else if (enemyType == 2) {
            return new Kind(x, y, 40, 1, 5, 20, "Kind", 30, 30);
        }else if(enemyType == 3){
            return new Bosslehrer(x, y, 110, 1, 5, 20, "Bosslehrer", 40, 60);
        }

        // if (enemyType == 2) return new Ninja(); oder andere Enemey Typen

        return null;
    }

    private void spawnEnemies(int enemyType) {

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
                if (enemies[i][j] == null) {
                    enemies[i][j] = createEnemyByType(enemyType, (int) x, (int) y);
                }else if (secenemies[i][j] == null) {
                    secenemies[i][j] = createEnemyByType(enemyType, (int) x, (int) y);
                }
            }
        }
    }

    // =================== DRAW ===================

    public void draw(DrawTool drawTool) {
        switch (scene) {
            case 0:
                ui.draw(drawTool);
                break;

            case 1:

                background.draw(drawTool,1);

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

                // Entities
                if (player.getHP() > 0) player.draw(drawTool);
                if (dieb != null && dieb.getHP() > 0) dieb.draw(drawTool);
                storytomole.draw(drawTool);

                // Objects
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

                // UI
                //drawHotbar(drawTool);
                if (inventoryOpen) drawInventoryOverlay(drawTool);
                break;

            case 3:
                deathscreen.draw(drawTool);
                break;

            case 4:
                level1.draw(drawTool);

                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;
                        if (e.getHP() > 0) e.draw(drawTool);
                    }
                }

                player.draw(drawTool);

                /*drawHotbar(drawTool);
                if (inventoryOpen) drawInventoryOverlay(drawTool);
                break;*/
        }
    }

    // =================== UPDATE ===================

    @Override
    public void update(double dt) {
        switch (scene) {
            case 0:
                ui.update(dt);
                break;

            case 1:
                // Reihenfolge wichtig: Combat / Enemy-Hit vor Movement/Inventar
                player.update(dt);
                if (dieb != null && dieb.getHP() > 0) dieb.update(dt);

                handleEnemyHitsPlayer(dieb);
                handlePlayerHitsEnemy(dieb);

                handleMovement(dt);
                handleHealingPotion();

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

                if (collisions.rectangleCollisions(player, gate)) {
                    switchScene(4);
                }


                break;

            case 3:
                deathscreen.update(dt);
                break;

            case 4:

                int toteFeinde = 0;
                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        if (enemies[i][j].getHP() <= 0) {
                            toteFeinde += 1;
                        }
                    }
                }

                player.update(dt);

                // Enemies update
                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;
                        if (e.getHP() > 0) e.update(dt);
                    }
                }

                // Enemy -> Player Hits (für alle)
                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;
                        if (e.getHP() <= 0) continue;
                        handleEnemyHitsPlayer(e);
                    }
                }

                // Player -> Enemy Hits (einmal pro Angriff)
                if (player.canDealHitNow()) {
                    Rectangle2D hitbox = player.getAttackHitbox();
                    boolean hitSomeone = false;

                    for (int i = 0; i < enemies.length; i++) {
                        for (int j = 0; j < enemies[0].length; j++) {
                            Enemy e = enemies[i][j];
                            if (e == null) continue;
                            if (e.getHP() <= 0) continue;

                            if (hitbox.intersects(e.getXpos(), e.getYpos(), e.getWidth(), e.getHeight())) {
                                e.setHP(e.getHP() - player.getAttackDamage());

                                double kx = e.getCenterX() - player.getCenterX();
                                double ky = e.getCenterY() - player.getCenterY();
                                double dist = Math.sqrt(kx * kx + ky * ky);
                                if (dist != 0) { kx /= dist; ky /= dist; }

                                double kb = player.getKnockbackStrength();
                                e.applyKnockback(kx * kb, ky * kb);

                                hitSomeone = true;
                            }
                        }
                    }

                    if (hitSomeone) player.markHitDone();
                }

                handleMovement(dt);
                handleHealingPotion();

                if (collisions.rectangleCollisions(player, storytomole) && storytomole.getETrue()) {
                    storytomole.speak();
                }
                break;
        }
    }

    // =================== Combat Helpers ===================

    private void handleEnemyHitsPlayer(Enemy e) {
        if (e == null) return;
        if (!e.canDealHitNow()) return;

        Rectangle2D enemyHitbox = e.getAttackHitbox();
        if (enemyHitbox.intersects(player.getXpos(), player.getYpos(), player.getWidth(), player.getHeight())) {
            player.setHP(player.getHP() - e.getAttackDamage());
            if (e instanceof Kind){
                e.setRunAway(true);
            }
            double kx = player.getCenterX() - e.getCenterX();
            double ky = player.getCenterY() - e.getCenterY();
            double dist = Math.sqrt(kx * kx + ky * ky);
            if (dist != 0) { kx /= dist; ky /= dist; }
            double knockback = 80;

            if (e instanceof Bosslehrer){
                knockback = 200;
            }else {
                knockback = 80;
            }

            double newX = player.getXpos() + kx * knockback;
            double newY = player.getYpos() + ky * knockback;

            newX = Math.max(0, Math.min(newX, Config.WINDOW_WIDTH - player.getWidth()));
            newY = Math.max(0, Math.min(newY, Config.WINDOW_HEIGHT - player.getHeight()));

            player.setXpos(newX);
            player.setYpos(newY);

            e.markHitDone();
        }
    }

    private void handlePlayerHitsEnemy(Enemy e) {
        if (e == null) return;
        if (!player.canDealHitNow()) return;

        Rectangle2D hitbox = player.getAttackHitbox();
        if (hitbox.intersects(e.getXpos(), e.getYpos(), e.getWidth(), e.getHeight())) {
            e.setHP(e.getHP() - player.getAttackDamage());

            double kx = e.getCenterX() - player.getCenterX();
            double ky = e.getCenterY() - player.getCenterY();
            double dist = Math.sqrt(kx * kx + ky * ky);
            if (dist != 0) { kx /= dist; ky /= dist; }

            double kb = player.getKnockbackStrength();
            e.applyKnockback(kx * kb, ky * kb);

            player.markHitDone();
        }
    }

    // =================== Movement + Collision ===================

    private void handleMovement(double dt) {
        if (inventoryOpen) return;

        double dx = 0, dy = 0;

        if (wDown) { dy -= moveSpeed * dt; player.setFacing(0, -1); player.setIsDownWTrue(); } else player.setIsDownWFalse();
        if (sDown) { dy += moveSpeed * dt; player.setFacing(0,  1); player.setIsDownSTrue(); } else player.setIsDownSFalse();
        if (aDown) { dx -= moveSpeed * dt; player.setFacing(-1, 0); player.setIsDownATrue(); } else player.setIsDownAFalse();
        if (dDown) { dx += moveSpeed * dt; player.setFacing( 1, 0); player.setIsDownDTrue(); } else player.setIsDownDFalse();

        if (dx != 0) {
            double oldX = player.getXpos();
            double newX = oldX + dx;
            newX = Math.max(0, Math.min(newX, Config.WINDOW_WIDTH - player.getWidth()));
            player.setXpos(newX);
            if (playerHitsAnyTree()) player.setXpos(oldX);
        }

        if (dy != 0) {
            double oldY = player.getYpos();
            double newY = oldY + dy;
            newY = Math.max(0, Math.min(newY, Config.WINDOW_HEIGHT - player.getHeight()));
            player.setYpos(newY);
            if (playerHitsAnyTree()) player.setYpos(oldY);
        }
    }

    private void handleHealingPotion() {
        if (healingPotion == null) return;

        if (healingPotion.getHealing()) {
            if (healingPotion.getAmount() > 0) {
                player.setHP(player.getHP() + 20);
                healingPotion.setHealing(false);
                healingPotion.setAmount(healingPotion.getAmount() - 1);
            } else {
                healingPotion.setHealing(false);
            }
        }
    }

    private boolean playerHitsAnyTree() {
        switch (scene) {
            case 1:

                for (int x = 0; x < baum.length; x++) {
                    for (int y = 0; y < baum[x].length; y++) {
                        if (collisions.rectangleCollisions(player, baum[x][y])) return true;
                    }
                }
                for (int x = 0; x < betonZaun.length; x++) {
                    for (int y = 0; y < betonZaun[x].length; y++) {
                        if (collisions.rectangleCollisions(player, betonZaun[x][y])) return true;
                    }
                }
                break;
        }
        return false;
    }

    // =================== UI ===================

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

        //Weapons w = inventory.getSelectedWeapon();
        //Potions p = inventory.getSelectedPotion();

        //drawTool.drawText(55, y + 50, ">> " + inventory.getDisplayName(w));
        //drawTool.drawText(295, y + 50, ">> " + inventory.getDisplayName(p));

        /*if (inventory.getActiveSlot() == 1) {
            drawTool.setCurrentColor(Color.YELLOW);
            drawTool.drawRectangle(40, y, slotW, slotH);
        } else {
            drawTool.setCurrentColor(Color.YELLOW);
            drawTool.drawRectangle(280, y, slotW, slotH);
        }*/
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
        drawTool.drawText((int) weaponField.getX() + 15, (int) weaponField.getY() + 40, "Waffen-Loadout bearbeiten (Hover + E)");

        drawTool.setCurrentColor(overPotion ? new Color(255, 255, 255, 90) : new Color(255, 255, 255, 40));
        drawTool.drawFilledRectangle(potionField.getX(), potionField.getY(), potionField.getWidth(), potionField.getHeight());
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(potionField.getX(), potionField.getY(), potionField.getWidth(), potionField.getHeight());
        drawTool.drawText((int) potionField.getX() + 15, (int) potionField.getY() + 40, "Potions-Loadout bearbeiten (Hover + E)");

        drawTool.drawText(70, 930, "1/2 = Slot in Hand | C = scroll | X = benutzen");
    }

    // ===== Hover + E öffnet Swing Auswahl =====
    private void tryOpenSwingForHoveredField() {
        if (!inventoryOpen) return;

        boolean overWeapon = weaponField.contains(mouseX, mouseY);
        boolean overPotion = potionField.contains(mouseX, mouseY);

//        if (overWeapon) {
//            List<Weapons> options = collectWeaponsFromInventory();
//            Weapons chosen = SwingUI.chooseWeapon(options);
//            if (chosen != null) inventory.addWeaponToSlot(chosen);
//        } else if (overPotion) {
//            List<Potions> options = collectPotionsFromInventory();
//            Potions chosen = SwingUI.choosePotion(options);
//            if (chosen != null) inventory.addPotionToSlot(chosen);
//        }
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

    public static void switchScene(int newSzene) {
        scene = newSzene;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (scene == 0) ui.mouseClicked(e);
        if (healingPotion != null) healingPotion.mouseClicked(e);

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

        if (key == KeyEvent.VK_X) {
            //if (inventory.getActiveSlot() == 1) {
                player.startAttack();
            /*} else {
                Potions p = inventory.getSelectedPotion();
                if (p instanceof HealingPotion) healingPotion.setHealing(true);
            }*/
        }

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
