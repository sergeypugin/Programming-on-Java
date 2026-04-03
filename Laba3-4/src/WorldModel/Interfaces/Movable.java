package WorldModel.Interfaces;

import WorldModel.Exceptions.UncheckedException;

public interface Movable {
     void run(double distance) throws UncheckedException;
     void fly(double distance) throws UncheckedException;
     void swim(double distance) throws UncheckedException;
     void walk(double distance) throws UncheckedException;

    String getName();
}
