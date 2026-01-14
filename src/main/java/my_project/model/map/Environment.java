package my_project.model.map;

import KAGO_framework.model.GraphicalObject;

public abstract class Environment extends GraphicalObject {

    protected boolean nachRechts;

    public double getHitboxWidth() {
        return 0;
    }

    public double getHitboxX() {
        return 0;
    }

    public double getHitboxHeight() {
        return 0;
    }

    public double getHitboxY() {
        return 0;
    }

    //break Blocks
    public double getHitboxXb() {
        return 0;
    }

    public double getHitboxHeightb() {
        return 0;
    }

    public double getHitboxYb() {
        return 0;
    }

    public double getHitboxWidthb() {
        return 0;
    }

    public void setNachRechts(boolean nachRechts) {
        this.nachRechts = nachRechts;
    }

    public boolean getNachRechts() {
        return nachRechts;
    }

}
