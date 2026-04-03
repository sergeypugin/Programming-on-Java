package WorldModel.Interfaces;

import WorldModel.Exceptions.CheckedException;
import WorldModel.PhysicalObject;

public interface Predator {
    default void hunt(PhysicalObject target) throws CheckedException{};
}