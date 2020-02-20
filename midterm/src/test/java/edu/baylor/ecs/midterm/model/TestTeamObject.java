package edu.baylor.ecs.midterm.model;

public class TestTeamObject extends Team {
    public TestTeamObject(Integer id) {
        this.id = id;
    }

    TestTeamObject(Integer id,
                   String name,
                   Integer age,
                   String email,
                   Integer version) {
        this.id = id;
        this.name = name;
//        this.age = age;
//        this.email = email;
        this.version = version;
    }
}
