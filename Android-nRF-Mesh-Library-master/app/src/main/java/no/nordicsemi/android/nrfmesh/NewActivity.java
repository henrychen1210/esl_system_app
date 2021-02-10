package no.nordicsemi.android.nrfmesh;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.transport.ConfigModelAppStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelPublicationStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionStatus;
import no.nordicsemi.android.mesh.transport.ConfigSigModelAppList;
import no.nordicsemi.android.mesh.transport.ConfigSigModelSubscriptionList;
import no.nordicsemi.android.mesh.transport.Element;
import no.nordicsemi.android.mesh.transport.GenericOnOffSet;
import no.nordicsemi.android.mesh.transport.MeshMessage;
import no.nordicsemi.android.mesh.transport.MeshModel;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;
import no.nordicsemi.android.nrfmesh.node.BaseModelConfigurationActivity;



public class NewActivity extends AppCompatActivity {

    String roomnumber;
    String x_select;
    private int[] BW_bit;
    private int[] WR_bit;
    private byte[] BW_byte;
    private byte[] WR_byte;
    public  static boolean engineer;


    @Inject
    ViewModelProvider.Factory mViewModelFactory;


    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("data_text");
    final public static DatabaseReference myRef2 = myRef.child("data01");

    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        setupToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBar actionBar = getSupportActionBar();

        Button shot = (Button) findViewById(R.id.btn_update);
        shot.setOnClickListener(shotListener);

        Button delet = (Button) findViewById(R.id.btn_delet);
        delet.setOnClickListener(deletListener);

        Button clear = (Button) findViewById(R.id.btn_clear);
        clear.setOnClickListener(clearListener);

        //接收從FireActivity傳來的值
        Intent intent = getIntent();
        String mActionBarTitle = intent.getStringExtra("roomnumber");
        roomnumber = intent.getStringExtra("roomnumber");
        String name = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        String type = intent.getStringExtra("type");
        String uid = intent.getStringExtra("uid");
        String date = intent.getStringExtra("date");
        String order1 = intent.getStringExtra("order1");
        String order2 = intent.getStringExtra("order2");
        String order3 = intent.getStringExtra("order3");
        String main = intent.getStringExtra("main");
        String resident = intent.getStringExtra("resident");
        String nurse = intent.getStringExtra("nurse");
        x_select = intent.getStringExtra("x_select");

        TextView troomnumber = (TextView) findViewById(R.id.roomnumber);
        TextView tname = (TextView) findViewById(R.id.name);
        TextView tgender = (TextView) findViewById(R.id.gender);
        TextView ttype = (TextView) findViewById(R.id.type);
        TextView tuid = (TextView) findViewById(R.id.uid);
        TextView tdate = (TextView) findViewById(R.id.date);
        TextView torder1 = (TextView) findViewById(R.id.order1);
        TextView torder2 = (TextView) findViewById(R.id.order2);
        TextView torder3 = (TextView) findViewById(R.id.order3);
        TextView tmain = (TextView) findViewById(R.id.main);
        TextView tresident = (TextView) findViewById(R.id.resident);
        TextView tnurse = (TextView) findViewById(R.id.nurse);

        EditText sname = (EditText) findViewById(R.id.set_name);
        EditText sgender = (EditText) findViewById(R.id.set_gender);
        EditText stype = (EditText) findViewById(R.id.set_type);
        EditText suid = (EditText) findViewById(R.id.set_uid);
        EditText sdate = (EditText) findViewById(R.id.set_date);
        EditText sorder1 = (EditText) findViewById(R.id.set_order1);
        EditText sorder2 = (EditText) findViewById(R.id.set_order2);
        EditText sorder3 = (EditText) findViewById(R.id.set_order3);
        EditText smain = (EditText) findViewById(R.id.set_main);
        EditText sresident = (EditText) findViewById(R.id.set_resident);
        EditText snurse = (EditText) findViewById(R.id.set_nurse);

        actionBar.setTitle(mActionBarTitle);
        tname.setText(name);
        ttype.setText(type);
        tuid.setText(uid);
        tdate.setText(date);
        torder1.setText(order1);
        torder2.setText(order2);
        torder3.setText(order3);
        tmain.setText(main);
        tresident.setText(resident);
        tnurse.setText(nurse);
        troomnumber.setText(roomnumber);
        tgender.setText(gender);

