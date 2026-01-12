package my_project.control;

import my_project.model.Entities.Entity;
import my_project.model.map.Baum;
import my_project.model.map.Environment;

public class Collisions {

    public Collisions() {}

    public boolean rectangleCollisions(Entity e1, Entity e2) {
        if (e1 == null || e2 == null) return false;

        return e1.getXpos() < e2.getXpos() + e2.getWidth() &&
                e1.getXpos() + e1.getWidth() > e2.getXpos() &&
                e1.getYpos() < e2.getYpos() + e2.getHeight() &&
                e1.getYpos() + e1.getHeight() > e2.getYpos();
    }

    // ✅ Entity ↔ Baum (BufferedImage-Hitbox)
    public boolean rectangleCollisions(Entity e, Environment b) {
        if (e == null || b == null) return false;

        return e.getXpos() < b.getHitboxX() + b.getHitboxWidth() &&
                e.getXpos() + e.getWidth() > b.getHitboxX() &&
                e.getYpos() < b.getHitboxY() + b.getHitboxHeight() &&
                e.getYpos() + e.getHeight() > b.getHitboxY();
    }
}
