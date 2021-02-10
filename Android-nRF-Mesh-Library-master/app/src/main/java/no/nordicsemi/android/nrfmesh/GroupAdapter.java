package no.nordicsemi.android.nrfmesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import no.nordicsemi.android.nrfmesh.node.GenericOnOffServerActivity;

import static no.nordicsemi.android.nrfmesh.R.layout.group_list;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyHolder>{

    Context context;
    List<TextString> modelList;

    int x_last=0;
    Boolean a = true;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference Ref = database.getReference("Users");
    public DatabaseReference Ref2 = Ref.child(user.getUid());
    final public DatabaseReference Ref3 = Ref2.child("group");



    public GroupAdapter(Context context, List<TextString> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(group_list, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        //String Image = modelList.get(i).getZ13_image();
        String name = modelList.get(i).getZ02_name();
        String roomnumber = modelList.get(i).getZ01_roomnumber();
        String gender  = modelList.get(i).getZ03_gender();
        String type = modelList.get(i).getZ04_type();
        String uid = modelList.get(i).getZ05_uid();
        String date = modelList.get(i).getZ06_date();
        String order1 = modelList.get(i).getZ07_order1();
        String order2 = modelList.get(i).getZ08_order2();
        String order3 = modelList.get(i).getZ09_order3();
        String main = modelList.get(i).getZ10_main();
        String resident = modelList.get(i).getZ11_resident();
        String nurse = modelList.get(i).getZ12_nurse();
        //String id = modelList.get(i).getZ13_id();

        holder.mname.setText(name);
        holder.mroomnumber.setText(roomnumber);
        holder.mgender.setText(gender);
        holder.mtype.setText(type);
        holder.muid.setText(uid);
        holder.mdate.setText(date);
        holder.morder1.setText(order1);
        holder.morder2.setText(order2);
        holder.morder3.setText(order3);
        holder.mmain.setText(main);
        holder.mresident.setText(resident);
        holder.mnurse.setText(nurse);
        //holder.mid.setText(id);
        /*try{
            Picasso.get().load(Image).placeholder(R.drawable.ic_default).into(holder.mAvatarIv);
        }catch (Exception e){

        }*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,""+roomnumber, Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = context.getSharedPreferences("room",Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.putString("roomnumber",roomnumber);
                editor.commit();
                Toast.makeText(context,""+roomnumber, Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.setClass(context, GenericOnOffServerActivity.class);
                context.startActivity(i);

            }
        });

        holder.deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref3.child(roomnumber).removeValue();
                Toast.makeText(context,"delet "+roomnumber+" in group", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void removeData(int position) {
        modelList.remove(position); //刪除動畫
        notifyItemRemoved(position);
        notifyDataSetChanged(); }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        ImageButton deletBtn;
        TextView mroomnumber, mname, mgender, mtype, muid, mdate, morder1, morder2, morder3, mmain, mresident, mnurse, mid;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarIv = itemView.findViewById(R.id.mainIcon);
            mroomnumber = itemView.findViewById(R.id.roomnumber);
            mname= itemView.findViewById(R.id.name);
            mgender = itemView.findViewById(R.id.gender);
            mtype = itemView.findViewById(R.id.type);
            muid = itemView.findViewById(R.id.uid);
            mdate = itemView.findViewById(R.id.date);
            morder1= itemView.findViewById(R.id.order1);
            morder2= itemView.findViewById(R.id.order2);
            morder3= itemView.findViewById(R.id.order3);
            mmain= itemView.findViewById(R.id.main);
            mresident= itemView.findViewById(R.id.resident);
            mnurse= itemView.findViewById(R.id.nurse);
            //mid = itemView.findViewById(R.id.id);


            deletBtn = itemView.findViewById(R.id.deletBtn);
        }
    }

}
