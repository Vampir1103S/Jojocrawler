package my_project.control;

import my_project.model.Entities.Entity;

public class Collisions {
    public Collisions() {

    }
    public boolean calculateColisions(Entity e1, Entity e2) {
        return e1.getXpos() < e2.getXpos() + e2.getWidth() &&
             e1.getXpos() + e1.getWidth() > e2.getXpos() &&
                e1.getYpos() < e2.getYpos() + e2.getHeight() &&
                e1.getYpos() + e1.getHeight() > e2.getYpos();

    }
}
