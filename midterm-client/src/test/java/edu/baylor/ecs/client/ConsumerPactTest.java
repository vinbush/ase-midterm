package edu.baylor.ecs.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.Request;
import au.com.dius.pact.model.RequestResponseInteraction;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.fest.assertions.Assertions;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Ken Finnigan
 */
public class ConsumerPactTest extends ConsumerPactTestMk2 {
    private Person createPerson(Integer id, Integer age, String email, String name) {
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        person.setEmail(email);
        person.setAge(age);
        person.setVersion(1);

        return person;
    }

    private Person createDummyPerson(Integer id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {
        Person sam = createPerson(1, 30, "sam@houston.com", "Sam Houston");
        Person newPerson = createPerson(null, 27, "new@new.com", "New Person");
        Person editedRob = createPerson(4, 19, "rrobbie@gmail.com", "Rob Robbie");

        String editedResult = null;
        String allCategoriesResult = null;
        String carsResult = null;
        try {
            editedResult = new String(Files.readAllBytes(Paths.get(ConsumerPactTest.class.getClassLoader().getResource("editedResult.json").toURI())));
            allCategoriesResult = new String(Files.readAllBytes(Paths.get(ConsumerPactTest.class.getClassLoader().getResource("allPersons.json").toURI())));
            carsResult = new String(Files.readAllBytes(Paths.get(ConsumerPactTest.class.getClassLoader().getResource("carsResult.json").toURI())));
        } catch (Exception e) {
            System.err.println("Could not read sample json files: " + e.toString());
            return null;
        }

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, String> createdHeaders = new HashMap<>();
        createdHeaders.put("Location", "http://localhost:8080/person/6");

        try {
            return builder
                    .given("test get")
                        .uponReceiving("Retrieve a person")
                            .path("/person/1")
                            .method("GET")
                        .willRespondWith()
                            .status(200)
                            .body(mapper.writeValueAsString(sam))
                    .given("test get all")
                        .uponReceiving("Retrieve all persons")
                            .path("/person")
                            .method("GET")
                        .willRespondWith()
                            .status(200)
                    .given("test get all")
                        .uponReceiving("Retrieve all person's cars")
                            .path("/person/2/cars")
                            .method("GET")
                        .willRespondWith()
                            .status(200)
                            .body(carsResult)
                    .given("test create")
                        .uponReceiving("Create a person")
                            .path("/person")
                            .method("POST")
                            .headers(headers)
                            .body(mapper.writeValueAsString(newPerson))
                        .willRespondWith()
                            .status(201)
                            .headers(createdHeaders)
                    .given("test delete")
                        .uponReceiving("Delete a person")
                            .path("/person/5")
                            .method("DELETE")
                        .willRespondWith()
                            .status(204)
                    .given("test update")
                        .uponReceiving("Update a person")
                            .path("/person/4")
                            .method("PUT")
                            .headers(headers)
                            .body(mapper.writeValueAsString(editedRob))
                        .willRespondWith()
                            .status(200)
                            .body(editedResult)
                    .toPact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected String providerName() {
        return "endpoint_service_provider";
    }

    @Override
    protected String consumerName() {
        return "endpoint_client_consumer";
    }

    @Override
    protected PactSpecVersion getSpecificationVersion() {
        return PactSpecVersion.V3;
    }

    @Override
    protected void runTest(MockServer mockServer) throws IOException {
        EndpointClient client = new EndpointClient(mockServer.getUrl());

        client.deletePerson(5);

        Person person = client.getPerson(1);

        Assertions.assertThat(person).isNotNull();
        assertThat(person.getId()).isEqualTo(1);
        assertThat(person.getName()).isEqualTo("Sam Houston");
        assertThat(person.getEmail()).isEqualTo("sam@houston.com");
        assertThat(person.getAge()).isEqualTo(30);

        List<Car> cars = client.getPersonCars(2);

        List<Person> allPersons = client.getAllPersons();

        Person newPerson = createPerson(null, 27, "new@new.com", "New Person");
        String createdUrl = client.createPerson(newPerson);

        assertThat(createdUrl).isNotNull();
        assertThat(createdUrl).isNotEmpty();

        Person edited = createPerson(4, 19, "rrobbie@gmail.com", "Rob Robbie");
        Person modifiedPerson = client.updatePerson(4, edited);

        assertThat(modifiedPerson).isNotNull();
        assertThat(modifiedPerson.getId()).isEqualTo(4);
        assertThat(modifiedPerson.getName()).isEqualTo("Rob Robbie");
        assertThat(modifiedPerson.getEmail()).isEqualTo("rrobbie@gmail.com");
        assertThat(modifiedPerson.getAge()).isEqualTo(19);

        assert(true);

    }
}
