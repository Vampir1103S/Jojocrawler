package my_project.model.Entities;

abstract class Boss extends Enemy{
    public Boss(double hp, int speed, double stamina, int defense, String Name) {
        super(hp, speed, stamina, defense, Name);
    }
}
