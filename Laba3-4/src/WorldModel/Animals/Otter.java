package WorldModel.Animals;

import WorldModel.Exceptions.CheckedException;
import WorldModel.Exceptions.UncheckedException;
import WorldModel.Animal;
import WorldModel.Interfaces.Predator;
import WorldModel.PhysicalObject;
import WorldModel.Animals.Fox.PawInjury;

import java.util.Random;

public final class Otter extends Animal implements Predator {
    public Otter(String name) {
        super(name);
    }

    public void complain(String message) {
        System.out.printf("%s заскулил(-а): \"%s\"\n", name, message);
    }

    @Override
    public void hunt(PhysicalObject target) throws CheckedException {
        System.out.printf("... %s пытается схватить гуся из стаи...\n", name);
        if (new Random().nextDouble() < 0.6) {
            PawInjury newInjury = new PawInjury("перепонка изрезана кровавыми клочьями", 8);
            this.setInjury(newInjury);
            throw new CheckedException("Да вдруг точно острая колючка вонзилась ему(ей) в лапу!", name);
        } else {
            System.out.printf("%s не получил(-а) травму, но не смогл(-а) поймать гуся%s.\n", name, target.getName());
        }
    }
}