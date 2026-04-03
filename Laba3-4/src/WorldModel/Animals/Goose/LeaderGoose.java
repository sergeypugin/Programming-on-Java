package WorldModel.Animals.Goose;

public final class LeaderGoose extends Goose {
    public LeaderGoose(String name) {
        super(name);
    }

    public void stopOthers(GooseFlock flock) {
        System.out.printf("Лидер %s издает резкий голос: \"Опасности нет! Спите спокойно.\"\n", name);
        for (Goose goose : flock.getGeese()) {
            if (!goose.equals(this) && goose.isAwake()) {
                goose.sleep();
            }
        }
        this.sleep();
    }
}