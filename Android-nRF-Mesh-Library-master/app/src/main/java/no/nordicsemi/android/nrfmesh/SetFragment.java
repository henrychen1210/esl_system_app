package no.nordicsemi.android.nrfmesh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.appcompat.widget.SearchView;

public class SetFragment extends Fragment {
    FirebaseAuth firebaseAuth;

    Button logout;

    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("data_text");
    final public static DatabaseReference myRef2 = myRef.child("data01");
    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///

    SwipeRefreshLayout refreshLayout;

    BroadcastReceiver minuteUpdateReceiver;
    public SetFragment () {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        logout = (Button)view.findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();
                SharedPreferences.Editor edit = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE).edit();
                edit.clear();
                edit.commit();
            }
        });

        return view;

    }


    @Override
    public void onCreate(@NonNull Bundle saveInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(saveInstanceState);
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user !=null){
            //user is signed is stay here
            //set email of logged in user
        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(),FirebaseActivity.class));
            getActivity().finish();
        }
    }

}