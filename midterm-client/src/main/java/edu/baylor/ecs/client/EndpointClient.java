package edu.baylor.ecs.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.baylor.ecs.client.Person;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class EndpointClient {
    private String url;
    private ObjectMapper mapper = new ObjectMapper();

    public EndpointClient(String url) {
        this.url = url;
    }

    public List<Person> getAllPersons() throws RuntimeException, IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/person");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                    .Get(uriBuilder.toString())
                    .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        TypeReference<List<Person>> listType = new TypeReference<List<Person>>() {};
        return mapper
                .readValue(jsonResponse, listType);
    }

    public Person getPerson(final Integer categoryId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/person/" + categoryId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                    .Get(uriBuilder.toString())
                    .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return mapper
                .readValue(jsonResponse, Person.class);
    }

    public List<Car> getPersonCars(final Integer personId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/person/" + personId + "/cars");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                        .Get(uriBuilder.toString())
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        TypeReference<List<Car>> listType = new TypeReference<List<Car>>() {};
        return mapper
                .readValue(jsonResponse, listType);
    }

    public String createPerson(Person person) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/person");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String personString = mapper
                .writeValueAsString(person);

        HttpEntity personEntity = new StringEntity(personString, ContentType.APPLICATION_JSON);
        HttpResponse response =
                Request
                        .Post(uriBuilder.toString())
                        .body(personEntity)
                        .execute().returnResponse();

        return response.getHeaders("Location")[0].getValue();
    }

    public void deletePerson(int id) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/person/" + id);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Request
                .Delete(uriBuilder.toString())
                .execute();
    }

    public Person updatePerson(int id, Person person) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/person/" + id);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String personString = mapper
                .writeValueAsString(person);

        HttpEntity personEntity = new StringEntity(personString, ContentType.APPLICATION_JSON);
        String jsonResponse =
                Request
                        .Put(uriBuilder.toString())
                        .body(personEntity)
                        .execute()
                        .returnContent().asString();

        return mapper
                .readValue(jsonResponse, Person.class);
    }

}
