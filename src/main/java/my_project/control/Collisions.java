package my_project.control;

import my_project.model.Entities.Entity;

import static com.sun.javafx.util.Utils.clamp;

public class Collisions {
    public Collisions() {

    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public boolean rectangleCollisions(Entity e1, Entity e2) {

        if (e1 == null || e2 == null) {
            return false;
        }

        boolean collisionX =
                e1.getXpos() < e2.getXpos() + e2.getWidth() &&
                        e1.getXpos() + e1.getWidth() > e2.getXpos();

        boolean collisionY =
                e1.getYpos() < e2.getYpos() + e2.getHeight() &&
                        e1.getYpos() + e1.getHeight() > e2.getYpos();

        return collisionX && collisionY;
    }

    public boolean rectangleCircleCollision(Entity rect, Entity circle) {


        double circleX = circle.getXpos();
        double circleY = circle.getYpos();
        double radius  = circle.getRadius();


        double closestX = clamp(circleX,
                rect.getXpos(),
                rect.getXpos() + rect.getWidth());

        double closestY = clamp(circleY,
                rect.getYpos(),
                rect.getYpos() + rect.getHeight());


        double distanceX = circleX - closestX;
        double distanceY = circleY - closestY;


        return (distanceX * distanceX + distanceY * distanceY)
                <= radius * radius;
    }

}
