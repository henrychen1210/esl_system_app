package no.nordicsemi.android.nrfmesh;

public class TextGroup {
    private String roomnumber;

    public TextGroup(){}
    public TextGroup(String roomnumber) {
        this.roomnumber = roomnumber;
    }

    public String getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(String roomnumber) {
        this.roomnumber = roomnumber;
    }
}