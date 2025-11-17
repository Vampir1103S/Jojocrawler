package my_project.model.Entities;

abstract class Boss extends Enemy{
    public Boss(int xpos,int ypos,double hp, int speed, double stamina, int defense, String Name) {
        super(xpos, ypos, hp, speed, stamina, defense, Name);
    }
}
