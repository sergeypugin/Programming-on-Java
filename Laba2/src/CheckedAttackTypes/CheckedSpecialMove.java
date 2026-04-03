package CheckedAttackTypes;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;

public abstract class CheckedSpecialMove extends SpecialMove {
    public CheckedSpecialMove(Type type, double power, double accuracy) {
        super(type, power, accuracy);
    }
    @Override
    protected void applyOppDamage(Pokemon defender, double damage) {
        double hpBefore = defender.getHP();
        super.applyOppDamage(defender, damage);
        checkWeakDamage(defender, hpBefore);
    }
    protected void checkWeakDamage(Pokemon defender, double hpBefore) {
        double damage = hpBefore - defender.getHP();;
        if (damage>0 && damage<hpBefore/5) {
            System.out.println("Слабак! Снёс всего " + damage + " из " + hpBefore + " HP с противника");
        }
    }
}