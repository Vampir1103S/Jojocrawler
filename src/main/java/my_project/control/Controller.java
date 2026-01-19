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
import java.util.*;
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

    // Level / Gates
    private Gate StationGate1;
    private Gate StationGate2;
    private Gate TrainGate;
    private Gate Maingate;

    // Scene 4 (dein alter Teil)
    private int deathcount = 0;
    private boolean backgateUnlocked = false;
    private boolean wave2Started = false;

    // Enemies (Scene 4)
    private Enemy dieb;
    private Enemy[][] enemies;
    private Enemy[][] secenemies;

    // NPCs (Scene 1)
    private StoryTeller storytomole;
    private MerchantNPC merchant;

    // Movement
    private boolean wDown, aDown, sDown, dDown;

    // Inventory Overlay
    private boolean inventoryOpen = false;

    // Mouse hover
    private int mouseX = 0;
    private int mouseY = 0;

    // Inventory buttons (Hover + E)
    private Rectangle2D weaponField = new Rectangle2D.Double(80, 750, 320, 70);
    private Rectangle2D potionField = new Rectangle2D.Double(80, 840, 320, 70);

    // ===== COIN DROPS =====
    private final java.util.List<CoinDrop> coinDrops = new ArrayList<>();
    private final Set<Object> alreadyDropped = new HashSet<>(); // verhindert Double-Drops (Scene4+Scene3)

    // =================== SCENE 2 WALLS ===================
    private final double[][] scene2Walls = new double[][]{
            {390, 0, 10, 550},
            {390, 750, 10, 550},
            {1420, 0, 10, 550},
            {1420, 550, 200, 10},
            {1500, 550, 10, 800},
    };

    // =================== SCENE 3 WALLS ===================
    private final double[][] scene3Walls = new double[][]{
            {0, 500, 2000, 10},
            {0, 850, 2000, 10},
    };

    // =================== SCENE 3 (Arena + Scaling) ===================
    private final java.util.List<Enemy> scene3Enemies = new ArrayList<>();

    // Wie oft Szene 3 betreten wurde (wichtig für "mehr Gegner" + "gemischt")
    private int scene3Run = 0;

    // Spawn-Bereich zwischen den Wänden
    private final double scene3MinX = 200;
    private final double scene3MaxX = 1940;
    private final double scene3MinY = 510;
    private final double scene3MaxY = 840;

    // Scaling
    private final int scene3BaseEnemies = 6;   // erstes Mal
    private final int scene3MorePerRun = 4;    // jedes weitere Mal +4

    // Mischchance Kind
    private final double scene3KindBaseChance = 0.15;  // erstes Mal 15%
    private final double scene3KindMorePerRun = 0.10;  // +10% pro Run
    private final double scene3KindMaxChance = 0.60;   // max 60%

    // Portal/Gate in Szene 3 -> zurück nach Szene 2
    private final Gate scene3ExitGate = new Gate(1900, 430, 10, 2000);
    private boolean scene3Cleared = false;

    public Controller() {
        ui = new UI();
        deathscreen = new Deathscreen();
        collisions = new Collisions();

        background = new Background();

        player = new Player();

        inventory = new Inventory();
        player.setInventory(inventory);

        // NPCs
        storytomole = new StoryTeller(0, 220, 10, 5, 10, 100, "Tomole", 200 , 100);
        storytomole.addDialogLine("Hallo!");
        storytomole.addDialogLine("Ich bin Tomole.");
        storytomole.addDialogLine("Eine komische Präsenz hat seine Klauen in unsere Stadt getrieben.");
        storytomole.addDialogLine("Fast alle haben diese Stadt, die sonst so vor Leben blühte verlassen.");
        storytomole.addDialogLine("Begib dich zum Bahnhof um weiteres zu finden!");

        merchant = new MerchantNPC(1500, 600);

        Maingate = new Gate(100, 100, 100, 10);
        StationGate1 = new Gate(340, 100, 445, 10);

        TrainGate = new Gate(300, 590, 10, 100);
        StationGate2 = new Gate(200, 1080, 800, 10);

        // Enemies (Scene 4)
        enemies = new Enemy[4][4];
        secenemies = new Enemy[4][4];

        spawnEnemies(3);
        dieb = enemies[0][0];

        Enemy.setController(this);

//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("SwingUI");
//            SwingUI ui = new SwingUI(storytomole);
//            frame.setContentPane(ui.outputField);
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.setPreferredSize(new Dimension(600, 120));
//            frame.pack();
//            frame.setVisible(true);
//        });
    }

    public Player getPlayer() {
        return player;
    }

    // =================== ENEMY FACTORY ===================

    private Enemy createEnemyByType(int enemyType, int x, int y) {
        if (enemyType == 1) {
            return new Dieb(x, y, 20, 1, 5, 20, "Dieb", 30, 30);
        } else if (enemyType == 2) {
            return new Kind(x, y, 40, 1, 5, 20, "Kind", 30, 30);
        } else if (enemyType == 3) {
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
                background.draw(drawTool, 1);
                if (player.getHP() > 0) player.draw(drawTool);
                storytomole.draw(drawTool);
                merchant.draw(drawTool);
                drawCoinDrops(drawTool);
                drawHotbar(drawTool);
                drawHPBar(drawTool);
                if (inventoryOpen) drawInventoryOverlay(drawTool);

                break;

            case 2:
                background.draw(drawTool, 2);
                player.draw(drawTool);
                drawCoinDrops(drawTool);
                drawHotbar(drawTool);
                drawHPBar(drawTool);
                if (inventoryOpen) drawInventoryOverlay(drawTool);
                break;

            case 3:
                background.draw(drawTool, 3);

                for (Enemy e : scene3Enemies) {
                    if (e == null) continue;
                    if (e.getHP() > 0) e.draw(drawTool);
                }

                player.draw(drawTool);
                drawCoinDrops(drawTool);
                drawHotbar(drawTool);
                drawHPBar(drawTool);
                if (inventoryOpen) drawInventoryOverlay(drawTool);


                break;

            case 4:
                deathscreen.draw(drawTool);
                break;
        }
    }

    private void drawCoinDrops(DrawTool drawTool) {
        for (CoinDrop c : coinDrops) c.draw(drawTool);
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
                handleMovement(dt);

                if (collisions.rectangleCollisions(player, StationGate1)) {
                    switchScene(2);
                    teleportPlayer(875, 950);
                }

                handleCoinPickup();
                break;

            case 2:
                player.update(dt);
                handleMovement(dt);

                if (collisions.rectangleCollisions(player, StationGate2)) {
                    switchScene(1);
                    teleportPlayer(520, 180);
                }

                if (collisions.rectangleCollisions(player, TrainGate)) {
                    switchScene(3);
                    startScene3();           // ✅ spawnt Gegner (mehr + gemischt je nach Run)
                    teleportPlayer(200, 650); // ✅ Player rein in die Arena
                }

                handleCoinPickup();
                break;

            case 3:

                player.update(dt);
                handleMovement(dt);

                updateScene3Enemies(dt);
                handleScene3EnemyHitsPlayer();    // ✅ Gegner schlagen
                handleScene3PlayerHitsEnemies();  // ✅ Spieler trifft

                handleScene3CoinDrops();          // ✅ Coins droppen (einmal pro Enemy)
                handleCoinPickup();               // ✅ Coins einsammeln

                scene3Cleared = isScene3Cleared();
                if (scene3Cleared && collisions.rectangleCollisions(player, scene3ExitGate)) {
                    switchScene(2);              // ✅ zurück nach Szene 2
                    teleportPlayer(350, 600);    // ✅ Spawnpunkt in Szene 2
                }
                if(player.getHP() <= 0) scene = 4;

                break;

            case 4:
                deathscreen.update(dt);
                break;
        }

        // Scene4 CoinDrops (falls du es weiterhin willst)
        if (scene == 3 || scene == 4) {
            handleScene4CoinDrops();
        }
    }

    // =================== TELEPORT ===================

    private void teleportPlayer(double x, double y) {
        player.setXpos(x);
        player.setYpos(y);

        // Wichtig: sonst rutscht/buggt Bewegung weil Keys noch "down" sind
        wDown = aDown = sDown = dDown = false;
        inventoryOpen = false;
    }

    // =================== COMBAT ===================

    private void handleEnemyHitsPlayer(Enemy e) {
        if (e == null) return;
        if (!e.canDealHitNow()) return;

        if (player.isInvulnerable()) {
            e.markHitDone();
            return;
        }

        Rectangle2D enemyHitbox = e.getAttackHitbox();
        if (enemyHitbox.intersects(player.getXpos(), player.getYpos(), player.getWidth(), player.getHeight())) {

            player.setHP(player.getHP() - e.getAttackDamage());

            // ✅ Knockback NUR auf X-Achse
            double knockback = (e instanceof Bosslehrer) ? 200 : 80;

            double kx = player.getCenterX() - e.getCenterX();
            if (kx == 0) kx = 1;
            else kx = kx / Math.abs(kx); // -1 oder +1

            double newX = player.getXpos() + kx * knockback;
            double newY = player.getYpos(); // Y bleibt gleich

            newX = Math.max(0, Math.min(newX, getSceneMaxX() - player.getWidth()));
            newY = Math.max(0, Math.min(newY, getSceneMaxY() - player.getHeight()));

            player.setXpos(newX);
            player.setYpos(newY);

            e.markHitDone();
        }
    }

    // =================== MOVEMENT + WALLS ===================

    private void handleMovement(double dt) {
        if (inventoryOpen) return;

        double moveSpeed = player.getMoveSpeed();

        double dx = 0, dy = 0;

        if (wDown) { dy -= moveSpeed * dt; player.setFacing(0, -1); player.setIsDownWTrue(); } else player.setIsDownWFalse();
        if (sDown) { dy += moveSpeed * dt; player.setFacing(0,  1); player.setIsDownSTrue(); } else player.setIsDownSFalse();
        if (aDown) { dx -= moveSpeed * dt; player.setFacing(-1, 0); player.setIsDownATrue(); } else player.setIsDownAFalse();
        if (dDown) { dx += moveSpeed * dt; player.setFacing( 1, 0); player.setIsDownDTrue(); } else player.setIsDownDFalse();

        double maxX = getSceneMaxX();
        double maxY = getSceneMaxY();

        if (dx != 0) {
            double oldX = player.getXpos();
            double newX = oldX + dx;
            newX = Math.max(0, Math.min(newX, maxX - player.getWidth()));
            player.setXpos(newX);

            if ((scene == 2 && collidesWithAnyWall(scene2Walls)) ||
                    (scene == 3 && collidesWithAnyWall(scene3Walls))) {
                player.setXpos(oldX);
            }
        }

        if (dy != 0) {
            double oldY = player.getYpos();
            double newY = oldY + dy;
            newY = Math.max(0, Math.min(newY, maxY - player.getHeight()));
            player.setYpos(newY);

            if ((scene == 2 && collidesWithAnyWall(scene2Walls)) ||
                    (scene == 3 && collidesWithAnyWall(scene3Walls))) {
                player.setYpos(oldY);
            }
        }
    }

    private double getSceneMaxX() {
        if (scene == 2 || scene == 3) return 2000;
        return Config.WINDOW_WIDTH;
    }

    private double getSceneMaxY() {
        if (scene == 2 || scene == 3) return 1300;
        return Config.WINDOW_HEIGHT;
    }

    private boolean collidesWithAnyWall(double[][] walls) {
        if (walls == null) return false;
        for (double[] r : walls) {
            if (r == null || r.length < 4) continue;
            // braucht deine neue Collisions-Funktion: rectangleCollisions(Entity, x,y,w,h)
            if (collisions.rectangleCollisions(player, r[0], r[1], r[2], r[3])) return true;
        }
        return false;
    }

    private void drawScene2Walls(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(255, 0, 0, 180));
        for (double[] r : scene2Walls) {
            if (r == null || r.length < 4) continue;
            drawTool.drawFilledRectangle(r[0], r[1], r[2], r[3]);
        }
    }

    private void drawScene3Walls(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(0, 0, 0, 180));
        for (double[] r : scene3Walls) {
            if (r == null || r.length < 4) continue;
            drawTool.drawFilledRectangle(r[0], r[1], r[2], r[3]);
        }
    }

    // =================== COINS ===================

    private void spawnCoinDrop(double centerX, double centerY, int amount) {
        coinDrops.add(new CoinDrop(centerX - CoinDrop.SIZE / 2.0, centerY - CoinDrop.SIZE / 2.0, amount));
    }

    /**
     * ✅ WICHTIG: wir nutzen NICHT getX()/getY() (hast du nicht),
     * sondern CoinDrop.getHitBox() (hast du sehr wahrscheinlich, weil du früher so gepickt hast).
     */
    private void handleCoinPickup() {
        Iterator<CoinDrop> it = coinDrops.iterator();
        while (it.hasNext()) {
            CoinDrop c = it.next();

            // robust: wenn CoinDrop getHitBox() hat:
            if (c.getHitBox().intersects(player.getHitBox())) {
                inventory.addCoins(c.getValue());
                it.remove();
            }
        }
    }

    // Coins aus Scene3 Enemies (nur 1x pro Enemy)
    private void handleScene3CoinDrops() {
        for (Enemy e : scene3Enemies) {
            if (e == null) continue;

            if (e.getHP() <= 0 && !alreadyDropped.contains(e)) {
                alreadyDropped.add(e);
                int coins = ThreadLocalRandom.current().nextInt(1, 6);
                spawnCoinDrop(e.getCenterX(), e.getCenterY(), coins);
            }
        }
    }

    // Coins aus Scene4 Enemies (nur 1x pro Enemy)
    private void handleScene4CoinDrops() {
        if (enemies == null) return;

        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < enemies[0].length; j++) {
                Enemy e = enemies[i][j];
                if (e == null) continue;

                if (e.getHP() <= 0 && !alreadyDropped.contains(e)) {
                    alreadyDropped.add(e);
                    int coins = ThreadLocalRandom.current().nextInt(1, 6);
                    spawnCoinDrop(e.getCenterX(), e.getCenterY(), coins);
                }
            }
        }

        // optional: wave2 enemies
        if (secenemies != null) {
            for (int i = 0; i < secenemies.length; i++) {
                for (int j = 0; j < secenemies[0].length; j++) {
                    Enemy e = secenemies[i][j];
                    if (e == null) continue;

                    if (e.getHP() <= 0 && !alreadyDropped.contains(e)) {
                        alreadyDropped.add(e);
                        int coins = ThreadLocalRandom.current().nextInt(1, 6);
                        spawnCoinDrop(e.getCenterX(), e.getCenterY(), coins);
                    }
                }
            }
        }
    }

    // =================== SCENE 3 LOGIC ===================

    /**
     * Wird GENAU 1x aufgerufen, wenn du von Szene 2 -> Szene 3 gehst.
     * Erhöht Run, spawnt mehr + gemischt.
     */
    private void startScene3() {
        scene3Run++;

        scene3Enemies.clear();
        scene3Cleared = false;

        // sehr wichtig: sonst droppen "neue" Gegner ggf. keine Coins, wenn alte Referenzen noch drin sind
        // Wir löschen NUR Scene3-Referenzen aus alreadyDropped, indem wir es komplett leeren (safe).
        alreadyDropped.clear();

        int total = scene3BaseEnemies + (scene3Run - 1) * scene3MorePerRun;
        spawnScene3EnemiesMixed(total);
    }

    private void spawnScene3EnemiesMixed(int count) {
        scene3Enemies.clear();

        double kindChance = Math.min(scene3KindBaseChance + scene3KindMorePerRun * (scene3Run - 1), scene3KindMaxChance);

        for (int i = 0; i < count; i++) {
            double x = randomRange(scene3MinX, scene3MaxX - 60);
            double y = randomRange(scene3MinY, scene3MaxY - 110);

            int enemyType = (ThreadLocalRandom.current().nextDouble() < kindChance) ? 2 : 1;

            Enemy e = createEnemyByType(enemyType, (int) x, (int) y);
            if (e != null) scene3Enemies.add(e);
        }
    }

    private boolean isScene3Cleared() {
        for (Enemy e : scene3Enemies) {
            if (e != null && e.getHP() > 0) return false;
        }
        return true;
    }

    private void updateScene3Enemies(double dt) {
        for (Enemy e : scene3Enemies) {
            if (e == null) continue;
            if (e.getHP() > 0) e.update(dt);
        }
    }

    private void handleScene3EnemyHitsPlayer() {
        for (Enemy e : scene3Enemies) {
            if (e == null) continue;
            if (e.getHP() <= 0) continue;
            handleEnemyHitsPlayer(e);
        }
    }

    private void handleScene3PlayerHitsEnemies() {
        if (!player.canDealHitNow()) return;

        Rectangle2D hitbox = player.getAttackHitbox();
        boolean hitSomeone = false;

        for (Enemy e : scene3Enemies) {
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

        if (hitSomeone) player.markHitDone();
    }

    private double randomRange(double a, double b) {
        return ThreadLocalRandom.current().nextDouble(Math.min(a, b), Math.max(a, b));
    }

    // =================== UI (Hotbar/Inventory/HP) ===================

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

    // =================== Swing Loadout Editor ===================

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
}
