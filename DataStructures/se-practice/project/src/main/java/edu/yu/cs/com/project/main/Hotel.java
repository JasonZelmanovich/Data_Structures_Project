package edu.yu.cs.com.project.main;

import edu.yu.cs.com.project.HotelInterface;
import edu.yu.cs.com.project.RoomInterface;
import edu.yu.cs.com.project.RoomInterface.Type;
import edu.yu.cs.com.project.people.Employee;
import edu.yu.cs.com.project.people.Guest;
import edu.yu.cs.com.project.util.Util;

import java.util.*;
import edu.yu.cs.com.project.util.Util;
public class Hotel implements HotelInterface {
    private ArrayList<Room> rooms;
    private Employee[] employees;
    private ArrayList<Reservation> reservations;
    private String hotelName;
    private Map<Guest,Reservation> guestReservationMap = new HashMap<>();

    public Hotel(String hotelName){
        this.hotelName = hotelName;
        this.rooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }


    @Override
    public void setNumberOfRooms(int numRooms) {
        if(numRooms < 10) throw new IllegalArgumentException("Hotel must have more then 10 rooms");
        //of the numbers of room, there is one presidnatial, then from the reaming 20% are suits, 50% are two bed 30% are single
        int numOfPres = 1;
        numRooms--;
        int numOfSuits = (int) Math.floor((20.0 / 100.0) * numRooms);
        int numOfDouble = (int) Math.floor((50.0 / 100.0) * numRooms);
        int numOfSingle = (int) Math.floor((30.0 / 100.0) * numRooms);
        numRooms++;
        int count = 1;
        count = createRooms(numOfSingle,count, Type.SINGLE);
        System.out.println(count);
        count = createRooms(numOfDouble,count, Type.TWO_BED);
        System.out.println(count);
        count = createRooms(numOfSuits,count, Type.SUITE);
        System.out.println(count);
        count = createRooms(numOfPres,count,Type.PRESIDENTIAL);
        createRooms(numRooms - count+1,count,Type.SINGLE);
    }

    private int createRooms(int numOfRoom, int roomNumber, Type type){
        for(int i = 0; i < numOfRoom; i++){
            Room newRoom = new Room(roomNumber);
            newRoom.setRoomType(type);
            this.rooms.add(newRoom);
            roomNumber++;
        }
        return roomNumber;
    }

    @Override
    public int getNumberOfRooms() {
        return this.rooms.size();
    }

    @Override
    public List<Room> getAvailRooms() {
        ArrayList<Room> results = new ArrayList<>();
        for(Room r : this.rooms){
            if(!r.isBooked()){
                results.add(r);
            }
        }
        return results;
    }

    @Override
    public List<Room> getBookedRooms() {
        ArrayList<Room> results = new ArrayList<>();
        for(Room r : this.rooms){
            if(r.isBooked()){
                results.add(r);
            }
        }
        return results;
    }

    /**
     * Find all matching rooms with the given amount of capacity
     *
     * @param cap
     * @return Room that matches
     */
    @Override
    public List<Room> roomSearchByCap(int cap) {
        ArrayList<Room> results = new ArrayList<>();
        switch (cap){
            case 1: results.addAll(getAllRooms(Type.SINGLE)); break;//return all rooms with single beds
            case 2: results.addAll(getAllRooms(Type.TWO_BED)); break;//return all double rooms
            case 3: results.addAll(getAllRooms(Type.SUITE)); break;//suits
            case 4: results.addAll(getAllRooms(Type.SUITE)); break;//return all suit rooms
            case 5: results.addAll(getAllRooms(Type.SUITE)); break;//return all suite rooms
            case 6: results.addAll(getAllRooms(Type.PRESIDENTIAL)); break;//return presdident room
            case 7: results.addAll(getAllRooms(Type.PRESIDENTIAL)); break;//return president room
            default://room doesnt exists
        }
        return results;
    }

    private List<Room> getAllRooms(RoomInterface.Type type){
        ArrayList<Room> results = new ArrayList<>();
        for(Room r : this.rooms){
            if(r.getRoomType() == type){
                results.add(r);
            }
        }
        return results;
    }

    /**
     * Search for room by the room number (should only return 1)
     * return the matching room object
     *
     * @param roomNum
     */
    @Override
    public Room roomSearchByNumber(int roomNum) {
        return this.rooms.get(roomNum - 1);
    }

    @Override
    public Guest findGuest(String name, int id) {
        Guest temp = new Guest(name,id);
        for(Room r : this.rooms){
            if(r.getCurrentGuest().equals(temp)){
                return r.getCurrentGuest();
            }
        }
        return null; //coudnt find guesr
    }




    @Override
    public boolean setReservation(Guest guest, int cap, Date checkInDate, Date checkOutDate) {
        Reservation reservation = new Reservation(guest,checkInDate,checkOutDate,-1, Util.generateReservationId());
        List<Room> rooms = roomSearchByCap(cap);
        Room room = getSpecifiedRoom(rooms);
        if(room == null) return false;//no room for this cap
        //if there is a reservation that overlaps with this one return false
        if(isOverlapping(room.getReservations(),reservation)){
            return false;
        }
        reservation.setRoomNumber(room.getRoomNum());
        room.setIsBooked();
        room.addReservation(reservation);
        room.setCurrentGuest(guest);
        //if not the reservation and return true
        this.guestReservationMap.put(guest,reservation);
        this.reservations.add(reservation);
        return false;
    }

    private Room getSpecifiedRoom(List<Room> rooms) {
        for(Room r : rooms){
            if(!r.isBooked()){
                return r;
            }
        }
        return null; //no room is avilible for this cap;
    }

    private boolean isOverlapping(List<Reservation> reservations, Reservation newReservation) {
        // Check if the new reservation overlaps with any existing reservations
        for (Reservation reservation : reservations) {
            if (reservation.overlapsWith(newReservation)) {
                // The new reservation overlaps with an existing reservation
                return true;
            }
        }
        // The new reservation does not overlap with any existing reservations
        return false;
    }


}
