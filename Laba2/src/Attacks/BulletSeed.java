package Attacks;
import CheckedAttackTypes.CheckedPhysicalMove;
import ru.ifmo.se.pokemon.*;

public final class BulletSeed extends CheckedPhysicalMove {
    public BulletSeed() {
        super(Type.GRASS, 25,100);
    }
    @Override
    protected void applyOppDamage(final Pokemon def, final double damage) {
        // Bullet Seed наносит от 2 до 5 ударов
        int hits=calculateHits();
        def.setMod(Stat.HP,25*hits);
        // Здесь могла бы быть проверка на контактные способности,
        // но в библиотеке ru.ifmo.se.pokemon это не реализовано

    }
    private int calculateHits() {
        double random = Math.random();
        if (random < 0.375) return 2;    // 37.5% chance
        else if (random < 0.75) return 3; // 37.5% chance
        else if (random < 0.875) return 4; // 12.5% chance
        else return 5;                    // 12.5% chance
    }
    @Override
    protected String describe() {
        return "использвует Bullet Seed и наносит от 2 до 5 ударов";
    }
}
