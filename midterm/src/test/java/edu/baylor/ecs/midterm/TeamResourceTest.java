package edu.baylor.ecs.midterm;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.baylor.ecs.midterm.model.Person;
import edu.baylor.ecs.midterm.model.Team;
import edu.baylor.ecs.midterm.model.TestTeamObject;
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
public class TeamResourceTest {

    @BeforeClass
    public static void setup() throws Exception {
        RestAssured.baseURI = "http://localhost:8080";
    }

    private ObjectMapper getMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
//
    @Test
    public void atestGetAllTeams() throws Exception {
        Response response =
                when()
                        .get("/team")
                .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");
        //List<Team> teams = getMapper().readValue(jsonAsString, new TypeReference<List<Person>>(){};
        // size of list
        assertThat(jsonAsList.size()).isEqualTo(4);

        Map<String, ?> record1 = jsonAsList.get(0);

//        ObjectMapper mapper = getMapper();

        // spot checking values
        assertThat(record1.get("id")).isEqualTo(1);
        assertThat(record1.get("name")).isEqualTo("Awesome Team");
        assertThat(record1.get("skill")).isEqualTo("Java");

        System.out.println(record1.get("members").toString());

//        //List<Person> members = getMapper().readValue(record1.get("members").toString(), new TypeReference<List<Person>>(){});
//
//        //List<Object> members = JsonPath.from(record1.get("members").toString()).getList("");
//
//        List<Team> teams = mapper.readValue(jsonAsString, new TypeReference<List<Team>>(){});
//        List<Person> members = teams.get(0).getMembers();
//
//        assertThat(members.size()).isEqualTo(3);

        Map<String, ?> record2 = jsonAsList.get(1);

        assertThat(record2.get("id")).isEqualTo(2);
        assertThat(record2.get("name")).isEqualTo("Baylor Team");
        assertThat(record2.get("skill")).isEqualTo("Java");
    }
//
    @Test
    public void btestGetTeam() throws Exception {
        Response response =
                given()
                        .pathParam("teamId", 2)
                .when()
                        .get("/team/{teamId}")
                .then()
                        .extract().response();

        String jsonAsString = response.asString();
        Map<String, ?> result = JsonPath.from(jsonAsString).getMap("");

        assertThat(result.get("id")).isEqualTo(2);
        assertThat(result.get("name")).isEqualTo("Baylor Team");
        assertThat(result.get("skill")).isEqualTo("Java");
    }
//
    @Test
    public void ctestCreateTeam() throws Exception {
        Team newTeam = new Team();
        newTeam.setName("New Team");
        newTeam.setSkill("Java");
        Person leader = new Person();
        leader.setId(11);
        newTeam.setLeader(leader);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newTeam)
                .when()
                        .post("/team");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(201);
        String locationUrl = response.getHeader("Location");
        Integer teamId = Integer.valueOf(locationUrl.substring(locationUrl.lastIndexOf('/') + 1));

        response =
                when()
                        .get("/team")
                .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(5);

        response =
                given()
                        .pathParam("teamId", teamId)
                .when()
                        .get("/team/{teamId}")
                .then()
                        .extract().response();

        jsonAsString = response.asString();
        Map<String, ?> result = JsonPath.from(jsonAsString).getMap("");

        assertThat(result.get("id")).isEqualTo(teamId);
        assertThat(result.get("name")).isEqualTo("New Team");
        assertThat(result.get("skill")).isEqualTo("Java");
    }

    @Test
    public void dtestAddMember() throws Exception {
        Person member = new Person();
        member.setId(12);

        Response response =

                given()
                        .pathParam("teamId", 3)
                        .contentType(ContentType.JSON)
                        .body(member)
                .when()
                    .put("/team/{teamId}/members");

        System.out.println(response.getStatusLine());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(204);
    }

    @Test
    public void etestAddOpponent() throws Exception {
        Team opp = new Team();
        opp.setId(4);

        Response response =

                given()
                        .pathParam("teamId", 3)
                        .contentType(ContentType.JSON)
                        .body(opp)
                        .when()
                        .put("/team/{teamId}/opponent");

        System.out.println(response.getStatusLine());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(204);
    }

    @Test
    public void ftestAddDupLeaderFails() throws Exception {
        Team newTeam = new Team();
        newTeam.setName("Another New Team");
        newTeam.setSkill("Java");
        Person leader = new Person();
        leader.setId(1);
        newTeam.setLeader(leader);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newTeam)
                        .when()
                        .post("/team");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(400);
    }

    @Test
    public void gtestAddDupMemberFails() throws Exception {
        Person member = new Person();
        member.setId(2);

        Response response =

                given()
                        .pathParam("teamId", 3)
                        .contentType(ContentType.JSON)
                        .body(member)
                        .when()
                        .put("/team/{teamId}/members");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(400);
    }

