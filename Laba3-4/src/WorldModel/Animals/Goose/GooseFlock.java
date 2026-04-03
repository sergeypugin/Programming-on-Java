package WorldModel.Animals.Goose;

import WorldModel.PhysicalObject;
import WorldModel.Exceptions.UncheckedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GooseFlock {
    private final List<Goose> geese;

    public GooseFlock(List<Goose> Geese) {
        this.geese = new ArrayList<>(Geese);
    }

    public void flyTo(PhysicalObject destination) throws UncheckedException {
        System.out.printf("Стая из %d гусей летит в сторону %s.\n", geese.size(), destination.getName());
        double d = 49 + new Random().nextDouble() * 10;
        for (Goose goose : geese) {
            goose.fly(d);
        }
    }

    public List<Goose> getGeese() {
        return geese;
    }
}
