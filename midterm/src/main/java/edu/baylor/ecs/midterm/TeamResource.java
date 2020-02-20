package edu.baylor.ecs.midterm;

import edu.baylor.ecs.midterm.model.Person;
import edu.baylor.ecs.midterm.model.Team;
import edu.baylor.ecs.midterm.model.TeamException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@Path("/team")
@ApplicationScoped
public class TeamResource {

    @Inject
    private TeamService service;

    @PersistenceContext(unitName = "MidtermPU")
    private EntityManager em;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Team> findAll() throws Exception {
        List<Team> teams = em.createNamedQuery("Team.findAll", Team.class)
                .getResultList();
        System.out.println("Teams: " + teams.size());
        System.out.println("Teams: " + teams.get(1));
        return teams;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    public Response get(@PathParam("personId") Integer teamId) {
        Team team = em.createNamedQuery("Team.find", Team.class)
                .setParameter("teamId", teamId)
                .getSingleResult();
        //Team team = em.find(Team.class, teamId);
        if (team == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(null)
                    .build();
        }
        return Response
                .status(Response.Status.OK)
                .entity(team)
                .build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Team team) throws Exception {
        if (team.getId() != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("Unable to create Team, id was already set.")
                    .build();
        }

        Team createdTeam;

        try {
            createdTeam = service.createTeam(team);

        } catch (TeamException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Unable to create team! " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to create team! " + e.getMessage())
                    .build();
        }

        return Response
                .created(new URI("person/" + createdTeam.getId().toString()))
                .build();
    }

    @PUT
    @Path("/{teamId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addTeamMember(@PathParam("teamId") Integer teamId, Person person) {
        if (teamId == null || person == null || person.getId() == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Unable to add member, no team and/or member specified")
                    .build();
        }

        try {
            service.addMember(teamId, person.getId());

        } catch (TeamException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Unable to add member! " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to add member! " + e.getMessage())
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    @PUT
    @Path("/{teamId}/opponent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response setOpponent(@PathParam("teamId") Integer teamId, Team opp) {
        if (teamId == null || opp == null || opp.getId() == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Unable to add opponent, no team specified")
                    .build();
        }

        try {
            service.addOpponent(teamId, opp.getId());

        } catch (TeamException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Unable to add opponent! " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to add opponent! " + e.getMessage())
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    @Transactional
    public Response remove(@PathParam("personId") Integer personId) throws Exception {
        try {
            Team team = em.find(Team.class, personId);
            List<Person> people = em.createNamedQuery("Person.findAllByTeam")
                    .setParameter("owner", team)
                    .getResultList();

            // get rid of ownership for this person, but don't delete the cars
            for (Person person : people) {
                person.setTeam(null);
                em.merge(person);
            }

            em.remove(team);
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    @Transactional
    public Response update(@PathParam("personId") Integer personId, Team team) throws Exception {
        try {
            Team entity = em.find(Team.class, personId);

            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Person with id of " + personId + " does not exist.")
                        .build();
            }

            if (team.getId() == null || !team.getId().equals(personId)) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Person id expected to be " + personId + ", but was " + team.getId() + ".")
                        .build();
            }

            // all this setter logic would go in a service method, if there was a service layer
            entity.setName(team.getName());
            //entity.setEmail(team.getEmail());
            //entity.setAge(team.getAge());
            try {
                Team merged = em.merge(entity);
                em.flush();
                return Response
                        .ok(merged)
                        .build();
            } catch (ConstraintViolationException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Bad entity!")
                        .build();
            }

        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}/cars")
    public Response getCars(@PathParam("personId") Integer personId) {
        Team team = em.find(Team.class, personId);

        if (team == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Person with id of " + personId + " does not exist.")
                    .build();
        }

        List<Person> people = em.createNamedQuery("Person.findAllByTeam")
                .setParameter("owner", team)
                .getResultList();

        return Response
                .status(Response.Status.OK)
                .entity(people)
                .build();
    }
}
