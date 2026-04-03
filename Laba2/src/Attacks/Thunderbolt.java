package Attacks;
import CheckedAttackTypes.CheckedSpecialMove;
import ru.ifmo.se.pokemon.*;

public final class Thunderbolt extends CheckedSpecialMove {
    public Thunderbolt() {
        super(Type.ELECTRIC, 90, 100);
    }
    @Override
    protected void applyOppEffects(final Pokemon p) {
        // 10% шанс парализовать противника
        if (Math.random() <= 0.1) Effect.paralyze(p);
    }
    @Override
    protected String describe() {
        return "использует Thunderbolt и с вероятностью 10% парализует противника";
    }
}