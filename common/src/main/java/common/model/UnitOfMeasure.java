package client.model;

import java.io.Serializable;

public enum UnitOfMeasure {
    METERS(1),
    PCS(2),
    LITERS(3);

    private final Integer defaultValue;

    UnitOfMeasure(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }
}