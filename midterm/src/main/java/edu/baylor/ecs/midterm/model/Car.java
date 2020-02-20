package edu.baylor.ecs.midterm.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "car")
@NamedQueries({
        @NamedQuery(name = "Car.findAll", query = "SELECT c from Car c"),
        @NamedQuery(name = "Car.findAllByPerson", query = "select c from Car c where c.owner = :owner")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Car.class)
public class Car {

    @Id
    @SequenceGenerator(
            name = "car_sequence",
            allocationSize = 1,
            initialValue = 12
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String brand;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String type;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z0-9]+ ?[A-Z0-9]*$")
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person owner;

    @Version
    private Integer version = 0;

    public Car() {
    }

    public Car(String brand, String licensePlate, String type, Integer version, Person owner) {
        this.brand = brand;
        this.type = type;
        this.licensePlate = licensePlate;
        this.owner = owner;
        this.version = version;
    }

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
