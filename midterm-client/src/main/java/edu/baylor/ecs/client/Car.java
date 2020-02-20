package edu.baylor.ecs.client;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Car.class)
public class Car {

    private Integer id;

    private String brand;

    private String type;

    private String licensePlate;

    private Person owner;

    private Integer version = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        // car ID not included in equality check, but owner ID is because for owner to be set, they must already exist w/ ID
        return Objects.equals(brand, car.brand) &&
                Objects.equals(type, car.type) &&
                Objects.equals(licensePlate, car.licensePlate) &&
                Objects.equals(version, car.version) &&
                (owner == null || (car.owner != null && Objects.equals(owner.getId(), car.owner.getId())));
    }

    @Override
    public int hashCode() {
        // car ID not included in hash code
        return Objects.hash(brand, type, licensePlate, version);
    }
}
