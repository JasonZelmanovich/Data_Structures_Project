package edu.yu.cs.com.project.people;

import java.util.Objects;

public abstract class Person {
    String name;
    int age;
    int ID;

    public int getAge() {
        return age;
    }

    public int getID(){
        return ID;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return getID() == person.getID() && Objects.equals(getName(), person.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), getID());
    }


}
