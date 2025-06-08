package common.model;


import java.io.Serializable;
import java.util.Objects;

public class Organization implements Serializable {

    private final Long id;
    private final String name;
    private final String fullName;
    private final OrganizationType type;


    public Organization(Long id, String name, String fullName, OrganizationType type) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.type = type;
    }

    public Organization(String name, String fullName, OrganizationType type) {
        this(null, name, fullName, type);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Organization{id=%d, name='%s', fullName='%s', type=%s}",
                id, name, fullName, type);
    }
}
