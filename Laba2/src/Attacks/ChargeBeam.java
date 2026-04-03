package Attacks;
import CheckedAttackTypes.CheckedSpecialMove;
import ru.ifmo.se.pokemon.*;

public final class ChargeBeam extends CheckedSpecialMove {
    public ChargeBeam() {
        super(Type.ELECTRIC, 50, 90);
    }
    @Override
    protected void applySelfEffects(final Pokemon p) {
        // С вероятностью 70% повышаем свою специальную атаку на 1 ступень
        if (Math.random() <= 0.7) p.setMod(Stat.SPECIAL_ATTACK, 1);
    }
    @Override
    protected String describe() {
        return "использвует Charge Beam и с вероятностью 70% повышаем свою специальную атаку на 1 ступень";
    }
}