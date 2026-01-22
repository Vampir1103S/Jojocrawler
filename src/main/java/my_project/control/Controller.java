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


    private Inventory inventory;


    private Gate StationGate1;
    private Gate StationGate2;
    private Gate TrainGate;
    private Gate Maingate;


    private int deathcount = 0;
    private boolean backgateUnlocked = false;
    private boolean wave2Started = false;


    private Enemy dieb;
    private Enemy[][] enemies;
    private Enemy[][] secenemies;


    private StoryTeller storytomole;
    private MerchantNPC merchant;

    private boolean wDown, aDown, sDown, dDown;

    private boolean inventoryOpen = false;

    private int mouseX = 0;
    private int mouseY = 0;

    private Rectangle2D weaponField = new Rectangle2D.Double(80, 750, 320, 70);
    private Rectangle2D potionField = new Rectangle2D.Double(80, 840, 320, 70);

    private final java.util.List<CoinDrop> coinDrops = new ArrayList<>();
    private final Set<Object> alreadyDropped = new HashSet<>();

    private final double[][] scene2Walls = new double[][]{
            {390, 0, 10, 550},
            {390, 750, 10, 550},
            {1420, 0, 10, 550},
            {1420, 550, 200, 10},
            {1500, 550, 10, 800},
    };

    private final double[][] scene3Walls = new double[][]{
            {0, 500, 2000, 10},
            {0, 850, 2000, 10},
    };

    private final java.util.List<Enemy> scene3Enemies = new ArrayList<>();

    private int scene3Run = 0;

    private final double scene3MinX = 500;
    private final double scene3MaxX = 1940;
    private final double scene3MinY = 510;
    private final double scene3MaxY = 840;

    private final int scene3BaseEnemies = 6;
    private final int scene3MorePerRun = 4;

    private final double scene3KindBaseChance = 0.15;
    private final double scene3KindMorePerRun = 0.10;
    private final double scene3KindMaxChance = 0.60;

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

        enemies = new Enemy[4][4];
        secenemies = new Enemy[4][4];

        spawnEnemies(3);
        dieb = enemies[0][0];

        Enemy.setController(this);


    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Erzeugt einen neuen Gegner basierend auf einem übergebenen Typ.
     * <p>
     * Abhängig vom {@code enemyType} wird eine konkrete Unterklasse von
     * {@link Enemy} instanziiert und mit vordefinierten Attributen
     * (Lebenspunkte, Geschwindigkeit, Größe usw.) initialisiert.
     * Diese Methode wird typischerweise beim Spawnen von Gegnern
     * in Arenen oder Szenen verwendet.
     *
     * <ul>
     *   <li>{@code enemyType == 1}: {@link Dieb}</li>
     *   <li>{@code enemyType == 2}: {@link Kind}</li>
     *   <li>{@code enemyType == 3}: {@link Bosslehrer}</li>
     * </ul>
     *
     * @param enemyType der numerische Typ des Gegners
     * @param x         Startposition des Gegners auf der X-Achse
     * @param y         Startposition des Gegners auf der Y-Achse
     * @return eine neue {@link Enemy}-Instanz des gewünschten Typs
     *         oder {@code null}, falls der Typ unbekannt ist
     */

    private Enemy createEnemyByType(int enemyType, int x, int y) {
        if (enemyType == 1) {
            return new Dieb(x, y, 20, 1, 5, 20, "Dieb", 50, 100);
        } else if (enemyType == 2) {
            return new Kind(x, y, 40, 1, 5, 20, "Kind", 50, 100);
        } else if (enemyType == 3) {
            return new Bosslehrer(x, y, 110, 1, 5, 20, "Bosslehrer", 40, 60);
        }
        return null;
    }
    /**
     * Spawnt Gegner eines bestimmten Typs in einer rasterförmigen Anordnung.
     * <p>
     * Die Methode durchläuft ein zweidimensionales Gegner-Array und erzeugt
     * neue Gegnerinstanzen an fest definierten Positionen.
     * Die Positionen ergeben sich aus einer Startposition sowie festen
     * Abständen (Swarm-Abstand) zwischen den einzelnen Gegnern.
     *
     * Zunächst wird versucht, freie Plätze im primären Gegner-Array
     * ({@code enemies}) zu belegen. Sind diese bereits gefüllt, werden
     * Gegner im sekundären Array ({@code secenemies}) erzeugt.
     *
     * Diese Methode wird typischerweise verwendet, um Gegnerwellen
     * oder Arenakämpfe zu initialisieren.
     *
     * @param enemyType der Typ des zu spawnenden Gegners
     *                  (wird an {@link #createEnemyByType(int, int, int)} weitergegeben)
     */
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
    /**
     * Zeichnet alle aktuell vorhandenen Coin-Drops auf den Bildschirm.
     * <p>
     * Die Methode durchläuft die Sammlung aller fallengelassenen Coins
     * und ruft für jeden {@link CoinDrop} dessen Zeichenmethode auf.
     * Sie wird im Render-Zyklus des Spiels aufgerufen, um eingesammelte
     * oder noch liegende Coins visuell darzustellen.
     *
     * @param drawTool das {@link DrawTool}, das zum Zeichnen der Coins verwendet wird
     */

    private void drawCoinDrops(DrawTool drawTool) {
        for (CoinDrop c : coinDrops) c.draw(drawTool);
    }


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
                    startScene3();
                    teleportPlayer(200, 650);
                }

                handleCoinPickup();
                break;

            case 3:

                player.update(dt);
                handleMovement(dt);

                updateScene3Enemies(dt);
                handleScene3EnemyHitsPlayer();
                handleScene3PlayerHitsEnemies();

                handleScene3CoinDrops();
                handleCoinPickup();
                scene3Cleared = isScene3Cleared();
                if (scene3Cleared && collisions.rectangleCollisions(player, scene3ExitGate)) {
                    switchScene(2);
                    teleportPlayer(350, 600);
                }
                if(player.getHP() <= 0) scene = 4;

                break;

            case 4:
                deathscreen.update(dt);
                break;
        }


        if (scene == 3 || scene == 4) {
            handleScene4CoinDrops();
        }
    }



    private void teleportPlayer(double x, double y) {
        player.setXpos(x);
        player.setYpos(y);


        wDown = aDown = sDown = dDown = false;
        inventoryOpen = false;
    }


    /**
     * Behandelt Treffer eines Gegners auf den Spieler.
     * <p>
     * Die Methode prüft zunächst, ob der Gegner aktuell Schaden verursachen darf
     * und ob der Spieler verwundbar ist. Trifft die Angriffs-Hitbox des Gegners
     * den Spieler, wird dem Spieler Schaden zugefügt und ein Rückstoß (Knockback)
     * angewendet.
     *
     * Besonderheiten:
     * <ul>
     *   <li>Ist der Spieler unverwundbar, wird der Angriff ignoriert.</li>
     *   <li>Der Rückstoß erfolgt ausschließlich auf der X-Achse.</li>
     *   <li>Boss-Gegner verursachen einen stärkeren Knockback.</li>
     *   <li>Die Spielerposition wird nach dem Rückstoß auf die Szenengrenzen begrenzt.</li>
     * </ul>
     *
     * Nach einem erfolgreichen Treffer wird der Angriff des Gegners als
     * abgeschlossen markiert, sodass pro Angriff nur ein Treffer möglich ist.
     *
     * @param e der Gegner, dessen Angriff auf den Spieler geprüft wird
     */

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


            double knockback = (e instanceof Bosslehrer) ? 200 : 80;

            double kx = player.getCenterX() - e.getCenterX();
            if (kx == 0) kx = 1;
            else kx = kx / Math.abs(kx);

            double newX = player.getXpos() + kx * knockback;
            double newY = player.getYpos();

            newX = Math.max(0, Math.min(newX, getSceneMaxX() - player.getWidth()));
            newY = Math.max(0, Math.min(newY, getSceneMaxY() - player.getHeight()));

            player.setXpos(newX);
            player.setYpos(newY);

            e.markHitDone();
        }
    }


    /**
     * Verarbeitet die Bewegung des Spielers basierend auf Tastatureingaben.
     * <p>
     * Die Methode liest den aktuellen Status der Bewegungs-Tasten (W, A, S, D)
     * aus und berechnet daraus eine framerate-unabhängige Bewegung mithilfe
     * der Delta Time ({@code dt}). Zusätzlich wird die Blickrichtung des
     * Spielers sowie der Animationsstatus gesetzt.
     *
     * Besonderheiten:
     * <ul>
     *   <li>Ist das Inventar geöffnet, wird keine Bewegung verarbeitet.</li>
     *   <li>Die Bewegung erfolgt getrennt auf der X- und Y-Achse.</li>
     *   <li>Nach jeder Bewegung wird geprüft, ob der Spieler mit Wänden kollidiert.</li>
     *   <li>Bei einer Kollision wird die Bewegung auf der entsprechenden Achse rückgängig gemacht.</li>
     *   <li>Die Spielerposition wird auf die aktuellen Szenengrenzen begrenzt.</li>
     * </ul>
     *
     * Diese Methode wird im {@code update}-Zyklus des Controllers aufgerufen
     * und bildet die Grundlage für das Bewegungs- und Kollisionssystem
     * des Spielers.
     *
     * @param dt Delta Time (Zeit seit dem letzten Frame), um eine
     *           gleichmäßige Bewegung unabhängig von der Framerate zu gewährleisten
     */

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


    /**
     * Erzeugt einen neuen Coin-Drop an einer bestimmten Position.
     * <p>
     * Der Coin wird zentriert um die übergebene Position erzeugt,
     * typischerweise an der Stelle, an der ein Gegner besiegt wurde.
     * Die Größe des Coins wird berücksichtigt, sodass der Drop
     * optisch mittig erscheint.
     *
     * Der neu erstellte {@link CoinDrop} wird der internen Sammlung
     * der aktiven Coin-Drops hinzugefügt und kann anschließend
     * vom Spieler eingesammelt werden.
     *
     * @param centerX die X-Koordinate des Zentrums, an dem der Coin erscheinen soll
     * @param centerY die Y-Koordinate des Zentrums, an dem der Coin erscheinen soll
     * @param amount  der Wert des Coin-Drops (Anzahl der Coins)
     */

    private void spawnCoinDrop(double centerX, double centerY, int amount) {
        coinDrops.add(new CoinDrop(centerX - CoinDrop.SIZE
                / 2.0, centerY - CoinDrop.SIZE / 2.0, amount));
    }

    /**
     * Behandelt das Aufsammeln von Coin-Drops durch den Spieler.
     * <p>
     * Die Methode durchläuft alle aktuell vorhandenen {@link CoinDrop}-Objekte
     * und prüft, ob deren Hitbox die Hitbox des Spielers schneidet.
     * Bei einer Kollision wird:
     * <ul>
     *   <li>der Coin-Wert dem Inventar des Spielers hinzugefügt</li>
     *   <li>der Coin aus der Liste der aktiven Drops entfernt</li>
     * </ul>
     *
     * Die Iteration erfolgt über einen {@link Iterator}, um das sichere
     * Entfernen von Coins während des Durchlaufens der Sammlung zu ermöglichen.
     * Diese Methode wird regelmäßig im Update-Zyklus des Spiels aufgerufen.
     */

    private void handleCoinPickup() {
        Iterator<CoinDrop> it = coinDrops.iterator();
        while (it.hasNext()) {
            CoinDrop c = it.next();


            if (c.getHitBox().intersects(player.getHitBox())) {
                inventory.addCoins(c.getValue());
                it.remove();
            }
        }
    }
    /**
     * Verarbeitet Coin-Drops für Gegner in Szene 3.
     * <p>
     * Die Methode überprüft alle Gegner der dritten Szene und erzeugt
     * einen Coin-Drop, sobald ein Gegner besiegt wurde.
     * Um zu verhindern, dass ein Gegner mehrfach Coins fallen lässt,
     * wird eine separate Sammlung ({@code alreadyDropped}) verwendet,
     * in der bereits abgehandelte Gegner gespeichert werden.
     *
     * Für jeden neu besiegten Gegner wird eine zufällige Anzahl
     * an Coins erzeugt und an der Position des Gegners gespawnt.
     *
     * Diese Methode wird typischerweise im Update-Zyklus der Szene 3
     * aufgerufen und ist Teil des Belohnungssystems des Spiels.
     */

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

    /**
     * Verarbeitet Coin-Drops für Gegner in Szene 4.
     * <p>
     * Die Methode überprüft alle Gegner aus den primären und sekundären
     * Gegner-Arrays der vierten Szene. Sobald ein Gegner besiegt wurde,
     * wird ein Coin-Drop an dessen Position erzeugt.
     *
     * Um Mehrfach-Drops desselben Gegners zu verhindern, wird die
     * {@code alreadyDropped}-Sammlung verwendet, in der bereits
     * berücksichtigte Gegner gespeichert werden.
     *
     * Für jeden neu besiegten Gegner wird eine zufällige Anzahl an Coins
     * generiert und in der Szene gespawnt.
     *
     * Die Methode ist robust gegenüber {@code null}-Referenzen und kann
     * auch dann sicher ausgeführt werden, wenn nur eines der Gegner-Arrays
     * initialisiert ist.
     */

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




    private void startScene3() {
        scene3Run++;

        scene3Enemies.clear();
        scene3Cleared = false;


        alreadyDropped.clear();

        int total = scene3BaseEnemies + (scene3Run - 1) * scene3MorePerRun;
        spawnScene3EnemiesMixed(total);
    }

    /**
     * Spawnt eine gemischte Gegnergruppe für Szene 3.
     * <p>
     * Die Methode erzeugt eine bestimmte Anzahl von Gegnern und platziert sie
     * an zufälligen Positionen innerhalb der erlaubten Koordinaten der Szene.
     * Dabei wird zwischen verschiedenen Gegnertypen unterschieden:
     * <ul>
     *   <li>{@link Kind}: wird mit einer steigenden Wahrscheinlichkeit gespawnt</li>
     *   <li>{@link Dieb}: dient als Standardgegner</li>
     * </ul>
     *
     * Die Wahrscheinlichkeit für {@code Kind}-Gegner erhöht sich mit jeder
     * weiteren Runde (Run) der Szene, ist jedoch durch einen Maximalwert
     * begrenzt. Dadurch steigt der Schwierigkeitsgrad progressiv.
     *
     * Vor dem Spawnen wird die bestehende Gegnerliste der Szene 3 vollständig
     * geleert.
     *
     * @param count die Anzahl der Gegner, die in Szene 3 gespawnt werden sollen
     */

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


    /**
     * Erzeugt einen zufälligen {@code double}-Wert innerhalb eines gegebenen Bereichs.
     * <p>
     * Die Methode liefert einen Zufallswert zwischen den beiden übergebenen
     * Grenzen {@code a} und {@code b}. Dabei ist die Reihenfolge der Parameter
     * unerheblich, da automatisch die kleinere und größere Grenze bestimmt wird.
     *
     * Diese Methode wird typischerweise verwendet, um zufällige Positionen
     * oder Werte innerhalb eines definierten Bereichs zu berechnen,
     * z.B. beim Spawnen von Gegnern.
     *
     * @param a eine Grenze des Wertebereichs
     * @param b die andere Grenze des Wertebereichs
     * @return ein zufälliger Wert im Bereich {@code [min(a, b), max(a, b))}
     */

    private double randomRange(double a, double b) {
        return ThreadLocalRandom.current().nextDouble(Math.min(a, b), Math.max(a, b));
    }



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

            SwingUI ui = new SwingUI(storytomole);

            frame.setContentPane(ui.outputField);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setPreferredSize(new Dimension(600, 120));
            frame.pack();


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


            if (collisions.rectangleCollisions(player, storytomole)) {
                storytomole.speak();
                openStoryWindow();
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


    /**
     * Öffnet bei aktivem Inventar die passende Swing-Oberfläche
     * für das aktuell mit der Maus überhoverte Inventarfeld.
     * <p>
     * Die Methode prüft zunächst, ob das Inventar geöffnet ist.
     * Anschließend wird anhand der Mausposition ermittelt, ob sich
     * der Mauszeiger über dem Waffen- oder dem Potion-Feld befindet.
     *
     * Je nach Feld wird:
     * <ul>
     *   <li>die aktuell verfügbaren Items aus dem Inventar gesammelt</li>
     *   <li>das derzeitige Loadout geladen</li>
     *   <li>eine Swing-UI zum Bearbeiten des jeweiligen Loadouts geöffnet</li>
     * </ul>
     *
     * Gibt die Swing-UI ein bearbeitetes Loadout zurück, wird dieses
     * im Inventar übernommen. Wird {@code null} zurückgegeben,
     * bleibt das Loadout unverändert.
     *
     * Die Methode dient als Brücke zwischen der Ingame-UI
     * und den externen Swing-Dialogen zur Loadout-Verwaltung.
     */

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
    /**
     * Sammelt alle Waffen aus dem Inventar des Spielers.
     * <p>
     * Die Methode durchläuft die interne KAGO-Datenstruktur des Inventars
     * und filtert alle enthaltenen Items nach dem Typ {@link Weapons}.
     * Gefundene Waffen werden in eine neue {@link java.util.ArrayList}
     * übernommen.
     *
     * Diese Methode wird typischerweise verwendet, um eine Übersicht
     * aller verfügbaren Waffen für Loadout-Auswahl oder Swing-Dialoge
     * bereitzustellen.
     *
     * @return eine {@link java.util.List} mit allen Waffen,
     *         die sich aktuell im Inventar befinden
     */

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
    /**
     * Sammelt alle Tränke aus dem Inventar des Spielers.
     * <p>
     * Die Methode durchläuft die interne KAGO-Datenstruktur des Inventars
     * und filtert alle enthaltenen Items nach dem Typ {@link Potions}.
     * Jeder gefundene Trank wird in eine neue {@link java.util.ArrayList}
     * übernommen.
     *
     * Diese Methode wird typischerweise verwendet, um eine Übersicht
     * aller verfügbaren Tränke bereitzustellen, z.B. für Loadout-Auswahl
     * oder Swing-basierte Inventar-Dialoge.
     *
     * @return eine {@link java.util.List} mit allen Tränken,
     *         die sich aktuell im Inventar befinden
     */

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
