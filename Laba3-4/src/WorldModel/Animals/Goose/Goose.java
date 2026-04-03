package WorldModel.Animals.Goose;

import WorldModel.Animal;

public class Goose extends Animal {
    public Goose(String name) {
        super(name);
    }

    public void honk() {
        System.out.printf("Гусь %s загоготал\n", name);
    }

    public void honk(String reason) {
        System.out.printf("Гусь %s тревожно загоготал, потому что услышал %s\n", name, reason);
    }
}