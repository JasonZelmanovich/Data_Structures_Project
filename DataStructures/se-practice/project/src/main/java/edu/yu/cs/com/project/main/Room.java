package edu.yu.cs.com.project.main;

import edu.yu.cs.com.project.RoomInterface;
import edu.yu.cs.com.project.people.Guest;

import java.util.ArrayList;
import java.util.List;

public class Room implements RoomInterface {
    private Guest current_guest;
    private boolean isBooked;
    private int roomNum;
    private Type roomType;
    private ArrayList<Reservation> reservations;

    public Room(int roomNum){
        this.roomNum = roomNum;
        this.isBooked = false;
        this.current_guest = null;
        this.reservations = new ArrayList<>();
    }

    public Guest getCurrent_guest() {
        return current_guest;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
    }

    public List<Reservation> getReservations(){
        return List.copyOf(this.reservations);
    }

    /**
     *
     * @return if no current guest return null
     */
    @Override
    public Guest getCurrentGuest() {
        return this.current_guest;
    }

    @Override
    public void setCurrentGuest(Guest g) {
        this.current_guest = g;
    }

    /**
     * Get type of room
     */
    @Override
    public Type getRoomType() {
        return this.roomType;
    }

    /**
     * Set new roomType
     *
     * @param t
     */
    @Override
    public void setRoomType(Type t) {
        if(!(t instanceof Type)){
            throw new IllegalArgumentException();
        }
        this.roomType = t;
    }

    @Override
    public boolean isOccupied() {
        return isBooked;
    }

    @Override
    public String toString() {
        return this.roomType + " " + this.roomNum;
    }

    public void setIsBooked() {
        this.isBooked = true;
    }
}
