package WorldModel;

import WorldModel.Exceptions.UncheckedException;
import WorldModel.Interfaces.Movable;

import java.util.Objects;

public abstract class PhysicalObject implements Movable {
    protected String name;
    protected MovementType currentMovement;

    public String getName() {
        return name;
    }

    public PhysicalObject(String name) {
        this.name = name;
    }

    public abstract void move(double distance) throws UncheckedException;

    @Override
    public void run(double distance) throws UncheckedException {
        currentMovement = MovementType.RUNNING;
        move(distance);
    }

    @Override
    public void fly(double distance) throws UncheckedException {
        currentMovement = MovementType.FLYING;
        move(distance);
    }

    @Override
    public void swim(double distance) throws UncheckedException {
        currentMovement = MovementType.SWIMMING;
        move(distance);
    }

    @Override
    public void walk(double distance) throws UncheckedException {
        currentMovement = MovementType.WALKING;
        move(distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        PhysicalObject that = (PhysicalObject) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
