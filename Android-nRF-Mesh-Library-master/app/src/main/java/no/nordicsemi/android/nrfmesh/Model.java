package no.nordicsemi.android.nrfmesh;

public class Model {
    String roomnumber, name, gender, type, uid, date, order1, order2, order3, main, resident, nurse, id;
    int mainicon;

    public Model(String roomnumber, String name, String gender, String type, String uid, String date, String order1,
                 String order2, String order3, String main, String resident, String nurse, String id, int mainicon) {
        this.roomnumber = roomnumber;
        this.name = name;
        this.gender = gender;
        this.type = type;
        this.uid = uid;
        this.date = date;
        this.order1 = order1;
        this.order2 = order2;
        this.order3 = order3;
        this.main = main;
        this.resident = resident;
        this.nurse = nurse;
        this.id = id;
        //this.mainicon = mainicon;
    }

    public String getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(String roomnumber) {
        this.roomnumber = roomnumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrder1() {
        return order1;
    }

    public void setOrder1(String order1) {
        this.order1 = order1;
    }

    public String getOrder2() {
        return order2;
    }

    public void setOrder2(String order2) {
        this.order2 = order2;
    }

    public String getOrder3() {
        return order3;
    }

    public void setOrder3(String order3) {
        this.order3 = order3;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getResident() {
        return resident;
    }

    public void setResident(String resident) {
        this.resident = resident;
    }

    public String getNurse() {
        return nurse;
    }

    public void setNurse(String nurse) {
        this.nurse = nurse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMainicon() {
        return mainicon;
    }

    public void setMainicon(int mainicon) {
        this.mainicon = mainicon;
    }
}
