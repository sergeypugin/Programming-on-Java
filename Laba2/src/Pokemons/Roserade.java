package Pokemons;
import ru.ifmo.se.pokemon.*;
import Attacks.*;

public final class Roserade extends Pokemon{
    public Roserade(String name, int level){
        super(name,level);
        setType(Type.GRASS,Type.POISON);
        setStats(60,70,65,125,105,90);
        setMove(new EnergyBall(), new Swagger(),new BulletSeed(), new Swagger());
    }
}