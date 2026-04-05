package common.data;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double x;

    private Float y; //Значение поля должно быть больше -93, Поле не может быть null
    public Coordinates(double x, Float y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
}