package my_project.control;

import KAGO_framework.model.InteractiveGraphicalObject;
import KAGO_framework.view.DrawTool;
import my_project.Config;
import my_project.SwingUI;
import my_project.model.Entities.*;
import my_project.model.items.*;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Controller extends InteractiveGraphicalObject {
    private static int scene = 0;

    private UI ui;
    private Player player;

    private Deathscreen deathscreen;
    private Collisions collisions;


    private Background background;

    // Items / Inventory
    private Inventory inventory;

    // Map (aus V2)
//    private Bürgersteig[][] bürgersteig;
//    private Baum[][] baum;
//    private Grünfläche[][] grünfläche;
//    private BetonZaun[][] betonZaun;

    // Level
    private Gate gate;
    private LevelOne level1;

    // ✅ aus V1: Backgate + Wave-Logic
    private Gate backgate;
    private int deathcount = 0;
    private boolean backgateUnlocked = false;
    private boolean wave2Started = false;

    private int hoehe = 10;
    private int breite = 10;
    private int bhoehe = 2;
    private int bbreite = 10;
    private int zhoehe = 2;
    private int zbreite = 4;

    // Enemies
    private Enemy dieb;           // Single enemy for Scene 1
    private Enemy[][] enemies;    // Swarm for Scene 4

    // ✅ aus V1: zweite Welle-Array
    private Enemy[][] secenemies;

    // NPCs
    private StoryTeller storytomole;
    private MerchantNPC merchant;

    // Movement
    private boolean wDown, aDown, sDown, dDown;

    // Inventory Overlay
    private boolean inventoryOpen = false;

    // ✅ IMPORTANT: Hover needs live mouse position
    private int mouseX = 0;
    private int mouseY = 0;

    // "Buttons" im Inventar (Hover + E)
    private Rectangle2D weaponField = new Rectangle2D.Double(80, 750, 320, 70);
    private Rectangle2D potionField = new Rectangle2D.Double(80, 840, 320, 70);

    // ===== COIN DROPS =====
    private final java.util.List<CoinDrop> coinDrops = new ArrayList<>();
    private final Set<Enemy> enemiesThatAlreadyDropped = new HashSet<>();

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        collisions = new Collisions();

        // ✅ aus V1
        background = new Background();

        player = new Player();

        // ✅ INVENTORY (Bugfix vs V1)
        inventory = new Inventory();
        player.setInventory(inventory);

        // ===== NPCs =====
        storytomole = new StoryTeller(0, 220, 10, 5, 10, 100, "Tomole", 80, 20);
        // V1 Story (mehr Lines) + kompatibel zu V2
        storytomole.addDialogLine("Hallo!");
        storytomole.addDialogLine("Ich bin Tomole.");
        storytomole.addDialogLine("Eine komische Präsenz hat seine Klauen in unsere Stadt getrieben.");
        storytomole.addDialogLine("Fast alle haben diese Stadt, die sonst so vor Leben blühte verlassen.");
        storytomole.addDialogLine("Begib dich zum Bahnhof um weiteres zu finden!");

        merchant = new MerchantNPC(560, 500);

        // ===== Level =====
        level1 = new LevelOne();
        gate = new Gate(800, 200, 200, 200);

        // ✅ Backgate aus V1
        backgate = new Gate(1200, 200, 200, 200);

        // ===== Enemies =====
        enemies = new Enemy[4][4];
        secenemies = new Enemy[4][4];

        // V1: direkt Bosslehrer; V2: Dieb
        // Ich spawne wie V1 Bosslehrer als erste Welle,
        // aber setze "dieb" trotzdem auf enemies[0][0], damit Scene1 nichts crasht.
        spawnEnemies(3);
        dieb = enemies[0][0];

        Enemy.setController(this);

        // optional Story Swing-Fenster
        /*
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SwingUI");
            SwingUI ui = new SwingUI(storytomole);
            frame.setContentPane(ui.outputField);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setPreferredSize(new Dimension(600, 120));
            frame.pack();
            frame.setVisible(true);
        });
        */


        // ===== Map bauen (V2) =====
//        bürgersteig = new Bürgersteig[breite][hoehe];
//        for (int x = 0; x < breite; x++) {
//            for (int y = 0; y < hoehe; y++) {
//                bürgersteig[x][y] = new Bürgersteig(100 + x * 30, 100 + y * 30);
//            }
//        }
//
//        betonZaun = new BetonZaun[zbreite][zhoehe];
//        for (int x = 0; x < zbreite; x++) {
//            for (int y = 0; y < zhoehe; y++) {
//                betonZaun[x][y] = new BetonZaun(130 + x * 70, 240 + y * 200);
//            }
//        }
//
//        baum = new Baum[bbreite][bhoehe];
//        grünfläche = new Grünfläche[bbreite][bhoehe];
//        for (int x = 0; x < bbreite; x++) {
//            for (int y = 0; y < bhoehe; y++) {
//                baum[x][y] = new Baum(100 + x * 70, 100 + y * 200);
//                grünfläche[x][y] = new Grünfläche(100 + x * 70, 145 + y * 200);
//            }
//        }
    }

    public Player getPlayer() {
        return player;
    }

    // =================== Enemy Spawning ===================

    private Enemy createEnemyByType(int enemyType, int x, int y) {
        if (enemyType == 1) {
            return new Dieb(x, y, 20, 1, 5, 20, "Dieb", 30, 30);
        } else if (enemyType == 2) {
            // ✅ aus V1
            return new Kind(x, y, 40, 1, 5, 20, "Kind", 30, 30);
        } else if (enemyType == 3) {
            // ✅ aus V1
            return new Bosslehrer(x, y, 110, 1, 5, 20, "Bosslehrer", 40, 60);
        }
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

                // ✅ Logik aus V1: erst enemies füllen, dann secenemies (Wave2)
                if (enemies[i][j] == null) {
                    enemies[i][j] = createEnemyByType(enemyType, (int) x, (int) y);
                } else if (secenemies[i][j] == null) {
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
                // ✅ Hintergrund aus V1
                background.draw(drawTool, 1);

                // (Map aus V2: optional, du kannst es lassen/entfernen – ich lasse V2-Style bestehen)
//                for (int x = 0; x < bürgersteig.length; x++) {
//                    for (int y = 0; y < bürgersteig[x].length; y++) {
//                        bürgersteig[x][y].draw(drawTool);
//                    }
//                }
//
//                for (int x = 0; x < baum.length; x++) {
//                    for (int y = 0; y < baum[x].length; y++) {
//                        grünfläche[x][y].draw(drawTool);
//                    }
//                }

                if (player.getHP() > 0) player.draw(drawTool);

                // optional: falls dieb in Scene1 noch genutzt wird
                if (dieb != null && dieb.getHP() > 0) dieb.draw(drawTool);

                storytomole.draw(drawTool);
                merchant.draw(drawTool);

//                for (int x = 0; x < betonZaun.length; x++) {
//                    for (int y = 0; y < betonZaun[x].length; y++) {
//                        betonZaun[x][y].draw(drawTool);
//                    }
//                }
//                for (int x = 0; x < baum.length; x++) {
//                    for (int y = 0; y < baum[x].length; y++) {
//                        baum[x][y].draw(drawTool);
//                    }
//                }

                gate.draw(drawTool);

                for (CoinDrop c : coinDrops) c.draw(drawTool);

                drawHotbar(drawTool);
                drawHPBar(drawTool);

                if (inventoryOpen) drawInventoryOverlay(drawTool);
                break;

            case 3:
                deathscreen.draw(drawTool);
                break;

            case 4:
                level1.draw(drawTool);

                if (backgateUnlocked) backgate.draw(drawTool);

                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;
                        if (e.getHP() > 0) e.draw(drawTool);
                    }
                }

                player.draw(drawTool);

                for (CoinDrop c : coinDrops) c.draw(drawTool);

                drawHotbar(drawTool);
                drawHPBar(drawTool);

                if (inventoryOpen) drawInventoryOverlay(drawTool);
                break;
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
                player.update(dt);

                // falls Scene1 noch einen Gegner hat


                // Coin Drop (falls in Scene1 genutzt)
                if (dieb != null && dieb.getHP() <= 0 && !enemiesThatAlreadyDropped.contains(dieb)) {
                    enemiesThatAlreadyDropped.add(dieb);
                    int coins = ThreadLocalRandom.current().nextInt(1, 6);
                    spawnCoinDrop(dieb.getCenterX(), dieb.getCenterY(), coins);
                }

                handleMovement(dt);

                // Gate -> Szene4
                if (collisions.rectangleCollisions(player, gate)) {
                    switchScene(4);
                }

                handleCoinPickup();
                break;

            case 3:
                deathscreen.update(dt);
                break;

            case 4:
                // ✅ Backgate zurück zu Szene1
                if (backgateUnlocked && collisions.rectangleCollisions(player, backgate)) {
                    switchScene(1);
                }

                // ✅ Wave1 deathcount (nur 1x pro Frame korrekt zählen)
                int aliveOrDeadCount = 0;
                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;
                        if (e.getHP() <= 0) aliveOrDeadCount++;
                    }
                }
                deathcount = aliveOrDeadCount;

                if (deathcount == 16) {
                    backgateUnlocked = true;

                    if (!wave2Started) {
                        wave2Started = true;
                        deathcount = 0;
                        player.resetPlayerPosition();
                        spawnEnemies(2); // ✅ Wave2 = Kind
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

                // Enemy -> Player Hits
                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;
                        if (e.getHP() <= 0) continue;
                        handleEnemyHitsPlayer(e);
                    }
                }

                // Player -> Enemy Hits
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

                // Coin drops für tote Enemies
                for (int i = 0; i < enemies.length; i++) {
                    for (int j = 0; j < enemies[0].length; j++) {
                        Enemy e = enemies[i][j];
                        if (e == null) continue;

                        if (e.getHP() <= 0 && !enemiesThatAlreadyDropped.contains(e)) {
                            enemiesThatAlreadyDropped.add(e);
                            int coins = ThreadLocalRandom.current().nextInt(1, 6);
                            spawnCoinDrop(e.getCenterX(), e.getCenterY(), coins);
                        }
                    }
                }

                handleCoinPickup();

                // Story
                if (collisions.rectangleCollisions(player, storytomole)) {
                    // speak nur auf E
                }
                break;
        }
    }

    // =================== Combat Helpers ===================

    private void handleEnemyHitsPlayer(Enemy e) {
        if (e == null) return;
        if (!e.canDealHitNow()) return;

        // ✅ ResistancePotion Support (Version2)
        if (player.isInvulnerable()) {
            e.markHitDone();
            return;
        }

        Rectangle2D enemyHitbox = e.getAttackHitbox();
        if (enemyHitbox.intersects(player.getXpos(), player.getYpos(), player.getWidth(), player.getHeight())) {
            player.setHP(player.getHP() - e.getAttackDamage());

            // ✅ aus V1: Kind rennt weg
            if (e instanceof Kind) {
                e.setRunAway(true);
            }

            double kx = player.getCenterX() - e.getCenterX();
            double ky = player.getCenterY() - e.getCenterY();
            double dist = Math.sqrt(kx * kx + ky * ky);
            if (dist != 0) { kx /= dist; ky /= dist; }

            // ✅ aus V1: Bosslehrer stärkerer Knockback
            double knockback = (e instanceof Bosslehrer) ? 200 : 80;

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

        // ✅ nutzt Player-Speed (V2)
        double moveSpeed = player.getMoveSpeed();

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
            //if (playerHitsAnyTree()) player.setXpos(oldX);
        }

        if (dy != 0) {
            double oldY = player.getYpos();
            double newY = oldY + dy;
            newY = Math.max(0, Math.min(newY, Config.WINDOW_HEIGHT - player.getHeight()));
            player.setYpos(newY);
            //if (playerHitsAnyTree()) player.setYpos(oldY);
        }
    }

