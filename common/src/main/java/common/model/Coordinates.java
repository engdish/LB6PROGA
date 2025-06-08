package common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    private final float x;
    private final Float y;

    @JsonCreator
    public Coordinates(@JsonProperty("x") float x, @JsonProperty("y") Float y) {
        this.x = x;
        if (y == null) {
            throw new IllegalArgumentException("Координата Y не может быть null");
        }
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("Coordinates{x=%.1f, y=%.1f}", x, y);
    }
}