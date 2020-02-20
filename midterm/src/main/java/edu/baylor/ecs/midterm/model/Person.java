package edu.baylor.ecs.midterm.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "person")
@NamedQueries({
        @NamedQuery(name = "Person.findAll", query = "select p from Person p")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Person.class)
public class Person {

    @Id
    @SequenceGenerator(
            name = "person_sequence",
            allocationSize = 1,
            initialValue = 6
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    protected Integer id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    protected String name;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", flags = Pattern.Flag.CASE_INSENSITIVE)
    protected String email;

    @Column(nullable = false)
    protected int age;

    @Version
    protected Integer version = 0;

    public Person() {
    }

    public Person(int age, String email, String name, Integer version) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
        Person other = (Person) o;
        // person ID not included in equality check
        return Objects.equals(name, other.name) &&
                Objects.equals(email, other.email) &&
                Objects.equals(age, other.age) &&
                Objects.equals(version, other.version);
    }

    @Override
    public int hashCode() {
        // person ID not included in hash code
        return Objects.hash(age, email, name, version);
    }
}
