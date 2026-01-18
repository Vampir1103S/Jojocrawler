package my_project.control;

import my_project.model.Entities.Entity;
import my_project.model.map.Environment;

public class Collisions {

    public Collisions() {}

    // ===============================
    // Entity ↔ Entity
    // ===============================
    public boolean rectangleCollisions(Entity e1, Entity e2) {
        if (e1 == null || e2 == null) return false;

        return e1.getXpos() < e2.getXpos() + e2.getWidth() &&
                e1.getXpos() + e1.getWidth() > e2.getXpos() &&
                e1.getYpos() < e2.getYpos() + e2.getHeight() &&
                e1.getYpos() + e1.getHeight() > e2.getYpos();
    }

    // ===============================
    // Entity ↔ Environment (Baum etc.)
    // ===============================
    public boolean rectangleCollisions(Entity e, Environment env) {
        if (e == null || env == null) return false;

        return e.getXpos() < env.getHitboxX() + env.getHitboxWidth() &&
                e.getXpos() + e.getWidth() > env.getHitboxX() &&
                e.getYpos() < env.getHitboxY() + env.getHitboxHeight() &&
                e.getYpos() + e.getHeight() > env.getHitboxY();
    }

    // ===============================
    // ✅ NEU: Entity ↔ Rechteck (x, y, w, h)
    // ===============================
    public boolean rectangleCollisions(
            Entity e,
            double rx, double ry,
            double rWidth, double rHeight
    ) {
        if (e == null) return false;

        return e.getXpos() < rx + rWidth &&
                e.getXpos() + e.getWidth() > rx &&
                e.getYpos() < ry + rHeight &&
                e.getYpos() + e.getHeight() > ry;
    }
}
