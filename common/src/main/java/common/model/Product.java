package common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Product implements Comparable<Product>, Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final double price;
    private final UnitOfMeasure unitOfMeasure;
    private final Organization manufacturer;

    // Конструктор для создания через DAO (с ID, который генерируется БД)
    public Product(int id, String name, Coordinates coordinates, LocalDateTime creationDate,
                   double price, UnitOfMeasure unitOfMeasure, Organization manufacturer) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
    }

    // Конструктор для создания нового продукта (без ID, для вставки в БД)
    public Product(String name, Coordinates coordinates, double price, UnitOfMeasure unitOfMeasure,
                   Organization manufacturer) {
        this(0, name, coordinates, LocalDateTime.now(), price, unitOfMeasure, manufacturer);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public double getPrice() {
        return price;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Organization getManufacturer() {
        return manufacturer;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', coordinates=%s, creationDate=%s, price=%.2f, unitOfMeasure=%s, manufacturer=%s}",
                id, name, coordinates, creationDate, price, unitOfMeasure, manufacturer);
    }

    // Реализация метода compareTo для сортировки по id
    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.id, other.id);
    }
}
