package Pokemons;
import ru.ifmo.se.pokemon.*;
import Attacks.*;

public final class Lanturn extends Pokemon{
    public Lanturn(String name, int level){
        super(name,level);
        setType(Type.WATER,Type.ELECTRIC);
        setStats(125,58,58,76,76,67);
        setMove(new WildCharge(), new Swagger(), new Thunderbolt(), new EerieImpulse());
    }
}
