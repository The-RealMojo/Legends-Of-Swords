package Pve;

public abstract class Room {

    protected int roomNumber;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    // All rooms must implement this
    public abstract void enter(Party party);
}