package edu.baylor.ecs.midterm;

import edu.baylor.ecs.midterm.model.Car;
import edu.baylor.ecs.midterm.model.Person;

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
        return em.createNamedQuery("Person.findAll", Person.class)
                .getResultList();
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
                .created(new URI("person/" + person.getId().toString()))
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    @Transactional
    public Response remove(@PathParam("personId") Integer personId) throws Exception {
        try {
            Person person = em.find(Person.class, personId);
            List<Car> cars = em.createNamedQuery("Car.findAllByPerson")
                    .setParameter("owner", person)
                    .getResultList();

            // get rid of ownership for this person, but don't delete the cars
            for (Car car : cars) {
                car.setOwner(null);
                em.merge(car);
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
    @Path("/{personId}")
    @Transactional
    public Response update(@PathParam("personId") Integer personId, Person person) throws Exception {
        try {
            Person entity = em.find(Person.class, personId);

            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Person with id of " + personId + " does not exist.")
                        .build();
            }

            if (person.getId() == null || !person.getId().equals(personId)) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Person id expected to be " + personId + ", but was " + person.getId() + ".")
                        .build();
            }

            // all this setter logic would go in a service method, if there was a service layer
            entity.setName(person.getName());
            entity.setEmail(person.getEmail());
            entity.setAge(person.getAge());
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
        Person person = em.find(Person.class, personId);

        if (person == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Person with id of " + personId + " does not exist.")
                    .build();
        }

        List<Car> cars = em.createNamedQuery("Car.findAllByPerson")
                .setParameter("owner", person)
                .getResultList();

        return Response
                .status(Response.Status.OK)
                .entity(cars)
                .build();
    }
}