//
//    @Test
//    public void testCreatePersonNullField() throws Exception {
//        Team sirNoName = new Team();
//        sirNoName.setEmail("some@email.com");
//        sirNoName.setAge(49);
//
//        Response response =
//                given()
//                        .contentType(ContentType.JSON)
//                        .body(sirNoName)
//                .when()
//                        .post("/person");
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStatusCode()).isEqualTo(400);
//    }
//
//    @Test
//    public void testUpdatePerson() {
//        Team rob = new TestTeamObject(4);
//        rob.setName("Rob Robbie");
//        rob.setAge(19);
//        rob.setEmail("rrobbie@gmail.com"); // he ditched yahoo
//
//        Response response =
//                given()
//                        .contentType(ContentType.JSON)
//                        .pathParam("personId", 4)
//                        .body(rob)
//                        .when()
//                        .put("/person/{personId}");
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStatusCode()).isEqualTo(200);
//
//        response =
//                given()
//                        .pathParam("personId", 4)
//                        .when()
//                        .get("/person/{personId}")
//                        .then()
//                        .extract().response();
//
//        String jsonAsString = response.asString();
//        Team team = JsonPath.from(jsonAsString).getObject("", Team.class);
//
//        assertThat(team.getId()).isEqualTo(4);
//        assertThat(team.getEmail()).isEqualTo("rrobbie@gmail.com");
//    }
//
//    @Test
//    public void testUpdatePersonNullId() {
//        Team rob = new Team(); // no ID
//        rob.setName("Rob Robbie III");
//        rob.setAge(19);
//        rob.setEmail("rob@yahoo.net");
//
//        Response response =
//                given()
//                        .contentType(ContentType.JSON)
//                        .pathParam("personId", 4)
//                        .body(rob)
//                        .when()
//                        .put("/person/{personId}");
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStatusCode()).isEqualTo(400);
//
//        response =
//                given()
//                        .pathParam("personId", 4)
//                        .when()
//                        .get("/person/{personId}")
//                        .then()
//                        .extract().response();
//
//        String jsonAsString = response.asString();
//        Team team = JsonPath.from(jsonAsString).getObject("", Team.class);
//
//        assertThat(team.getId()).isEqualTo(4);
//        // assert email didn't change
//        assertThat(team.getName()).isEqualTo("Rob Robbie");
//    }
//
//    @Test
//    public void testDeletePerson() {
//        // get list as is
//        Response response =
//                when()
//                        .get("/person")
//                        .then()
//                        .extract().response();
//
//        String oldJsonString = response.asString();
//        List<Map<String, ?>> oldList = JsonPath.from(oldJsonString).getList("");
//
//        response =
//                given()
//                        .pathParam("personId", 5)
//                        .when()
//                        .delete("/person/{personId}")
//                        .then()
//                        .extract().response();
//
//        assertThat(response.getStatusCode()).isEqualTo(204);
//
//        // get the new list
//        response =
//                when()
//                        .get("/person")
//                        .then()
//                        .extract().response();
//
//        String newJsonString = response.asString();
//        List<Map<String, ?>> newList = JsonPath.from(newJsonString).getList("");
//
//        assertThat(newList.size()).isEqualTo(oldList.size() - 1);
//
//        // try to get the deleted person
//        response =
//                given()
//                        .pathParam("personId", 5)
//                        .when()
//                        .get("/person/{personId}")
//                        .then()
//                        .extract().response();
//
//        assertThat(response.getStatusCode()).isEqualTo(404);
//
//    }
}
