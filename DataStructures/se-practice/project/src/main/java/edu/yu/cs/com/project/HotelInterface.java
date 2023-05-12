package edu.yu.cs.com.project;

import edu.yu.cs.com.project.main.Date;
import edu.yu.cs.com.project.main.Reservation;
import edu.yu.cs.com.project.main.Room;
import edu.yu.cs.com.project.people.Guest;

import java.util.List;

public interface HotelInterface {
    public void setNumberOfRooms(int numRooms);
    public int getNumberOfRooms();
    public List<Room> getAvailRooms();
    public List<Room> getBookedRooms();
    /**
     * Find all matching rooms with the given amount of space for given int
     * @return Room that matches
     */
    public List<Room> roomSearchByCap(int cap);

    /**Search for room by the room number (should only return 1)
     *return the matching room object
     */
    public Room roomSearchByNumber(int roomNum);

    public Guest findGuest(String name, int id);

    /**
     * Book a reservation
     * @return true if it booked successfully and false if not
     * */
    public boolean setReservation(Guest guest, int cap, Date checkInDate, Date checkOutDate );


}
