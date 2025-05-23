package client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Product implements Comparable<Product>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final double price;
    private final UnitOfMeasure unitOfMeasure;
    private final Organization manufacturer;

    @JsonCreator
    public Product(@JsonProperty("name") String name,
                   @JsonProperty("coordinates") Coordinates coordinates,
                   @JsonProperty("price") double price,
                   @JsonProperty("unitOfMeasure") UnitOfMeasure unitOfMeasure,
                   @JsonProperty("manufacturer") Organization manufacturer) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя продукта не может быть пустым или null!");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null!");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше чем 0!");
        }
        if (manufacturer == null) {
            throw new IllegalArgumentException("Производитель не может быть null!");
        }
        this.id = idGenerator.getAndIncrement();
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
        this.creationDate = LocalDateTime.now();
    }

    public static void updateIdGenerator(int newValue) {
        idGenerator.set(newValue);
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

    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', coordinates=%s, creationDate=%s, price=%.2f, unitOfMeasure=%s, manufacturer=%s}",
                id, name, coordinates, creationDate, price,
                Objects.toString(unitOfMeasure, "null"), manufacturer);
    }
}