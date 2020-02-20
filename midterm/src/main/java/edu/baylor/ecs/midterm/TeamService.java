package edu.baylor.ecs.midterm;

import edu.baylor.ecs.midterm.model.Person;
import edu.baylor.ecs.midterm.model.Team;
import edu.baylor.ecs.midterm.model.TeamException;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Stateless
public class TeamService {
    @PersistenceContext(unitName = "MidtermPU")
    private EntityManager em;

    public void test() {
        System.out.println("Test is tested hooray");
    }

    public Team createTeam(Team team) throws TeamException {
        if (team.getLeader() == null || team.getLeader().getId() == null) {
            throw new TeamException("Cannot create team, needs leader");
        }

        Person leader = em.find(Person.class, team.getLeader().getId());

        if (leader == null) {
            throw new TeamException("Cannot create team, needs leader");
        }

        if (leader.getTeam() != null || leader.getLedTeam() != null) {
            throw new TeamException("Cannot create team, leader on another team");
        }

        try{
            team.setLeader(leader);
            leader.setLedTeam(team);
            em.persist(leader);
            em.persist(team);
            em.flush();
        } catch (ConstraintViolationException e) {
            throw new TeamException("Cannot create team! " + e.getConstraintViolations().stream().map(v -> v.getMessage()).collect(Collectors.joining(", ")));
        }

        return team;
    }

    public void addMember(Integer teamId, Integer memberId) throws TeamException {
        Person member = em.find(Person.class, memberId);
        if (member == null) {
            throw new TeamException("Cannot add member, person doesn't exist");
        }

        if (member.getTeam() != null || member.getLedTeam() != null) {
            throw new TeamException("Cannot add member, person already member of a team");
        }
        Team team;
        try {
            team  = em.createNamedQuery("Team.find", Team.class)
                    .setParameter("teamId", teamId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new TeamException("Cannot add member, team doesn't exist");
        }


        if (team == null || team.getMembers().size() >= 3) {
            throw new TeamException("Cannot add member, team full");
        }

        try{
            member.setTeam(team);
            team.getMembers().add(member);
            em.persist(member);
            em.persist(team);
            em.flush();
        } catch (ConstraintViolationException e) {
            throw new TeamException("Cannot add member! " + e.getConstraintViolations().stream().map(v -> v.getMessage()).collect(Collectors.joining(", ")));
        }
    }

    public void addOpponent(Integer teamId, Integer oppId) throws TeamException {
        Team opp = em.find(Team.class, oppId);
        if (opp == null) {
            throw new TeamException("Cannot add opponent, team doesn't exist");
        }
        Team team;
        try {
            team  = em.createNamedQuery("Team.find", Team.class)
                    .setParameter("teamId", teamId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new TeamException("Cannot add opponent, team doesn't exist");
        }


        if (!team.getSkill().equals(opp.getSkill())) {
            throw new TeamException("Cannot add opponent, mismatched skill");
        }

        try {
            team.setOpponent(opp);
            em.persist(team);
            em.flush();
        } catch (ConstraintViolationException e) {
            throw new TeamException("Cannot add opponent! " + e.getConstraintViolations().stream().map(v -> v.getMessage()).collect(Collectors.joining(", ")));
        }
    }


}
