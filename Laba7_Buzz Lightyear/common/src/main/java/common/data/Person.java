package common.data;

import java.io.Serializable;

public class Person  implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Integer height; //Поле не может быть null, Значение поля должно быть больше 0
    private float weight; //Значение поля должно быть больше 0

    public Person(String name, Integer height, float weight) {
        this.name = name;
        this.height = height;
        this.weight = weight;
    }
    public String getName(){
        return name;
    }
    public Integer getHeight(){
        return height;
    }
    public float getWeight(){
        return weight;
    }
    @Override
    public String toString() {
        return "Person {name='" + name + "', height=" + height + ", weight=" + weight + "}";
    }
}