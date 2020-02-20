package edu.baylor.ecs.midterm.model;


import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PersonTest {

    @Test
    public void personsAreEqual() throws Exception {
        Person one = createPerson(1, "sam", "sam@sam.com", 5);
        Person two = createPerson(1, "sam", "sam@sam.com", 5);

        assertThat(one).isEqualTo(two);
        assertThat(one.equals(two)).isTrue();
        assertThat(one.hashCode()).isEqualTo(two.hashCode());
    }

    @Test
    public void categoriesAreNotEqual() throws Exception {
        Person one = createPerson(1, "sam", "sam@sam.com", 5);
        Person two = createPerson(1, "else", "sam@sam.com", 5);

        assertThat(one).isNotEqualTo(two);
        assertThat(one.equals(two)).isFalse();
        assertThat(one.hashCode()).isNotEqualTo(two.hashCode());
    }

    @Test
    public void categoryModification() throws Exception {
        Person one = createPerson(1, "sam", "sam@sam.com", 5);
        Person two = createPerson(1, "sam", "sam@sam.com", 5);

        assertThat(one).isEqualTo(two);
        assertThat(one.equals(two)).isTrue();
        assertThat(one.hashCode()).isEqualTo(two.hashCode());

        one.setName("else");

        assertThat(one).isNotEqualTo(two);
        assertThat(one.equals(two)).isFalse();
        assertThat(one.hashCode()).isNotEqualTo(two.hashCode());
    }

    private Person createPerson(Integer id, String name, String email, Integer age) {
        return new TestPersonObject(id, name, age, email, 1);
    }
}
