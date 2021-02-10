package no.nordicsemi.android.nrfmesh;

public class TextString {

    private  String z01_roomnumber;
    private  String z02_name;
    private  String z03_gender;
    private  String z04_type;
    private  String z05_uid;
    private  String z06_date;
    private  String z07_order1;
    private  String z08_order2;
    private  String z09_order3;
    private  String z10_main;
    private  String z11_resident;
    private  String z12_nurse;
    private  String z13_address;

    public TextString(){}
    public TextString(String text_1, String text_2, String text_3, String text_4, String text_5, String text_6,
                      String text_7, String text_8, String text_9, String text_10, String text_11, String text_12, String text_13){
        this.z01_roomnumber=text_1;
        this.z02_name=text_2;
        this.z03_gender=text_3;
        this.z04_type=text_4;
        this.z05_uid=text_5;
        this.z06_date=text_6;
        this.z07_order1=text_7;
        this.z08_order2=text_8;
        this.z09_order3=text_9;
        this.z10_main=text_10;
        this.z11_resident=text_11;
        this.z12_nurse=text_12;
        this.z13_address=text_13;
    }

    public String getZ01_roomnumber(){
        return z01_roomnumber;
    }

    public String getZ02_name(){
        return z02_name;
    }

    public String getZ03_gender(){
        return z03_gender;
    }

    public String getZ04_type(){
        return z04_type;
    }

    public String getZ05_uid(){
        return z05_uid;
    }

    public String getZ06_date(){
        return z06_date;
    }

    public String getZ07_order1(){
        return z07_order1;
    }

    public String getZ08_order2(){
        return z08_order2;
    }

    public String getZ09_order3(){
        return z09_order3;
    }

    public String getZ10_main(){
        return z10_main;
    }

    public String getZ11_resident(){
        return z11_resident;
    }

    public String getZ12_nurse(){
        return z12_nurse;
    }

    public String getZ13_address(){ return z13_address; }

}