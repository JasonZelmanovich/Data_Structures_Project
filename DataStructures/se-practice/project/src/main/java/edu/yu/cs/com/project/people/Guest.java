package edu.yu.cs.com.project.people;

import edu.yu.cs.com.project.people.Person;

public class Guest extends Person {

    private int cardNumber;

    public Guest(String name, int id){
        this.name = name;
        this.ID = id;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setCreditCardNumber(int cardNumber){
        this.cardNumber = cardNumber;
    }

    public int getCardNumber() {
        return this.cardNumber;
    }
}
