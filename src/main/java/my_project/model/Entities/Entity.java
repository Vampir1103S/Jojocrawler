package my_project.model.Entities;

public abstract class Entity {
    protected double xpos;
    protected double ypos;
    protected double hp;
    protected double speed;
    protected double stamina;
    protected int defense;
    protected String Name;
    protected double width;
    protected double height;

    public Entity(double xpos , double ypos,double hp, double speed, double stamina, int defense, String Name,  double width, double height) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.hp = hp;
        this.speed = speed;
        this.stamina = stamina;
        this.defense = defense;
        this.Name = Name;
        this.width = width;
        this.height = height;

    }


    public double getXpos() {return xpos;}

    public void setXpos(int xpos) {this.xpos = xpos;}

    public double getYpos() {return ypos;}

    public void setYpos(int ypos) {this.ypos = ypos;}

    public double getHP(){
        return hp;
    }

    public void setHP(double hp){
        this.hp = hp;
    }

    public double getSpeed(){
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

    public double getWidth(){ return width; }

    public double getHeight(){ return height; }




}

