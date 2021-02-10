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
import android.widget.Toast;

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

public class MygroupFragment extends Fragment {

    RecyclerView recyclerView;
    GroupAdapter adapterModel;
    List<TextString> modelList;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    Button deletBtn;

    String[] group={};
    Context context;
    int i = 0;

    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("data_text");
    final public static DatabaseReference myRef2 = myRef.child("data01");
    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///

    public FirebaseDatabase database1 = FirebaseDatabase.getInstance();
    public DatabaseReference Ref = database1.getReference("Users");
    public DatabaseReference Ref2 = Ref.child(user.getUid());
    final public DatabaseReference Ref3 = Ref2.child("group");

    SwipeRefreshLayout refreshLayout;

    BroadcastReceiver minuteUpdateReceiver;
    public MygroupFragment () {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.searchView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        modelList = new ArrayList<>();

        getAllModel();

        refreshLayout = view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllModel();
                refreshLayout.setRefreshing(false);
            }
        });
        return view;

    }


    private void getAllModel(){
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    TextString model = ds.getValue(TextString.class);

                    Ref3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds: snapshot.getChildren()){
                                TextGroup group = ds.getValue(TextGroup.class);
                                if(model.getZ01_roomnumber().toLowerCase().equals(group.getRoomnumber().toLowerCase())){
                                    modelList.add(model);
                                    adapterModel = new GroupAdapter(getActivity(), modelList);
                                    recyclerView.setAdapter(adapterModel);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ///＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊刷新介面＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊///
    public void startMinuteUpdater(){ //每分鐘重新擷取一次資料庫
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                getAllModel();
            }
        };
        requireActivity().registerReceiver(minuteUpdateReceiver,intentFilter);
    }

    Boolean  update=false;
    @Override
    public void onPause() { //activity暫停時將minuteUpdateReceiver關閉
        // TODO Auto-generated method stub
        super.onPause();
        update=true;
        requireActivity().unregisterReceiver(minuteUpdateReceiver);
    }

    @Override
    public void onResume() { //從pause中解除後先刷新一次 之後每分鐘刷新一次
        // TODO Auto-generated method stub
        super.onResume();
        if (update) {
            getAllModel();
            update = false;
        }
        startMinuteUpdater();
    }
    ///＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊刷新介面＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊///

    @Override
    public void onCreate(@NonNull Bundle saveInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(saveInstanceState);
    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUser(query);
                }
                else{
                    getAllModel();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUser(newText);
                }
                else{
                    getAllModel();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchUser(String s) {
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    TextString model = ds.getValue(TextString.class);
                    if(model.getZ01_roomnumber().toLowerCase().contains(s.toLowerCase()) ||
                            model.getZ02_name().toLowerCase().contains(s.toLowerCase()) ||
                            model.getZ05_uid().toLowerCase().contains(s.toLowerCase())){
                        modelList.add(model);
                    }

                    adapterModel = new GroupAdapter(getActivity(), modelList);
                    adapterModel.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            SharedPreferences.Editor edit = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE).edit();
            edit.clear();
            edit.commit();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkUserStatus(){
        //get current user
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


    public class ItemMoveSwipeListener {
    }
}