        sname.setText(name);
        stype.setText(type);
        suid.setText(uid);
        sdate.setText(date);
        sorder1.setText(order1);
        sorder2.setText(order2);
        sorder3.setText(order3);
        smain.setText(main);
        sresident.setText(resident);
        snurse.setText(nurse);
        sgender.setText(gender);
    }

    void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //Update按紐---->更新資料庫＋截圖＋比對資料後傳輸
    public Button.OnClickListener shotListener =
            new Button.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    int name_set = 0, gender_set = 0, type_set = 0, uid_set = 0, date_set = 0, order1_set = 0, order2_set = 0,
                            order3_set = 0, qr_set = 0, maindoc_set = 0, residentdoc_set = 0, nurse_set = 0;   //set = 1 資料有異動

                    //partial array ( Height * width )
                    int[] name_part = new int[256 * 88];
                    int[] gender_part = new int[40 * 40];
                    int[] type_part = new int[440 * 24];
                    int[] uid_part = new int[440 * 24];
                    int[] date_part = new int[440 * 24];
                    int[] order1_part = new int[504 * 70];
                    int[] order2_part = new int[504 * 70];
                    int[] order3_part = new int[504 * 70];
                    int[] maindoc_part = new int[192 * 66];
                    int[] residentdoc_part = new int[192 * 66];
                    int[] nurse_part = new int[192 * 66];

                    TextView tname = (TextView) findViewById(R.id.name);
                    TextView tgender = (TextView) findViewById(R.id.gender);
                    TextView ttype = (TextView) findViewById(R.id.type);
                    TextView tuid = (TextView) findViewById(R.id.uid);
                    TextView tdate = (TextView) findViewById(R.id.date);
                    TextView torder1 = (TextView) findViewById(R.id.order1);
                    TextView torder2 = (TextView) findViewById(R.id.order2);
                    TextView torder3 = (TextView) findViewById(R.id.order3);
                    TextView tmain = (TextView) findViewById(R.id.main);
                    TextView tresident = (TextView) findViewById(R.id.resident);
                    TextView tnurse = (TextView) findViewById(R.id.nurse);

                    EditText sname = (EditText) findViewById(R.id.set_name);
                    EditText sgender = (EditText) findViewById(R.id.set_gender);
                    EditText stype = (EditText) findViewById(R.id.set_type);
                    EditText suid = (EditText) findViewById(R.id.set_uid);
                    EditText sdate = (EditText) findViewById(R.id.set_date);
                    EditText sorder1 = (EditText) findViewById(R.id.set_order1);
                    EditText sorder2 = (EditText) findViewById(R.id.set_order2);
                    EditText sorder3 = (EditText) findViewById(R.id.set_order3);
                    EditText smain = (EditText) findViewById(R.id.set_main);
                    EditText sresident = (EditText) findViewById(R.id.set_resident);
                    EditText snurse = (EditText) findViewById(R.id.set_nurse);

                    if (!tname.getText().toString().equals(sname.getText().toString())) {
                        name_set = 1;
                    }
                    if (!tgender.getText().toString().equals(sgender.getText().toString())) {
                        gender_set = 1;
                    }
                    if (!ttype.getText().toString().equals(stype.getText().toString())) {
                        type_set = 1;
                    }
                    if (!tuid.getText().toString().equals(suid.getText().toString())) {
                        uid_set = 1;
                    }
                    if (!tdate.getText().toString().equals(sdate.getText().toString())) {
                        date_set = 1;
                    }
                    if (!torder1.getText().toString().equals(sorder1.getText().toString())) {
                        order1_set = 1;
                    }
                    if (!torder2.getText().toString().equals(sorder2.getText().toString())) {
                        order2_set = 1;
                    }
                    if (!torder3.getText().toString().equals(sorder3.getText().toString())) {
                        order3_set = 1;
                    }
                    if (!tmain.getText().toString().equals(smain.getText().toString())) {
                        maindoc_set = 1;
                    }
                    if (!tresident.getText().toString().equals(sresident.getText().toString())) {
                        residentdoc_set = 1;
                    }
                    if (!tnurse.getText().toString().equals(snurse.getText().toString())) {
                        nurse_set = 1;
                    }

                    tname.setText(sname.getText().toString());
                    tgender.setText(sgender.getText().toString());
                    ttype.setText(stype.getText().toString());
                    tuid.setText(suid.getText().toString());
                    tdate.setText(sdate.getText().toString());
                    torder1.setText(sorder1.getText().toString());
                    torder2.setText(sorder2.getText().toString());
                    torder3.setText(sorder3.getText().toString());
                    tmain.setText(smain.getText().toString());
                    tresident.setText(sresident.getText().toString());
                    tnurse.setText(snurse.getText().toString());

                    try {
                        //set time in mili
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //onStart();
                    //將截圖Bitmap放入ImageView
                    ImageView mImg = (ImageView) findViewById(R.id.yaa);
                    mImg.setImageBitmap(getScreenShot());

                    if (name_set == 1) {
                        for (int y = 90, z = 0; y < 90 + 88; y++) {
                            for (int x = 65; x < 65 + 256; x++) {
                                int i = y * 880 + x;
                                name_part[z++] = BW_bit[i];
                            }
                        }

                        name_part = invert(name_part, 256, 88);
                        System.out.println();
                        System.out.println();
                        System.out.println("name part  = ");
                        for (int i = 0; i < 256 * 88; i++) {
                            //換行
                            if ((i % 256) == 0 && i != 0)
                                System.out.println();
                            System.out.print(name_part[i]);
                        }

                        byte[] name_part_byte = encodeToByteArray(name_part);

                        System.out.println();
                        System.out.println("name part (byte) = " + print(name_part_byte));
                    }



                    if (gender_set == 1) {
                        for (int y = 120, z = 0; y < 120 + 40; y++) {
                            for (int x = 391; x < 391 + 40; x++) {
                                int i = y * 880 + x;
                                gender_part[z++] = BW_bit[i];
                            }
                        }

                        gender_part = invert(gender_part, 40, 40);
                        System.out.println();
                        System.out.println();
                        System.out.println("gender part  = ");
                        for (int i = 0; i < 40 * 40; i++) {
                            //換行
                            if ((i % 40) == 0 && i != 0)
                                System.out.println();
                            System.out.print(gender_part[i]);
                        }

                        byte[] gender_part_byte = encodeToByteArray(gender_part);

                        System.out.println();
                        System.out.println("gender part (byte) = " + print(gender_part_byte));
                    }

                    if (type_set == 1) {
                        for (int y = 206, z = 0; y < 206 + 24; y++) {
                            for (int x = 83; x < 83 + 440; x++) {
                                int i = y * 880 + x;
                                type_part[z++] = BW_bit[i];
                            }
                        }

                        type_part = invert(type_part, 440, 24);
                        System.out.println();
                        System.out.println();
                        System.out.println("type part  = ");
                        for (int i = 0; i < 440 * 24; i++) {
                            //換行
                            if ((i % 440) == 0 && i != 0)
                                System.out.println();
                            System.out.print(type_part[i]);
                        }

                        byte[] type_part_byte = encodeToByteArray(type_part);

                        System.out.println();
                        System.out.println("type part (byte) = " + print(type_part_byte));
                    }

                    if (uid_set == 1) {
                        for (int y = 234, z = 0; y < 234 + 24; y++) {
                            for (int x = 83; x < 83 + 440; x++) {
                                int i = y * 880 + x;
                                uid_part[z++] = BW_bit[i];
                            }
                        }

                        uid_part = invert(uid_part, 440, 24);
                        System.out.println();
                        System.out.println();
                        System.out.println("uid part  = ");
                        for (int i = 0; i < 440 * 24; i++) {
                            //換行
                            if ((i % 440) == 0 && i != 0)
                                System.out.println();
                            System.out.print(uid_part[i]);
                        }

                        byte[] uid_part_byte = encodeToByteArray(uid_part);

                        System.out.println();
                        System.out.println("uid part (byte) = " + print(uid_part_byte));
                    }

                    if (date_set == 1) {
                        for (int y = 262, z = 0; y < 262 + 24; y++) {
                            for (int x = 83; x < 83 + 440; x++) {
                                int i = y * 880 + x;
                                date_part[z++] = BW_bit[i];
                            }
                        }

                        date_part = invert(date_part, 440, 24);
                        System.out.println();
                        System.out.println();
                        System.out.println("date part  = ");
                        for (int i = 0; i < 440 * 24; i++) {
                            //換行
                            if ((i % 440) == 0 && i != 0)
                                System.out.println();
                            System.out.print(date_part[i]);
                        }

                        byte[] date_part_byte = encodeToByteArray(date_part);

                        System.out.println();
                        System.out.println("date part (byte) = " + print(date_part_byte));

                    }

                    if (order1_set == 1) {
                        for (int y = 293, z = 0; y < 292 + 70; y++) {
                            for (int x = 15; x < 15 + 504; x++) {
                                int i = y * 880 + x;
                                order1_part[z++] = WR_bit[i];
                            }
                        }

                        order1_part = invert(order1_part, 504, 70);
                        System.out.println();
                        System.out.println();
                        System.out.println("order1 part  = ");
                        for (int i = 0; i < 504 * 70; i++) {
                            //換行
                            if ((i % 504) == 0 && i != 0)
                                System.out.println();
                            System.out.print(order1_part[i]);
                        }

                        byte[] order1_part_byte = encodeToByteArray(order1_part);

                        System.out.println();
                        System.out.println("order1 part (byte) = " + print(order1_part_byte));
                    }

                    if (order2_set == 1) {
                        //name part
                        for (int y = 369, z = 0; y < 369 + 70; y++) {
                            for (int x = 15; x < 15 + 504; x++) {
                                int i = y * 880 + x;
                                order2_part[z++] = WR_bit[i];
                            }
                        }

                        order2_part = invert(order2_part, 504, 70);
                        System.out.println();
                        System.out.println();
                        System.out.println("order2 part  = ");
                        for (int i = 0; i < 504 * 70; i++) {
                            //換行
                            if ((i % 504) == 0 && i != 0)
                                System.out.println();
                            System.out.print(order2_part[i]);
                        }

                        byte[] order2_part_byte = encodeToByteArray(order2_part);

                        System.out.println();
                        System.out.println("order2 part (byte) = " + print(order2_part_byte));
                    }


                    if (order3_set == 1) {
                        for (int y = 446, z = 0; y < 446 + 70; y++) {
                            for (int x = 15; x < 15 + 504; x++) {
                                int i = y * 880 + x;
                                order3_part[z++] = WR_bit[i];
                            }
                        }

                        order3_part = invert(order3_part, 504, 70);
                        System.out.println();
                        System.out.println();
                        System.out.println("order3 part  = ");
                        for (int i = 0; i < 504 * 70; i++) {
                            //換行
                            if ((i % 504) == 0 && i != 0)
                                System.out.println();
                            System.out.print(order3_part[i]);
                        }

                        byte[] order3_part_byte = encodeToByteArray(order3_part);

                        System.out.println();
                        System.out.println("order3 part (byte) = " + print(order3_part_byte));
                    }

                    if (maindoc_set == 1) {
                        for (int y = 296, z = 0; y < 296 + 66; y++) {
                            for (int x = 662; x < 662 + 192; x++) {
                                int i = y * 880 + x;
                                maindoc_part[z++] = WR_bit[i];
                            }
                        }

                        maindoc_part = invert(maindoc_part, 192, 66);
                        System.out.println();
                        System.out.println();
                        System.out.println("maindoc part  = ");
                        for (int i = 0; i < 192 * 66; i++) {
                            //換行
                            if ((i % 192) == 0 && i != 0)
                                System.out.println();
                            System.out.print(maindoc_part[i]);
                        }

                        byte[] maindoc_part_byte = encodeToByteArray(maindoc_part);

                        System.out.println();
                        System.out.println("maindoc part (byte) = " + print(maindoc_part_byte));
                    }

                    if (residentdoc_set == 1) {
                        for (int y = 372, z = 0; y < 372 + 66; y++) {
                            for (int x = 662; x < 662 + 192; x++) {
                                int i = y * 880 + x;
                                residentdoc_part[z++] = WR_bit[i];
                            }
                        }

                        residentdoc_part = invert(residentdoc_part, 192, 66);
                        System.out.println();
                        System.out.println();
                        System.out.println("residentdoc part  = ");
                        for (int i = 0; i < 192 * 66; i++) {
                            //換行
                            if ((i % 192) == 0 && i != 0)
                                System.out.println();
                            System.out.print(residentdoc_part[i]);
                        }

                        byte[] residentdoc_part_byte = encodeToByteArray(residentdoc_part);

                        System.out.println();
                        System.out.println("residentdoc part (byte) = " + print(residentdoc_part_byte));
                    }

                    if (nurse_set == 1) {
                        for (int y = 448, z = 0; y < 448 + 66; y++) {
                            for (int x = 662; x < 662 + 192; x++) {
                                int i = y * 880 + x;
                                nurse_part[z++] = WR_bit[i];
                            }
                        }

                        nurse_part = invert(nurse_part, 192, 66);
                        System.out.println();
                        System.out.println();
                        System.out.println("nurse part  = ");
                        for (int i = 0; i < 192 * 66; i++) {
                            //換行
                            if ((i % 192) == 0 && i != 0)
                                System.out.println();
                            System.out.print(nurse_part[i]);
                        }

                        byte[] nurse_part_byte = encodeToByteArray(nurse_part);

                        System.out.println();
                        System.out.println("nurse part (byte) = " + print(nurse_part_byte));
                    }

                    if ((name_set + gender_set + type_set + uid_set + date_set + order1_set + order2_set + order3_set + qr_set + maindoc_set + residentdoc_set + nurse_set == 11)        //qr_set未設
                            || (name_set + gender_set + type_set + uid_set + date_set + order1_set + order2_set + order3_set + qr_set + maindoc_set + residentdoc_set + nurse_set == 0)) {

                        BW_bit = invert(BW_bit, 880, 528);   //反轉
                        WR_bit = invert(WR_bit, 880, 528);   //反轉

                        //黑白bit陣列
                        System.out.println();
                        System.out.println("B & W (bits) = ");
                        //print
                        for (int i = 0; i < 880 * 528; i++) {
                            //換行
                            if ((i % 880) == 0 && i != 0)
                                System.out.println();

                            System.out.print(BW_bit[i]);
                        }

                        System.out.println();
                        System.out.println();

                        //紅白bit陣列
                        System.out.println();
                        System.out.println("W & R (bits) = ");
                        //print
                        for (int i = 0; i < 880 * 528; i++) {
                            //換行
                            if ((i % 880) == 0 && i != 0)
                                System.out.println();

                            System.out.print(WR_bit[i]);
                        }

                        System.out.println();
                        System.out.println();

                        //轉黑白byte陣列
                        BW_byte = encodeToByteArray(BW_bit);         //BW_byte 黑白hex陣列

                        System.out.println();
                        System.out.println("B & W (byte) = " + print(BW_byte));

                        System.out.println();
                        System.out.println();

                        //轉紅白byte陣列
                        WR_byte = encodeToByteArray(WR_bit);         //WR_byte 紅白hex陣列

                        System.out.println();
                        System.out.println("W & R (bytes) = " + print(WR_byte));

                        //sendGenericOnOff(true, (int) 0, gender_part_byte);

                    }
                    //更新資料
                    myRef2.child(roomnumber).setValue(new TextString(roomnumber, tname.getText().toString(),
                            tgender.getText().toString(), ttype.getText().toString(), tuid.getText().toString(), tdate.getText().toString(), torder1.getText().toString(),
                            torder2.getText().toString(), torder3.getText().toString(), tmain.getText().toString(), tresident.getText().toString(), tnurse.getText().toString(), "A102"));
                }
            };


    //刪除資料
    public Button.OnClickListener deletListener =
            new Button.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewActivity.this);//跳出詢問對話框
                    builder.setTitle("警告");
                    builder.setMessage("是否要刪除此筆資料？");
                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myRef2.child(x_select).removeValue();
                            Intent i = new Intent();
                            i.setClass(NewActivity.this, DashboardActivity.class);
                            startActivity(i);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            };

    //清空資料
    public Button.OnClickListener clearListener =
            new Button.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewActivity.this);//跳出詢問對話框
                    builder.setTitle("警告");
                    builder.setMessage("是否要清空此頁資料？");
                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextView tname = (TextView) findViewById(R.id.name);
                            TextView tgender = (TextView) findViewById(R.id.gender);
                            TextView ttype = (TextView) findViewById(R.id.type);
                            TextView tuid = (TextView) findViewById(R.id.uid);
                            TextView tdate = (TextView) findViewById(R.id.date);
                            TextView torder1 = (TextView) findViewById(R.id.order1);
                            TextView torder2 = (TextView) findViewById(R.id.order2);
                            TextView torder3 = (TextView) findViewById(R.id.order3);
                            TextView tmain = (TextView) findViewById(R.id.main);
                            TextView tresident = (TextView) findViewById(R.id.resident);
                            TextView tnurse = (TextView) findViewById(R.id.nurse);

                            EditText sname = (EditText) findViewById(R.id.set_name);
                            EditText sgender = (EditText) findViewById(R.id.set_gender);
                            EditText stype = (EditText) findViewById(R.id.set_type);
                            EditText suid = (EditText) findViewById(R.id.set_uid);
                            EditText sdate = (EditText) findViewById(R.id.set_date);
                            EditText sorder1 = (EditText) findViewById(R.id.set_order1);
                            EditText sorder2 = (EditText) findViewById(R.id.set_order2);
                            EditText sorder3 = (EditText) findViewById(R.id.set_order3);
                            EditText smain = (EditText) findViewById(R.id.set_main);
                            EditText sresident = (EditText) findViewById(R.id.set_resident);
                            EditText snurse = (EditText) findViewById(R.id.set_nurse);

                            String a = null;
                            sname.setText(a);
                            stype.setText(a);
                            suid.setText(a);
                            sdate.setText(a);
                            sorder1.setText(a);
                            sorder2.setText(a);
                            sorder3.setText(a);
                            smain.setText(a);
                            sresident.setText(a);
                            snurse.setText(a);
                            sgender.setText(a);

                            tname.setText(sname.getText().toString());
                            tgender.setText(sgender.getText().toString());
                            ttype.setText(stype.getText().toString());
                            tuid.setText(suid.getText().toString());
                            tdate.setText(sdate.getText().toString());
                            torder1.setText(sorder1.getText().toString());
                            torder2.setText(sorder2.getText().toString());
                            torder3.setText(sorder3.getText().toString());
                            tmain.setText(smain.getText().toString());
                            tresident.setText(sresident.getText().toString());
                            tnurse.setText(snurse.getText().toString());

                            Toast.makeText(getApplicationContext(), "頁面已清空", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            };

    //螢幕截圖 & partial update & 轉陣列
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Bitmap getScreenShot() {
        //藉由View來Cache全螢幕畫面後放入Bitmap
        View mView = getWindow().getDecorView();
        mView.setDrawingCacheEnabled(true);
        ImageView tx = mView.findViewById(R.id.hi);
        mView.buildDrawingCache();
        Bitmap mFullBitmap = mView.getDrawingCache();

        //取得系統狀態列高度
        Rect mRect = new Rect();
        Rect nRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);

        //取得手機螢幕長寬尺寸
        int mPhoneWidth = getWindowManager().getDefaultDisplay().getWidth();
        int mPhoneHeight = getWindowManager().getDefaultDisplay().getHeight();
        int mStatusBarHeight = mRect.top;

        //將狀態列的部分移除並建立新的Bitmap
        Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, 0, 130, tx.getWidth(), tx.getHeight() - mStatusBarHeight);
        int newwidth = 880;
        int newheight = 528;
        float scalew = ((float) newwidth / mBitmap.getWidth());
        float scaleh = ((float) newheight / mBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scalew, scaleh);
        Bitmap newbitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        BW_bit = Bitmap2BitBW(newbitmap);
        WR_bit = Bitmap2BitWR(newbitmap);


        //將Cache的畫面清除
        mView.destroyDrawingCache();
        return newbitmap;
    }

    //bitmap to 黑白陣列
    public int[] Bitmap2BitBW(Bitmap src) {
        int bmWidth = src.getWidth();
        int bmHeight = src.getHeight();
        int[] newBitmap = new int[bmWidth * bmHeight];
        int[] BW = new int[bmWidth * bmHeight];

        //印出bitmap尺寸
        System.out.println();
        System.out.println("Width " + bmWidth);
        System.out.println("Height " + bmHeight);
        System.out.println();

        src.getPixels(newBitmap, 0, bmWidth, 0, 0, bmWidth, bmHeight);

        for (int h = 0; h < bmHeight; h++) {
            for (int w = 0; w < bmWidth; w++) {
                int index = h * bmWidth + w;

                int a = (newBitmap[index] >> 24) & 0xff;//讀取Alpha
                int r = (newBitmap[index] >> 16) & 0xff;//讀取red
                int g = (newBitmap[index] >> 8) & 0xff;//讀取green
                int b = newBitmap[index] & 0xff;//讀取blue

                if ((r <= 160) && (g <= 160) && (b <= 160))//濾出黑色(0為黑，1為白)
                    BW[index] = 1;
                else
                    BW[index] = 0;
            }
        }

        return BW;
    }

    //bitmap to 紅白陣列
    public int[] Bitmap2BitWR(Bitmap src) {
        int bmWidth = src.getWidth();
        int bmHeight = src.getHeight();
        int[] newBitmap = new int[bmWidth * bmHeight];

        int[] WR = new int[bmWidth * bmHeight];

        src.getPixels(newBitmap, 0, bmWidth, 0, 0, bmWidth, bmHeight);

        for (int h = 0; h < bmHeight; h++) {
            for (int w = 0; w < bmWidth; w++) {
                int index = h * bmWidth + w;

                int a = (newBitmap[index] >> 24) & 0xff;//讀取Alpha
                int r = (newBitmap[index] >> 16) & 0xff;//讀取red
                int g = (newBitmap[index] >> 8) & 0xff;//讀取green
                int b = newBitmap[index] & 0xff;//讀取blue

                if ((r >= 184) && (g <= 50) && (b <= 50))//濾出紅色(0為紅，1為白)
                    WR[index] = 0;
                else
                    WR[index] = 1;
            }
        }

        return WR;
    }


    //反轉陣列
    public int[] invert(int[] array, int width, int height) {

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int index = h * width + w;
                int Rindex = (h + 1) * width - 1 - w;

                if (index >= Rindex) {
                    w = width;
                    break;
                } else {
                    int temp = array[index];
                    array[index] = array[Rindex];
                    array[Rindex] = temp;
                }
            }
        }
        return array;
    }

    //bit to byte array
    private static byte[] encodeToByteArray(int[] bits) {

        byte[] results = new byte[bits.length / 8];
        String binaryString, hex;

        for (int i = 0; i < bits.length / 8; i++) {
            //取陣列裡8bit
            binaryString = Integer.toString(bits[i * 8]) + Integer.toString(bits[i * 8 + 1]) + Integer.toString(bits[i * 8 + 2]) + Integer.toString(bits[i * 8 + 3]) + Integer.toString(bits[i * 8 + 4]) + Integer.toString(bits[i * 8 + 5]) + Integer.toString(bits[i * 8 + 6]) + Integer.toString(bits[i * 8 + 7]);

            hex = Integer.toHexString(Integer.parseInt(binaryString, 2));//二進制轉十進制轉十六進制

            results[i] = ((byte) Integer.parseInt(hex, 16));

        }
        return results;
    }

    public static String print(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format("0x%02X, ", b));
        }
        sb.append("]");
        return sb.toString();
    }

    /*

    public void sendGenericOnOff(final boolean state, final Integer delay, final byte[] eslmap) {
        if (!checkConnectivity()) return;
        final ProvisionedMeshNode node = mViewModel.getSelectedMeshNode().getValue();
        if (node != null) {
            final Element element = mViewModel.getSelectedElement().getValue();
            if (element != null) {
                final MeshModel model = mViewModel.getSelectedModel().getValue();
                if (model != null) {
                    if (!model.getBoundAppKeyIndexes().isEmpty()) {
                        final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                        final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);
                        final int address = element.getElementAddress();

                        String eslstring = "";

                        for(int i = 0; i < eslmap.length/18; i++){
                            for(int n = 0; n < 18; n++ ){
                                eslstring = eslstring + Byte.toString(eslmap[i*18 + n]);
                            }
                            final GenericOnOffSet eslset = new GenericOnOffSet(appKey, state,
                                    new Random().nextInt(), Byte.parseByte(eslstring), mTransitionSteps, mTransitionStepResolution, delay);
                            sendMessage(address, eslset);
                            eslstring = "";
                        }

                    } else {
                        //mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                    }
                }
            }
        }
    }

     */

}







