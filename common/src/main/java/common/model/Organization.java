package client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicLong idGenerator = new AtomicLong(1);
    private final Long id;
    private final String name;
    private final String fullName;
    private final OrganizationType type;

    @JsonCreator
    public Organization(@JsonProperty("name") String name,
                        @JsonProperty("fullName") String fullName,
                        @JsonProperty("type") OrganizationType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя организации не может быть пустым или null!");
        }
        this.id = idGenerator.getAndIncrement();
        this.name = name;
        this.fullName = fullName;
        this.type = type;
    }

    public static void updateIdGenerator(long newValue) {
        idGenerator.set(newValue);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public OrganizationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Organization{id=%d, name='%s', fullName='%s', type=%s}",
                id, name, Objects.toString(fullName, "null"), Objects.toString(type, "null"));
    }
}