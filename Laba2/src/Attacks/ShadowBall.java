package Attacks;
import CheckedAttackTypes.CheckedSpecialMove;
import ru.ifmo.se.pokemon.*;

public final class ShadowBall extends CheckedSpecialMove {
    public ShadowBall() {
        super(Type.GHOST, 80, 100);
    }
    @Override
    protected String describe() {
        return "использует Shadow Ball";
    }
}
