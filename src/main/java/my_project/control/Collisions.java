package my_project.control;

import my_project.model.Entities.Entity;

public class Collisions {
    public Collisions() {

    }
    public boolean calculateColisions(Entity E1, Entity E2) {
        boolean stateX1;
        boolean stateY1;
        boolean stateX2;
        boolean stateY2;
        stateX1 = false;
        stateY1 = false;
        stateX2 = false;
        stateY2 = false;

//        zahl2 = Math.max(E1.getYpos(), E2.getYpos()) - Math.min(E1.getYpos() + E1.getHeight(), E2.getYpos() + E2.getHeight());
     //   System.out.println("zahl1: " + zahl1);

        if(E1.getXpos() < E2.getXpos() ) {
           if((E2.getXpos() - (E1.getWidth()) + E1.getXpos()) == 0) {
               stateX1 = true;
           }else stateX1 = false;
        }
        if(E1.getXpos() > E2.getXpos() ) {
            if((E1.getXpos() - (E2.getWidth()) + E2.getXpos()) == 0) {
                stateX2 = true;
            }else stateX2 = false;
        }



        if(E1.getYpos() < E2.getYpos() ) {
            if((E2.getYpos() - (E1.getHeight()) + E1.getYpos()) == 0) {
                stateY1 = true;
            }else stateY1 = false;
        }
        if(E1.getYpos() > E2.getYpos() ) {
            if ((E1.getYpos() + E1.getHeight()) - E2.getYpos() == 0) {
                stateY2 = true;
            }else stateY2 = false;
        }
        System.out.println(stateX1 + " " + stateY1 + " " + stateX2 + " " + stateY2);
        if(stateX1 && stateY1 || stateX2 && stateY2 || stateX1 && stateY2 || stateX2 && stateY1) {
            return true;
        }
        return false;



//        zahl1 = Math.max((int)E1.getXpos() + (int)E1.getWidth() + (int)E2.getWidth(), (int)E2.getXpos()) - (int)Math.min(E1.getXpos() , (int)E2.getXpos());
//        zahl2 = Math.max(E1.getYpos(), E2.getYpos()) - Math.min(E1.getYpos() + E1.getHeight(), E2.getYpos() + E2.getHeight());
//        System.out.println("zahl1: " + zahl1);
//
//        if(Math.max(E1.getXpos(), E2.getXpos()) - Math.min(E1.getXpos() + E1.getWidth(), E2.getXpos() + E2.getWidth()) <= 0 ) {
//            return true;
//
//
//        }else if(Math.max(E1.getYpos(), E2.getYpos()) - Math.min(E1.getYpos() + E1.getHeight(), E2.getYpos() + E2.getHeight()) <= 0 ) {
//            return true;
//        }
        //return false;
    }
}
