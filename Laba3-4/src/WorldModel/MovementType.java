package WorldModel;

public enum MovementType {
    RUNNING("бежит"),
    FLYING("летит"),
    SWIMMING("плывет"),
    WALKING("идет");
    private final String Description;

    MovementType(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }
}