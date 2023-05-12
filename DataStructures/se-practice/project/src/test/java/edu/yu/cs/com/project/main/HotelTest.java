package edu.yu.cs.com.project.main;

import edu.yu.cs.com.project.people.Guest;
import edu.yu.cs.com.project.util.Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {

    @Test
    void setNumberOfRooms() {
        Hotel hotelTest = new Hotel("Jz");
        hotelTest.setNumberOfRooms(100);
        System.out.println(hotelTest.getAvailRooms());

        System.out.println(hotelTest.roomSearchByCap(1));
        System.out.println(hotelTest.roomSearchByCap(2));
        System.out.println(hotelTest.roomSearchByCap(3));
        System.out.println(hotelTest.roomSearchByCap(6));
        System.out.println(hotelTest.roomSearchByCap(10));

        System.out.println(hotelTest.roomSearchByNumber(2));

        hotelTest.setReservation(new Guest("Jason", Util.generateID()),3,new Date(2,22),new Date(2,27));
        hotelTest.setReservation(new Guest("Koop", Util.generateID()),3,new Date(1,22),new Date(1,27));
        String b = "B";
    }

    @Test
    void getNumberOfRooms() {
    }

    @Test
    void getAvailRooms() {
    }

    @Test
    void getOccupiedRooms() {
    }

    @Test
    void roomSearchByBeds() {
    }

    @Test
    void roomSearchByNumber() {
    }

    @Test
    void findGuest() {
    }
}