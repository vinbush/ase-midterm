package edu.baylor.ecs.midterm.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "person")
@NamedQueries({
        @NamedQuery(name = "Person.findAll", query = "SELECT p from Person p"),
        @NamedQuery(name = "Person.findAllByTeam", query = "select c from Person c where c.team = :team")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Person {

    @Id
    @SequenceGenerator(
            name = "person_seq",
            allocationSize = 1,
            initialValue = 13
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    protected LocalDateTime birthdate;

    @OneToOne
    @JoinColumn(name = "led_team_id")
    protected Team ledTeam;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Version
    private Integer version = 0;

    public Person() {
    }

    public Person(String name, LocalDateTime birthdate, Team ledTeam, Team team, Integer version) {
        this.name = name;
        this.birthdate = birthdate;
        this.ledTeam = ledTeam;
        this.team = team;
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

    public LocalDateTime getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDateTime birthdate) {
        this.birthdate = birthdate;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getLedTeam() {
        return ledTeam;
    }

    public void setLedTeam(Team ledTeam) {
        this.ledTeam = ledTeam;
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
        Person person = (Person) o;
        // person ID not included in equality check, but team ID is because for team to be set, they must already exist w/ ID
        return Objects.equals(name, person.name) &&
                Objects.equals(birthdate, person.birthdate) &&
                Objects.equals(version, person.version) &&
                ((team == null && person.team == null) ||
                        (team != null && person.team != null && Objects.equals(team.getId(), person.team.getId())));
    }

    @Override
    public int hashCode() {
        // person ID not included in hash code
        return Objects.hash(name, birthdate, version);
    }
}
