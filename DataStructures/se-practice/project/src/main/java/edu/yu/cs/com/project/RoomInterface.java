package edu.yu.cs.com.project;

import edu.yu.cs.com.project.people.Guest;

public interface RoomInterface {
    enum Type{
        SINGLE(1),// 1 person - 1 full size bed
        TWO_BED(2),// 2 people - 2 full size beds
        SUITE(5), // 2 bedrooms 5 people - 2 queen bed 1 pull out couch
        PRESIDENTIAL(7);// 3 bedrooms 7 people- 2 queen beds one pull out couch
        final int size;
        private Type(int p) {this.size = p;}
    }
    public Guest getCurrentGuest();
    public void setCurrentGuest(Guest g);
    /**
     * Get type of room
     */
    public Type getRoomType();
    /**
     * Set new roomType
     */
    public void setRoomType(Type t);

    public boolean isOccupied();

}
