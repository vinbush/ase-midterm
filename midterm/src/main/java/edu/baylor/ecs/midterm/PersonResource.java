package edu.baylor.ecs.midterm;

import edu.baylor.ecs.midterm.model.Person;
import edu.baylor.ecs.midterm.model.Team;

import javax.enterprise.context.ApplicationScoped;
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

@Path("/person")
@ApplicationScoped
public class PersonResource {

    @PersistenceContext(unitName = "MidtermPU")
    private EntityManager em;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Person> findAll() throws Exception {
        List<Person> people = em.createNamedQuery("Person.findAll", Person.class)
                .getResultList();
        for(Person person : people) {
            person.getLedTeam().getMembers().size();
            person.getTeam().getMembers().size();
        }

        return people;

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    public Response get(@PathParam("personId") Integer personId) {
        Person person = em.find(Person.class, personId);
        if (person == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(null)
                    .build();
        }
        return Response
                .status(Response.Status.OK)
                .entity(person)
                .build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Person person) throws Exception {
        if (person.getId() != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("Unable to create Person, id was already set.")
                    .build();
        }

        try {
            em.persist(person);
            em.flush();
        } catch (ConstraintViolationException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Bad entity!")
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
        return Response
                .created(new URI("car/" + person.getId().toString()))
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{carId}")
    @Transactional
    public Response remove(@PathParam("carId") Integer carId) throws Exception {
        try {
            Person person = em.find(Person.class, carId);
            if (person.getTeam() != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Cannot remove member on team!").build();
            }
            em.remove(person);
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
    @Path("/{carId}")
    @Transactional
    public Response update(@PathParam("carId") Integer carId, Person person) throws Exception {
        try {
            Person entity = em.find(Person.class, carId);
            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Car with id of " + carId + " does not exist.")
                        .build();
            }

            if (person.getId() == null || !person.getId().equals(carId)) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Car id expected to be " + carId + ", but was " + person.getId() + ".")
                        .build();
            }

            Team owner;
            if ((person.getTeam()) != null) {
                owner = person.getTeam();
                if (owner.getId() != null) {
                    Team fetchedOwner = em.find(Team.class, owner.getId());
                    if (fetchedOwner == null) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("Car was given invalid owner! ID " + owner.getId())
                                .build();
                    }
                    entity.setTeam(fetchedOwner);
                }
            }

//            entity.setBrand(person.getBrand());
//            entity.setLicensePlate(person.getLicensePlate());
//            entity.setType(person.getType());
            try {
                Person merged = em.merge(entity);
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
            System.err.println(e);
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }
}
