package my_project.model.Entities;
import KAGO_framework.view.DrawTool;
import my_project.model.Entities.Player;
import my_project.control.Controller;

public abstract class Enemy extends Entity {
    private int direction;
    protected static Controller controller;


    public Enemy(double xpos, double ypos, double hp, double speed, double stamina, int defense, String Name, double width, double height) {
        super(xpos, ypos, hp, speed, stamina, defense, Name, width, height);


    }


    public void draw(DrawTool drawTool) {

    }

    @Override
    public void update(double dt) {
        if (controller == null) return;

        // Ziel = MITTE des Players
        double targetX = controller.getPlayer().getCenterX();
        double targetY = controller.getPlayer().getCenterY();

        // Gegner-Mitte (damit er wirklich "mittig" auf den Player zugeht)
        double myX = this.getCenterX();
        double myY = this.getCenterY();

        double dx = targetX - myX;
        double dy = targetY - myY;

        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        double move = dt * (100 * speed);

        // Wenn wir in diesem Frame "drüber hinaus" laufen würden: direkt auf Ziel setzen
        if (dist <= move) {
            // xpos/ypos sind top-left, also Mitte zurückrechnen
            xpos = targetX - this.getWidth() / 2.0;
            ypos = targetY - this.getHeight() / 2.0;
            return;
        }

        // Normalisierte Richtung
        double dirX = dx / dist;
        double dirY = dy / dist;

        xpos += dirX * move;
        ypos += dirY * move;
    }
    public static void setController(Controller con) {
        controller = con;
    }

}

