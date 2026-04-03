package WorldModel;

import WorldModel.Exceptions.UncheckedException;
import WorldModel.Animals.Fox.PawInjury;

import java.util.Random;

public abstract class Animal extends PhysicalObject {
    protected boolean isAwake = true;
    protected double health = 100.0;
    protected double stamina = 100.0;
    protected PawInjury injury = null;

    public Animal(String name) {
        super(name);
    }

    @Override
    public void move(double distance) throws UncheckedException {
        System.out.printf("%s %s, преодолевая %.1f км. (выносливость: %.1f)\n",
                name, currentMovement.getDescription(), distance, stamina);
        decreaseStamina(distance * new Random().nextDouble());
    }

    public double getStamina() {
        return stamina;
    }

    protected void decreaseStamina(double value) throws UncheckedException {
        stamina -= value;
        if (stamina < 0) {
            stamina = 0;
            throw new UncheckedException(this.name, "невероятно устал(-а) и больше не может двигаться дальше");
        }
    }

    public void sleep() {
        isAwake = false;
        stamina = 100.0;
        System.out.printf("%s засыпает и полностью восстанавливает силы.\n", name);
    }

    public void wakeUp() {
        isAwake = true;
        System.out.printf("%s просыпается\n", name);
    }

    public void wakeUp(String reason) {
        isAwake = true;
        System.out.printf("%s просыпается, поскольку услышал %s\n", name, reason);
    }

    public boolean isAwake() {
        return isAwake;
    }

    public void setInjury(PawInjury injury) {
        this.injury = injury;
    }

    @Override
    public String toString() {
        String status = injury != null ? " (травмирован: " + injury.description() + ")" : "";
        String sleepStatus = isAwake ? "Бодрствует" : "Спит";
        return String.format("%s [здоровье: %.1f, выносливость: %.1f, состояние: %s%s]",
                name, health, stamina, sleepStatus, status);
    }
}