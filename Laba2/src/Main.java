// https://wiki.pokemonrevolution.net
import Pokemons.*;
import ru.ifmo.se.pokemon.*;
public class Main {
    public static void main(String[] args) {
        Battle b = new Battle();
        Pokemon p1 = new Girafarig("Girafarig", 1);
        Pokemon p2 = new Chinchou("Chinchou", 1);
        Pokemon p3 = new Lanturn("Lanturn", 1);
        Pokemon p4 = new Budew("Budew", 1);
        Pokemon p5 = new Roselia("Roselia", 1);
        Pokemon p6 = new Roserade("Roserade", 1);
        b.addAlly(p1); b.addAlly(p2); b.addAlly(p3);
        b.addFoe(p4); b.addFoe(p5); b.addFoe(p6);
        b.go();
    }
}
