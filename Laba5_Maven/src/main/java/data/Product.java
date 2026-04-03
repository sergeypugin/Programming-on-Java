package data;

import java.util.Date;

public class Product implements Comparable<Product> {
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private java.util.Date creationDate; // Дата создания
    private long price;
    private UnitOfMeasure unitOfMeasure;
    private Person owner;

    public Product(String name, Coordinates coordinates, long price, UnitOfMeasure unit, Person owner) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.unitOfMeasure = unit;
        this.owner = owner;

        this.creationDate = new Date();
//        ID будет в колллекции
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCreationDate(Date creationDate){
        this.creationDate=creationDate;
    }

    public Date getCreationDate(){
        return creationDate;
    }

    public long getPrice() {
        return price;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Person getOwner() {
        return owner;
    }
//    creationDate создано автоматически в конструкторе

    @Override
    public int compareTo(Product other) {// Сортировка по имени и цене
        int result = this.name.compareTo(other.name);
        if (result != 0) {
            return result;
        }
        return Long.compare(this.price, other.price);
    }
    @Override
    public String toString() {
        return "Product {" +
                "id=" + id +
                ", name='" + name +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", unit=" + unitOfMeasure +
                ", owner=" + owner + "}";
    }
}