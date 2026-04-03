package Pokemons;
import ru.ifmo.se.pokemon.*;
import Attacks.*;

public final class Girafarig extends Pokemon {
    public Girafarig(String name, int level) {
        super(name, level);
        setType(Type.NORMAL, Type.PSYCHIC);
        setStats(70, 80, 65, 90, 65, 85);
        setMove(new Growl(), new ChargeBeam(), new ShadowBall(), new WorkUp());
    }
}