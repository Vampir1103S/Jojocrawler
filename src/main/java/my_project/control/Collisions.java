package my_project.control;

import my_project.model.Entities.Entity;

public class Collisions {
    public Collisions() {

    }
    public boolean calculateColisions(Entity E1, Entity E2) {

        if(Math.max(E1.getXpos(), E2.getXpos()) - Math.min(E1.getXpos() + E1.getWidth(), E2.getXpos() + E2.getWidth()) < 0 ) {
            return true;


        }else if(Math.max(E1.getYpos(), E2.getYpos()) - Math.min(E1.getYpos() + E1.getWidth(), E2.getYpos() + E2.getWidth()) < 0 ) {
            return true;
        }
        return false;
    }
}
