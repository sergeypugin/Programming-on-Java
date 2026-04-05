package common.data;

import java.io.Serializable;
import java.util.Date;

public class Product implements Comparable<Product>, Serializable {
    private long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate; // Дата создания
    private long price;
    private UnitOfMeasure unitOfMeasure;
    private Person owner;
    private String creatorUsername;

    public Product(String name, Coordinates coordinates, long price, UnitOfMeasure unit, Person owner) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.unitOfMeasure = unit;
        this.owner = owner;
        this.creationDate = new Date();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate(){
        return creationDate;
    }
    public void setCreationDate(Date creationDate){
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public long getPrice() {
        return price;
    }
    public void setPrice(long price) {
        this.price = price;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }
    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Person getOwner() {
        return owner;
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getCreatorUsername(){
        return creatorUsername;
    }
    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    @Override
    public int compareTo(Product other) {
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
                ", owner=" + owner +
                ", creator='" + creatorUsername + "'}";
    }
}