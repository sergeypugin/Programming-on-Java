package Attacks;
import CheckedAttackTypes.CheckedPhysicalMove;
import ru.ifmo.se.pokemon.*;

public final class WildCharge extends CheckedPhysicalMove {
    public WildCharge() {
        super(Type.ELECTRIC, 90, 100);
    }
    @Override
    protected void applySelfDamage(final Pokemon p, final double damage) {
        // Атакующий получает отдачу 1/4 от нанесенного урона
        p.setMod(Stat.HP, (int) Math.round(damage / 4));
    }
    @Override
    protected String describe() {
        return "использует Wild Charge и получает отдачу";
    }
}