package no.nordicsemi.android.nrfmesh;

public class TextPatient {

    private  String z01_roomnumber;
    private  String z05_uid;
    private  String z13_address;

    public TextPatient(){}
    public TextPatient(String text_1, String text_5 ,String text_13){
        this.z01_roomnumber=text_1;
        this.z05_uid=text_5;
        this.z13_address=text_13;
    }

    public String getZ01_roomnumber(){
        return z01_roomnumber;
    }
    public String getZ05_uid(){
        return z05_uid;
    }
    public String getZ13_address(){ return z13_address; }

}