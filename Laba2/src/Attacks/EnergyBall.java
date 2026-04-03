package Attacks;
import CheckedAttackTypes.CheckedSpecialMove;
import ru.ifmo.se.pokemon.*;

public final class EnergyBall extends CheckedSpecialMove {
    public EnergyBall(){
        super(Type.GRASS,90,100);
    }
    @Override
    protected void applyOppEffects(final Pokemon p) {
        // С вероятностью 10% снижает специальную защиту противника на 1 уровень
        if (Math.random() <= 0.1) p.setMod(Stat.SPECIAL_DEFENSE, 1);
    }
    @Override
    protected String describe() {
        return "использует Energy Ball и вероятностью 10% снижает специальную защиту противника на 1 уровень";
    }
}