//    private boolean playerHitsAnyTree() {
//        for (int x = 0; x < baum.length; x++) {
//            for (int y = 0; y < baum[x].length; y++) {
//                if (collisions.rectangleCollisions(player, baum[x][y])) return true;
//            }
//        }
//        for (int x = 0; x < betonZaun.length; x++) {
//            for (int y = 0; y < betonZaun[x].length; y++) {
//                if (collisions.rectangleCollisions(player, betonZaun[x][y])) return true;
//            }
//        }
//        return false;
//    }

    // =================== Coins ===================

    private void spawnCoinDrop(double centerX, double centerY, int amount) {
        coinDrops.add(new CoinDrop(centerX - CoinDrop.SIZE / 2.0, centerY - CoinDrop.SIZE / 2.0, amount));
    }

    private void handleCoinPickup() {
        Iterator<CoinDrop> iterator = coinDrops.iterator();
        while (iterator.hasNext()) {
            CoinDrop c = iterator.next();
            if (c.getHitBox().intersects(player.getHitBox())) {
                inventory.addCoins(c.getValue());
                iterator.remove();
            }
        }
    }

    // =================== UI ===================

    private void drawHotbar(DrawTool drawTool) {
        int y = 980;
        int slotW = 220;
        int slotH = 70;

        drawTool.setCurrentColor(new Color(0, 0, 0, 160));
        drawTool.drawFilledRectangle(40, y, slotW, slotH);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(40, y, slotW, slotH);

        drawTool.setCurrentColor(new Color(0, 0, 0, 160));
        drawTool.drawFilledRectangle(280, y, slotW, slotH);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(280, y, slotW, slotH);

        drawTool.formatText("Arial", 0, 18);
        drawTool.drawText(55, y + 25, "Slot 1 (Waffe) [1]");
        drawTool.drawText(295, y + 25, "Slot 2 (Potion) [2]");

        Weapons w = inventory.getSelectedWeapon();
        Potions p = inventory.getSelectedPotion();

        drawTool.drawText(55, y + 50, ">> " + inventory.getDisplayName(w));
        drawTool.drawText(295, y + 50, ">> " + inventory.getDisplayName(p));

        drawTool.setCurrentColor(Color.YELLOW);
        if (inventory.getActiveSlot() == 1) drawTool.drawRectangle(40, y, slotW, slotH);
        else drawTool.drawRectangle(280, y, slotW, slotH);
    }

    private void drawInventoryOverlay(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(0, 0, 0, 180));
        drawTool.drawFilledRectangle(40, 40, 600, 920);

        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawRectangle(40, 40, 600, 920);

        drawTool.formatText("Arial", 1, 26);
        drawTool.drawText(70, 80, "INVENTAR (I zum Schließen)");

        drawTool.formatText("Arial", 0, 20);
        drawTool.drawText(70, 110, "Coins: " + inventory.getCoins());

        drawTool.formatText("Arial", 0, 18);
        drawTool.drawText(70, 140, "Aufzählung Items:");
        int y = 170;

        for (Item item : inventory.getItemsAsJavaList()) {
            drawTool.drawText(80, y, "- " + inventory.getDisplayName(item));
            y += 22;
            if (y > 700) break;
        }

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

        drawTool.drawText(70, 930, "1/2 = Slot | C = scroll | X = benutzen | E im Inventar = Loadout Editor");
    }

    private void tryOpenSwingForHoveredField() {
        if (!inventoryOpen) return;

        boolean overWeapon = weaponField.contains(mouseX, mouseY);
        boolean overPotion = potionField.contains(mouseX, mouseY);

        if (overWeapon) {
            java.util.List<Weapons> available = collectWeaponsFromInventory();
            java.util.List<Weapons> current = inventory.getWeaponLoadoutCopy();
            java.util.List<Weapons> edited = SwingUI.editWeaponsLoadout(available, current);
            if (edited != null) inventory.setWeaponLoadout(edited);

        } else if (overPotion) {
            java.util.List<Potions> available = collectPotionsFromInventory();
            java.util.List<Potions> current = inventory.getPotionLoadoutCopy();
            java.util.List<Potions> edited = SwingUI.editPotionsLoadout(available, current);
            if (edited != null) inventory.setPotionLoadout(edited);
        }
    }

    private java.util.List<Weapons> collectWeaponsFromInventory() {
        java.util.List<Weapons> out = new ArrayList<>();
        var ds = inventory.getItemsDS();
        ds.toFirst();
        while (ds.hasAccess()) {
            Item item = ds.getContent();
            if (item instanceof Weapons) out.add((Weapons) item);
            ds.next();
        }
        return out;
    }

    private java.util.List<Potions> collectPotionsFromInventory() {
        java.util.List<Potions> out = new ArrayList<>();
        var ds = inventory.getItemsDS();
        ds.toFirst();
        while (ds.hasAccess()) {
            Item item = ds.getContent();
            if (item instanceof Potions) out.add((Potions) item);
            ds.next();
        }
        return out;
    }

    private void openStoryWindow() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dialog mit Tomole");
            // Hier übergeben wir den NPC 'storytomole', damit sein Text angezeigt wird
            SwingUI ui = new SwingUI(storytomole);

            frame.setContentPane(ui.outputField);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fenster schließt sich, Spiel läuft weiter
            frame.setPreferredSize(new Dimension(600, 120));
            frame.pack();

            // Optional: Fenster in der Bildschirmmitte platzieren
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }

    // =================== HP BAR ===================

    private void drawHPBar(DrawTool drawTool) {
        if (player == null) return;

        int barWidth = 300;
        int barHeight = 25;
        int margin = 30;

        int x = Config.WINDOW_WIDTH - barWidth - margin;
        int y = margin;

        double hp = player.getHP();
        double maxHp = player.getMaxHP();
        if (maxHp <= 0) maxHp = 1;

        double ratio = Math.max(0, Math.min(1, hp / maxHp));
        int currentWidth = (int) (barWidth * ratio);

        drawTool.setCurrentColor(new Color(0, 0, 0, 180));
        drawTool.drawFilledRectangle(x, y, barWidth, barHeight);

        drawTool.setCurrentColor(new Color(0, 200, 0));
        drawTool.drawFilledRectangle(x, y, currentWidth, barHeight);

        drawTool.setCurrentColor(Color.BLACK);
        drawTool.drawRectangle(x, y, barWidth, barHeight);

        drawTool.formatText("Arial", 0, 14);
        drawTool.setCurrentColor(Color.WHITE);
        drawTool.drawText(x + 8, y + 18, (int) hp + " / " + (int) maxHp + " HP");
    }



    // =================== INPUT ===================

    public static void switchScene(int newSzene) {
        scene = newSzene;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (scene == 0) ui.mouseClicked(e);
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
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
            if (inventory.getActiveSlot() == 1) {
                player.startAttack();
            } else {
                inventory.consumeSelectedPotion(player);
            }
        }

        if (key == KeyEvent.VK_E) {
            if (inventoryOpen) {
                tryOpenSwingForHoveredField();
                return;
            }

            if (collisions.rectangleCollisions(player, merchant)) {
                SwingUI.openShop(inventory, player);
                return;
            }

            if (collisions.rectangleCollisions(player, storytomole)) {
                storytomole.speak();
            }
        }

        if (!inventoryOpen) {
            if (key == KeyEvent.VK_W) wDown = true;
            if (key == KeyEvent.VK_A) aDown = true;
            if (key == KeyEvent.VK_S) sDown = true;
            if (key == KeyEvent.VK_D) dDown = true;
        }

        if (key == KeyEvent.VK_E) {
            if (inventoryOpen) {
                tryOpenSwingForHoveredField();
                return;
            }

            if (collisions.rectangleCollisions(player, merchant)) {
                SwingUI.openShop(inventory, player);
                return;
            }

            // HIER IST DIE ÄNDERUNG:
            if (collisions.rectangleCollisions(player, storytomole)) {
                storytomole.speak(); // Konsolen-Output (optional, kann bleiben)
                openStoryWindow();   // <-- Öffnet jetzt das Swing-Fenster!
            }
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
