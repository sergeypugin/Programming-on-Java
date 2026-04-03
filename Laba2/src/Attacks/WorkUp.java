package Attacks;
import ru.ifmo.se.pokemon.*;

public final class WorkUp extends StatusMove {
    public WorkUp() {
        super(Type.NORMAL, 0, 0);
    }
    @Override
    protected String describe() {
        return "использует Work Up";
    }
}
