package edu.yu.cs.com.project.main;

import edu.yu.cs.com.project.people.Guest;


import java.util.concurrent.TimeUnit;



public class Reservation {
    private Guest guest;
    private int roomNumber;
    private String reservationID;
    private Date checkInDate;
    private Date checkOutDate;

    public Reservation(Guest guest, Date checkInDate, Date checkOutDate, int roomNumber, String reservationID) {
        this.guest = guest;
        this.roomNumber = roomNumber;
        this.reservationID = reservationID;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        if(checkInDate.getCurrentVal() > checkOutDate.getCurrentVal()){
            throw new IllegalArgumentException("Checkout cannot be before Checkin");
        }
    }

    public int getLengthOfStay() {
        int lengthOfStay = this.checkInDate.compareTo(checkOutDate);
        return lengthOfStay;
    }

    public Guest getGuestName() {
        return this.guest;
    }

    public void setGuestName(Guest guest) {
        this.guest = guest;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public boolean overlapsWith(Reservation other) {
        // Check if the check-in date of the other reservation is before the check-out date of this reservation
        if (other.getCheckInDate().compareTo(this.checkOutDate) < 0) {
            // Check if the check-out date of the other reservation is after the check-in date of this reservation
            if (other.getCheckOutDate().compareTo(this.checkInDate) > 0) {
                // The reservations overlap
                return true;
            }
        }
        // The reservations do not overlap
        return false;
    }
}
