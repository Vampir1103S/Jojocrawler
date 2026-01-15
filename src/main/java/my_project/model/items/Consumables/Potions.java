package my_project.model.items.Consumables;

public abstract class Potions extends Consumable {

    protected int amount;

    public Potions(int amount) {
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
