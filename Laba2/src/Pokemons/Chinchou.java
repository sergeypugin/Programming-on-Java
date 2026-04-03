package Pokemons;
import ru.ifmo.se.pokemon.*;
import Attacks.*;

public final class Chinchou extends Pokemon{
    public Chinchou(String name, int level){
        super(name,level);
        setType(Type.WATER,Type.ELECTRIC);
        setStats(75,38,38,56,56,67);
        setMove(new WildCharge(), new Swagger(), new Thunderbolt());
    }
}
