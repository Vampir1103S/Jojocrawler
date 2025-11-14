package my_project.model.Entities;

abstract class Entity {
    protected double hp;
    protected int speed;
    protected double stamina;
    protected int defense;
    protected String Name;

    public Entity(double hp, int speed, double stamina, int defense, String Name) {
        this.hp = hp;
        this.speed = speed;
        this.stamina = stamina;
        this.defense = defense;
        this.Name = Name;

    }

    public double getHP(){
        return hp;
    }

    public void setHP(double hp){
        this.hp = hp;
    }

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public double getStamina(){
        return stamina;
    }

    public void setStamina(double stamina){
        this.stamina = stamina;
    }

    public int getDefense(){
        return defense;
    }

    public void setDefense(int defense){
        this.defense = defense;
    }

    public String getName(){
        return Name;
    }


}

