package my_project.model.map;

import KAGO_framework.view.DrawTool;

import java.awt.image.BufferedImage;

public abstract class Level extends Environment {

    protected int enemyAmount;
    protected int enemySpawnX;
    protected int enemySpawnY;
    protected int Wave;
    protected double statMultiplicator = Wave /2;

    protected Level(int enemyAmount, double statMultiplicator, int enemySpawnX, int enemySpawnY){}

    public void draw(DrawTool drawTool) {
        this.enemySpawnX = (int) (Math.random() * 1800) + 1;  // 1–1800
        this.enemySpawnY = (int) (Math.random() * 801);// 0–800

    }


    public int getEnemyAmount() {return enemyAmount;}
    public double getStatMultiplicator() {return statMultiplicator;}
    public int getEnemySpawnX() {return enemySpawnX;}
    public int getEnemySpawnY() {return enemySpawnY;}


}
