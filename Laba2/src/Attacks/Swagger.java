package Attacks;
import ru.ifmo.se.pokemon.*;

public final class Swagger extends StatusMove {
    public Swagger() {
        super(Type.NORMAL, 0, 90);
    }
    @Override
    protected void applyOppEffects(final Pokemon p) {
        // Повышает атаку противника на 2 ступени и вызывает замешательство
        p.setMod(Stat.ATTACK, 2);
        Effect.confuse(p);
    }
    @Override
    protected String describe() {
        return "использует Swagger, повышает атаку противника на 2 ступени и вызывает замешательство";
    }
}
