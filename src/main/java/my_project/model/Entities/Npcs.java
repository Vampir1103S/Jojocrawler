package my_project.model.Entities;

public abstract class Npcs extends Entity{
    public Npcs(int xpos ,int ypos, double hp, int speed, double stamina, int defense, String Name, double width, double height) {
        super(xpos, ypos, hp, speed, stamina, defense, Name,  width, height);

    }
}