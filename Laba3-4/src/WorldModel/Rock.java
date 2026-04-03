package WorldModel;

import WorldModel.PhysicalObject;

public final class Rock extends PhysicalObject {
    private final String description;
    private final boolean isSafe = true;

    public Rock(String name, String description) {
        super(name);
        this.description = description;
    }

    @Override
    public void move(double distance) {
        System.out.printf("Попытка переместить %s - безуспешна.\n", name);
    }

    @Override
    public String toString() {
        return String.format("%s [Описание: %s, Надежность: %b]", name, description, isSafe);
    }
}