package Pve;

public abstract class Room {
    protected int floor;

    public Room(int floor) {
        this.floor = floor;
    }

    public abstract void execute(Party party);

    public int getFloor() {
        return floor;
    }
}