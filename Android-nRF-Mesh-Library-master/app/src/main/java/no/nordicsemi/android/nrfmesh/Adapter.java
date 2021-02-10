package no.nordicsemi.android.nrfmesh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{
    ArrayList<TextString> list;
    public Adapter(ArrayList<TextString> list){
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_text, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.roomnumber.setText(list.get(i).getZ01_roomnumber());
        myViewHolder.name.setText(list.get(i).getZ02_name());
        myViewHolder.gender.setText(list.get(i).getZ03_gender());
        myViewHolder.type.setText(list.get(i).getZ04_type());
        myViewHolder.uid.setText(list.get(i).getZ05_uid());
        myViewHolder.date.setText(list.get(i).getZ06_date());
        myViewHolder.order1.setText(list.get(i).getZ07_order1());
        myViewHolder.order2.setText(list.get(i).getZ08_order2());
        myViewHolder.order3.setText(list.get(i).getZ09_order3());
        myViewHolder.main.setText(list.get(i).getZ10_main());
        myViewHolder.resident.setText(list.get(i).getZ11_resident());
        myViewHolder.nurse.setText(list.get(i).getZ12_nurse());
}

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView id, roomnumber, name, gender, type, uid, date, order1, order2, order3, main, resident, nurse;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            roomnumber = itemView.findViewById(R.id.roomnumber);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            type = itemView.findViewById(R.id.type);
            uid = itemView.findViewById(R.id.uid);
            date = itemView.findViewById(R.id.date);
            order1 = itemView.findViewById(R.id.order1);
            order2 = itemView.findViewById(R.id.order2);
            order3 = itemView.findViewById(R.id.order3);
            main = itemView.findViewById(R.id.main);
            resident = itemView.findViewById(R.id.resident);
            nurse = itemView.findViewById(R.id.nurse);
        }
    }
}
