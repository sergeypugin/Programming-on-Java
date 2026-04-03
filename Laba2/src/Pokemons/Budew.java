package Pokemons;
import ru.ifmo.se.pokemon.*;
import Attacks.*;

public final class Budew extends Pokemon{
    public Budew(String name, int level){
        super(name,level);
        setType(Type.GRASS,Type.POISON);
        setStats(40,30,35,50,70,55);
        setMove(new EnergyBall(), new Swagger());
    }
}