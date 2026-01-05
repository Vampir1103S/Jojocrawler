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

    public void update(double dt) {
        if (controller == null) return;

        double dx = controller.followPlayerX(this);
        double dy = controller.followPlayerY(this);

        double dist = Math.sqrt(dx * dx + dy * dy);

        double stopDistance = 5.0;   // damit er nicht jittert
        if (dist < stopDistance) return;

        // Richtung normalisieren (Länge = 1)
        double dirX = dx / dist;
        double dirY = dy / dist;

        double move = dt * (100 * speed);

        xpos += dirX * move;
        ypos += dirY * move;

    }
    public static void setController(Controller con) {
        controller = con;
    }

}

