package edu.baylor.ecs.midterm.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "team")
@NamedQueries({
        @NamedQuery(name = "Team.findAll", query = "select distinct t from Team t left join fetch t.members"),
        @NamedQuery(name = "Team.find", query = "select distinct t from Team t left join fetch t.members where t.id = :teamId")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Team {

    @Id
    @SequenceGenerator(
            name = "team_seq",
            allocationSize = 1,
            initialValue = 5
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    @Column(name = "id", updatable = false, nullable = false)
    protected Integer id;

    @OneToOne(mappedBy = "ledTeam")
    protected Person leader;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @Size(max = 3)
    protected List<Person> members = new ArrayList<>();

    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    protected String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "opponent_id", nullable = true) // don't need an opponent always
    protected Team opponent;

    @NotNull
    @Column(nullable = false)
    protected String skill;

    @Version
    protected Integer version = 0;

    public Team() {
    }

    public Team(Person leader, List<Person> members, String name, Team opponent, String skill) {
        this.name = name;
        this.leader = leader;
        this.opponent = opponent;
        this.skill = skill;
        this.members = members;
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

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Person getLeader() {
        return leader;
    }

    public void setLeader(Person leader) {
        this.leader = leader;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public Team getOpponent() {
        return opponent;
    }

    public void setOpponent(Team opponent) {
        this.opponent = opponent;
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
        Team other = (Team) o;
        return Objects.equals(name, other.name) &&
                Objects.equals(skill, other.skill) &&
                Objects.equals(version, other.version) &&
                ((leader == null && other.leader == null) ||
                        (leader != null && other.leader != null && Objects.equals(leader.getId(), other.leader.getId()))) &&
                ((opponent == null && other.opponent == null) ||
                        (opponent != null && other.opponent != null && Objects.equals(opponent.getId(), other.opponent.getId())));
    }

    @Override
    public int hashCode() {
        // ID not included in hash code
        return Objects.hash(skill, name, version);
    }
}
