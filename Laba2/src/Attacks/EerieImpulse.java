package Attacks;
import ru.ifmo.se.pokemon.*;

public final class EerieImpulse extends StatusMove{
    public EerieImpulse(){
        super(Type.ELECTRIC,0,100);
    }
    @Override
    protected String describe() {
        return "использвует Eerie Impulse";
    }
}
