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

@Path("/car")
@ApplicationScoped
public class CarResource {

    @PersistenceContext(unitName = "MidtermPU")
    private EntityManager em;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Car> findAll() throws Exception {
        return em.createNamedQuery("Car.findAll", Car.class)
                .getResultList();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{carId}")
    public Response get(@PathParam("carId") Integer carId) {
        Car car = em.find(Car.class, carId);
        if (car == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(null)
                    .build();
        }
        return Response
                .status(Response.Status.OK)
                .entity(car)
                .build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Car car) throws Exception {
        if (car.getId() != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("Unable to create Car, id was already set.")
                    .build();
        }

        try {
            em.persist(car);
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
                .created(new URI("car/" + car.getId().toString()))
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{carId}")
    @Transactional
    public Response remove(@PathParam("carId") Integer carId) throws Exception {
        try {
            Car car = em.find(Car.class, carId);
            em.remove(car);
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
    public Response update(@PathParam("carId") Integer carId, Car car) throws Exception {
        try {
            Car entity = em.find(Car.class, carId);
            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Car with id of " + carId + " does not exist.")
                        .build();
            }

            if (car.getId() == null || !car.getId().equals(carId)) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Car id expected to be " + carId + ", but was " + car.getId() + ".")
                        .build();
            }

            Person owner;
            if ((car.getOwner()) != null) {
                owner = car.getOwner();
                if (owner.getId() != null) {
                    Person fetchedOwner = em.find(Person.class, owner.getId());
                    if (fetchedOwner == null) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("Car was given invalid owner! ID " + owner.getId())
                                .build();
                    }
                    entity.setOwner(fetchedOwner);
                }
            }

            entity.setBrand(car.getBrand());
            entity.setLicensePlate(car.getLicensePlate());
            entity.setType(car.getType());
            try {
                Car merged = em.merge(entity);
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
