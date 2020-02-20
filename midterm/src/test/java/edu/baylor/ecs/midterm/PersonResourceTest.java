package edu.baylor.ecs.midterm;

import java.util.List;
import java.util.Map;

import edu.baylor.ecs.midterm.model.Person;
import edu.baylor.ecs.midterm.model.TestPersonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersonResourceTest {

    @BeforeClass
    public static void setup() throws Exception {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testGetAllPersons() throws Exception {
        Response response =
                when()
                        .get("/person")
                .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        // size of list
        assertThat(jsonAsList.size()).isEqualTo(5);

        Map<String, ?> record1 = jsonAsList.get(0);

        // spot checking values
        assertThat(record1.get("id")).isEqualTo(1);
        assertThat(record1.get("name")).isEqualTo("Sam Houston");
        assertThat(record1.get("age")).isEqualTo(30);

        Map<String, ?> record2 = jsonAsList.get(2);

        assertThat(record2.get("id")).isEqualTo(3);
        assertThat(record2.get("name")).isEqualTo("Otto von Bismark");
        assertThat(record2.get("age")).isEqualTo(56);
    }

    @Test
    public void testGetPerson() throws Exception {
        Response response =
                given()
                        .pathParam("personId", 2)
                .when()
                        .get("/person/{personId}")
                .then()
                        .extract().response();

        String jsonAsString = response.asString();

        Person person = JsonPath.from(jsonAsString).getObject("", Person.class);

        assertThat(person.getId()).isEqualTo(2);
        assertThat(person.getName()).isEqualTo("Kane Barker");
        assertThat(person.getAge()).isEqualTo(24);
    }

    @Test
    public void testCreatePerson() throws Exception {
        Person newPerson = new Person();
        newPerson.setName("New Person");
        newPerson.setAge(92);
        newPerson.setEmail("person@place.com");

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newPerson)
                .when()
                        .post("/person");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(201);
        String locationUrl = response.getHeader("Location");
        Integer personId = Integer.valueOf(locationUrl.substring(locationUrl.lastIndexOf('/') + 1));

        response =
                when()
                        .get("/person")
                .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(6);

        response =
                given()
                        .pathParam("personId", personId)
                .when()
                        .get("/person/{personId}")
                .then()
                        .extract().response();

        jsonAsString = response.asString();

        Person person = JsonPath.from(jsonAsString).getObject("", Person.class);

        assertThat(person.getId()).isEqualTo(personId);
        assertThat(person.getName()).isEqualTo("New Person");
        assertThat(person.getAge()).isEqualTo(92);
        assertThat(person.getEmail()).isEqualTo("person@place.com");
    }

    @Test
    public void testCreatePersonNullField() throws Exception {
        Person sirNoName = new Person();
        sirNoName.setEmail("some@email.com");
        sirNoName.setAge(49);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(sirNoName)
                .when()
                        .post("/person");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testUpdatePerson() {
        Person rob = new TestPersonObject(4);
        rob.setName("Rob Robbie");
        rob.setAge(19);
        rob.setEmail("rrobbie@gmail.com"); // he ditched yahoo

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("personId", 4)
                        .body(rob)
                        .when()
                        .put("/person/{personId}");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);

        response =
                given()
                        .pathParam("personId", 4)
                        .when()
                        .get("/person/{personId}")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        Person person = JsonPath.from(jsonAsString).getObject("", Person.class);

        assertThat(person.getId()).isEqualTo(4);
        assertThat(person.getEmail()).isEqualTo("rrobbie@gmail.com");
    }

    @Test
    public void testUpdatePersonNullId() {
        Person rob = new Person(); // no ID
        rob.setName("Rob Robbie III");
        rob.setAge(19);
        rob.setEmail("rob@yahoo.net");

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("personId", 4)
                        .body(rob)
                        .when()
                        .put("/person/{personId}");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(400);

        response =
                given()
                        .pathParam("personId", 4)
                        .when()
                        .get("/person/{personId}")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        Person person = JsonPath.from(jsonAsString).getObject("", Person.class);

        assertThat(person.getId()).isEqualTo(4);
        // assert email didn't change
        assertThat(person.getName()).isEqualTo("Rob Robbie");
    }

    @Test
    public void testDeletePerson() {
        // get list as is
        Response response =
                when()
                        .get("/person")
                        .then()
                        .extract().response();

        String oldJsonString = response.asString();
        List<Map<String, ?>> oldList = JsonPath.from(oldJsonString).getList("");

        response =
                given()
                        .pathParam("personId", 5)
                        .when()
                        .delete("/person/{personId}")
                        .then()
                        .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(204);

        // get the new list
        response =
                when()
                        .get("/person")
                        .then()
                        .extract().response();

        String newJsonString = response.asString();
        List<Map<String, ?>> newList = JsonPath.from(newJsonString).getList("");

        assertThat(newList.size()).isEqualTo(oldList.size() - 1);

        // try to get the deleted person
        response =
                given()
                        .pathParam("personId", 5)
                        .when()
                        .get("/person/{personId}")
                        .then()
                        .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(404);

    }
}
