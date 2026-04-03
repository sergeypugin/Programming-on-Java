import WorldModel.Animals.Fox.Fox;
import WorldModel.Animals.Goose.Goose;
import WorldModel.Animals.Goose.LeaderGoose;
import WorldModel.Exceptions.*;

import WorldModel.Animals.*;
import WorldModel.Animals.Goose.GooseFlock;
import WorldModel.Rock;

import java.util.List;
import java.util.Random;

import static WorldModel.echo.repeat;

public class Main {
    public static void main(String[] args) throws UncheckedException {
        System.out.println("==========================================");
        System.out.println("  Запуск сценария: Объектная модель мира");
        System.out.println("==========================================");
        final Fox smirre = new Fox("Смирре");
        final Otter otter = new Otter("Выдра");
        final Rock rock = new Rock("Одинокая скала", "высоко торчит, как поднятый палец великана");
        final LeaderGoose akka = new LeaderGoose("Акка Кебнекайсе");
        final GooseFlock flock = new GooseFlock(List.of(
                akka,
                new Goose("Гусь 1"),
                new Goose("Гусь 2"),
                new Goose("Гусь 3")
        ));

        System.out.print("\n--- Начальное состояние объектов ---\n");
        System.out.println(smirre);
        System.out.println(otter);

        System.out.print("\n--- Сцена 1: Встреча Смирре и Выдры ---\n");
        smirre.walk(0.1);
        smirre.slip(otter);
        smirre.growl("мокрая выдра");
        otter.complain("Да, тебе-то легко говорить, а я вот чуть без лапы не осталась...");

        System.out.print("\n--- Сцена 2: Охота выдры и травма ---\n");
        try {
            otter.hunt(flock.getGeese().get(1));
        } catch (CheckedException e) {
            System.out.flush();
            System.err.println(e.getMessage());
            System.out.println(otter.getName() + " поднимает раненую лапу. И верно, перепонка была вся изрезана и висела кровавыми клочьями.");
        }
        System.out.println(smirre.getName() + " перешагнул через " + otter.getName() + " и побежал дальше.");

        System.out.println("\n--- Сцена 3: Гонка к скале ---");
        if (new Random().nextBoolean()) {
            System.out.println("Ночь уже подходила к концу...");
        }
        flock.flyTo(rock);
        try {
            smirre.run(150);
            System.out.println(smirre.getName() + " подбежал к подножию скалы. По земле путь длинней, чем по воздуху.");
        } catch (UncheckedException e) {
            System.out.flush();
            System.err.println(e.getMessage());
        }
        System.out.println("\n--- Сцена 4: Гуси на вершине скалы ---");
        for (Goose goose : flock.getGeese()) {
            goose.sleep();
        }
        if (smirre.getStamina()>0){
            System.out.println("\n--- Сцена 5: Смирре развлекает гусей ---");
            smirre.hunt(rock);
            smirre.mock();
            repeat();
            String reason_to_wake_up="вой Смирре";
            for (Goose goose : flock.getGeese()) {
                if (!goose.equals(akka)) {
                    goose.wakeUp(reason_to_wake_up);
                    goose.honk(reason_to_wake_up);
                }
        }
        akka.stopOthers(flock);
        }
        System.out.println();
        System.out.println("==================================");
        System.out.println("   Финальное состояние объектов");
        System.out.println("==================================");
        System.out.println(smirre);
        System.out.println(otter);
        System.out.println(akka);
    }
}