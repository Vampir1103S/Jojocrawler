package my_project.model.items;

public abstract class Item {
    public String getDisplayName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
