/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmesh.node;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.Group;
import no.nordicsemi.android.mesh.MeshNetwork;
import no.nordicsemi.android.mesh.models.ConfigurationClientModel;
import no.nordicsemi.android.mesh.models.ConfigurationServerModel;
import no.nordicsemi.android.mesh.models.SigModel;
import no.nordicsemi.android.mesh.models.SigModelParser;
import no.nordicsemi.android.mesh.transport.ConfigModelAppBind;
import no.nordicsemi.android.mesh.transport.ConfigModelAppStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelAppUnbind;
import no.nordicsemi.android.mesh.transport.ConfigModelPublicationGet;
import no.nordicsemi.android.mesh.transport.ConfigModelPublicationSet;
import no.nordicsemi.android.mesh.transport.ConfigModelPublicationStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionAdd;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionDelete;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionVirtualAddressAdd;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionVirtualAddressDelete;
import no.nordicsemi.android.mesh.transport.ConfigSigModelAppGet;
import no.nordicsemi.android.mesh.transport.ConfigSigModelAppList;
import no.nordicsemi.android.mesh.transport.ConfigSigModelSubscriptionGet;
import no.nordicsemi.android.mesh.transport.ConfigSigModelSubscriptionList;
import no.nordicsemi.android.mesh.transport.ConfigVendorModelAppGet;
import no.nordicsemi.android.mesh.transport.ConfigVendorModelSubscriptionGet;
import no.nordicsemi.android.mesh.transport.Element;
import no.nordicsemi.android.mesh.transport.GenericOnOffSet;
import no.nordicsemi.android.mesh.transport.GenericOnOffStatus;
import no.nordicsemi.android.mesh.transport.MeshMessage;
import no.nordicsemi.android.mesh.transport.MeshModel;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;
import no.nordicsemi.android.mesh.transport.PublicationSettings;
import no.nordicsemi.android.mesh.utils.CompositionDataParser;
import no.nordicsemi.android.mesh.utils.MeshAddress;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;
import no.nordicsemi.android.nrfmesh.GroupCallbacks;
import no.nordicsemi.android.nrfmesh.NewActivity;
import no.nordicsemi.android.nrfmesh.R;
import no.nordicsemi.android.nrfmesh.TextAddress;
import no.nordicsemi.android.nrfmesh.TextGroup;
import no.nordicsemi.android.nrfmesh.TextString;
import no.nordicsemi.android.nrfmesh.adapter.GroupAddressAdapter;
import no.nordicsemi.android.nrfmesh.di.Injectable;
import no.nordicsemi.android.nrfmesh.dialog.DialogFragmentConfigStatus;
import no.nordicsemi.android.nrfmesh.dialog.DialogFragmentDisconnected;
import no.nordicsemi.android.nrfmesh.dialog.DialogFragmentError;
import no.nordicsemi.android.nrfmesh.dialog.DialogFragmentGroupSubscription;
import no.nordicsemi.android.nrfmesh.dialog.DialogFragmentTransactionStatus;
import no.nordicsemi.android.nrfmesh.keys.AppKeysActivity;
import no.nordicsemi.android.nrfmesh.keys.adapter.BoundAppKeysAdapter;
import no.nordicsemi.android.nrfmesh.utils.Utils;
import no.nordicsemi.android.nrfmesh.viewmodels.ModelConfigurationViewModel;
import no.nordicsemi.android.nrfmesh.widgets.ItemTouchHelperAdapter;
import no.nordicsemi.android.nrfmesh.widgets.RemovableItemTouchHelperCallback;
import no.nordicsemi.android.nrfmesh.widgets.RemovableViewHolder;

import static no.nordicsemi.android.nrfmesh.utils.Utils.BIND_APP_KEY;
import static no.nordicsemi.android.nrfmesh.utils.Utils.CONNECT_TO_NETWORK;
import static no.nordicsemi.android.nrfmesh.utils.Utils.EXTRA_DATA;
import static no.nordicsemi.android.nrfmesh.utils.Utils.MESSAGE_TIME_OUT;
import static no.nordicsemi.android.nrfmesh.utils.Utils.RESULT_KEY;
import static no.nordicsemi.android.nrfmesh.utils.Utils.SELECT_KEY;

public abstract class BaseModelConfigurationActivity extends AppCompatActivity implements Injectable,
        GroupCallbacks,
        ItemTouchHelperAdapter,
        DialogFragmentDisconnected.DialogFragmentDisconnectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String DIALOG_FRAGMENT_CONFIGURATION_STATUS = "DIALOG_FRAGMENT_CONFIGURATION_STATUS";
    private static final String PROGRESS_BAR_STATE = "PROGRESS_BAR_STATE";

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.container)
    CoordinatorLayout mContainer;
    @BindView(R.id.app_key_card)
    View mContainerAppKeyBinding;
    @BindView(R.id.action_bind_app_key)
    Button mActionBindAppKey;
    @BindView(R.id.bound_keys)
    TextView mAppKeyView;
    @BindView(R.id.unbind_hint)
    TextView mUnbindHint;

    @BindView(R.id.publish_address_card)
    View mContainerPublication;
    @BindView(R.id.action_set_publication)
    Button mActionSetPublication;
    @BindView(R.id.action_clear_publication)
    Button mActionClearPublication;
    @BindView(R.id.publish_address)
    TextView mPublishAddressView;

    @BindView(R.id.subscription_address_card)
    View mContainerSubscribe;
    @BindView(R.id.action_subscribe_address)
    Button mActionSubscribe;
    @BindView(R.id.subscribe_addresses)
    TextView mSubscribeAddressView;
    @BindView(R.id.subscribe_hint)
    TextView mSubscribeHint;
    @BindView(R.id.configuration_progress_bar)
    ProgressBar mProgressbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipe;

    @BindView(R.id.engineer)
    LinearLayout mEngineer;
    @BindView(R.id.build)
    LinearLayout mBuild;

    ImageView imageView;


    //****************************************//
    public static String roomnumber;
    public String uid;
    //public static int address;
    //public static String  sAddress;

    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("data_text");
    final public static DatabaseReference myRef2 = myRef.child("data01");
    public static DatabaseReference myRef3 = database.getReference("Patient");

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ///＊＊＊＊＊＊＊＊＊＊＊以靜態的方式宣告資料庫＊＊＊＊＊＊＊＊＊＊///

    int name_set = 0, gender_set = 0, type_set = 0, uid_set = 0, date_set = 0, order1_set = 0, order2_set = 0,
            order3_set = 0, maindoc_set = 0, residentdoc_set = 0, nurse_set = 0;   //set = 1 資料有異動

    private int[] BW_bit;
    private int[] WR_bit;

    int[] name_part = new int[280 * 88];
    int[] gender_part = new int[48 * 48];
    int[] type_part = new int[280 * 32];
    int[] uid_part = new int[280 * 32];
    int[] qrcode_part = new int[176 * 152];
    int[] date_part = new int[280 * 24];
    int[] order1_part = new int[504 * 64];
    int[] order2_part = new int[504 * 56];
    int[] order3_part = new int[504 * 56];
    int[] maindoc_part = new int[184 * 56];
    int[] residentdoc_part = new int[184 * 56];
    int[] nurse_part = new int[184 * 56];

    //****************************************//

    protected Handler mHandler;
    protected ModelConfigurationViewModel mViewModel;
    protected List<Integer> mGroupAddress = new ArrayList<>();
    protected List<Integer> mKeyIndexes = new ArrayList<>();
    protected GroupAddressAdapter mSubscriptionAdapter;
    protected BoundAppKeysAdapter mBoundAppKeyAdapter;
    protected Button mActionRead;
    protected Button mActionSetRelayState;
    protected Button mSetNetworkTransmitStateButton;

    private RecyclerView recyclerViewBoundKeys, recyclerViewAddresses;
    protected boolean mIsConnected;
    private Object StorageReference;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_configuration);
        ButterKnife.bind(this);
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(ModelConfigurationViewModel.class);
        mHandler = new Handler();

        final MeshModel meshModel = mViewModel.getSelectedModel().getValue();
        //noinspection ConstantConditions
        //final String modelName = meshModel.getModelName();

        // Set up views
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(modelName);
        //*******************************************//
        SharedPreferences shared = getSharedPreferences("room", MODE_PRIVATE);
        roomnumber = shared.getString("roomnumber", "");

        SharedPreferences shared1 = getSharedPreferences("mode", MODE_PRIVATE);
        boolean engineer = shared1.getBoolean("engineer", false);

        SharedPreferences shared2 = getSharedPreferences("build", MODE_PRIVATE);
        boolean build = shared2.getBoolean("building", false);

        Button shot = (Button) findViewById(R.id.btn_update) ;
        shot.setOnClickListener(shotListener);

        //Button delet = (Button) findViewById(R.id.btn_delet) ;
        //delet.setOnClickListener(deletListener);

        Button clear = (Button) findViewById(R.id.btn_clear) ;
        clear.setOnClickListener(clearListener);

        Button background = (Button) findViewById(R.id.btn_background) ;
        background.setOnClickListener(backgroundListener);

        if(!engineer){
            mEngineer.setVisibility(View.GONE);
            background.setVisibility(View.GONE);
        }

        if(build && engineer){
            ImageView epaper = (ImageView)findViewById(R.id.imageView);
            TextView troomnumber=(TextView)findViewById(R.id.roomnumber);
            TextView tname=(TextView)findViewById(R.id.name);
            TextView tgender=(TextView)findViewById(R.id.gender);
            TextView ttype=(TextView)findViewById(R.id.type);
            TextView tuid=(TextView)findViewById(R.id.uid);
            TextView tdate=(TextView)findViewById(R.id.date);
            TextView torder1=(TextView)findViewById(R.id.order1);
            TextView torder2=(TextView)findViewById(R.id.order2);
            TextView torder3=(TextView)findViewById(R.id.order3);
            TextView tmain=(TextView)findViewById(R.id.main);
            TextView tresident=(TextView)findViewById(R.id.resident);
            TextView tnurse=(TextView)findViewById(R.id.nurse);
            TextView taddress=(TextView)findViewById(R.id.address);
            TextView textView3=(TextView)findViewById(R.id.textView3);


            //epaper.setVisibility(View.GONE);
            //troomnumber.setVisibility(View.GONE);
            imageView = (ImageView)findViewById(R.id.iqrcode);
            imageView.setVisibility(View.GONE);
            tname.setVisibility(View.GONE);
            tgender.setVisibility(View.GONE);
            ttype.setVisibility(View.GONE);
            tuid.setVisibility(View.GONE);
            tdate.setVisibility(View.GONE);
            torder1.setVisibility(View.GONE);
            torder2.setVisibility(View.GONE);
            torder3.setVisibility(View.GONE);
            tmain.setVisibility(View.GONE);
            tresident.setVisibility(View.GONE);
            tnurse.setVisibility(View.GONE);
            taddress.setVisibility(View.GONE);
            mBuild.setVisibility(View.GONE);
            shot.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);


        }

        if(engineer && !build){
            mEngineer.setVisibility(View.GONE);
            background.setVisibility(View.GONE);
        }

        getSupportActionBar().setTitle(roomnumber);


        TextView troomnumber=(TextView)findViewById(R.id.roomnumber);
        TextView tname=(TextView)findViewById(R.id.name);
        TextView tgender=(TextView)findViewById(R.id.gender);
        TextView ttype=(TextView)findViewById(R.id.type);
        TextView tuid=(TextView)findViewById(R.id.uid);
        TextView tdate=(TextView)findViewById(R.id.date);
        TextView torder1=(TextView)findViewById(R.id.order1);
        TextView torder2=(TextView)findViewById(R.id.order2);
        TextView torder3=(TextView)findViewById(R.id.order3);
        TextView tmain=(TextView)findViewById(R.id.main);
        TextView tresident=(TextView)findViewById(R.id.resident);
        TextView tnurse=(TextView)findViewById(R.id.nurse);
        TextView taddress=(TextView)findViewById(R.id.address);

        EditText sname = (EditText) findViewById(R.id.set_name);
        EditText sgender = (EditText) findViewById(R.id.set_gender);
        EditText stype = (EditText) findViewById(R.id.set_type);
        EditText suid = (EditText) findViewById(R.id.set_uid);
        EditText sdate = (EditText)findViewById(R.id.set_date);
        EditText sorder1 = (EditText) findViewById(R.id.set_order1);
        EditText sorder2 = (EditText)findViewById(R.id.set_order2);
        EditText sorder3 = (EditText) findViewById(R.id.set_order3);
        EditText smain = (EditText)findViewById(R.id.set_main);
        EditText sresident = (EditText) findViewById(R.id.set_resident);
        EditText snurse = (EditText) findViewById(R.id.set_nurse);

        troomnumber.setText(roomnumber);


        imageView = (ImageView)findViewById(R.id.iqrcode);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    TextString model = ds.getValue(TextString.class);
                    if(model.getZ01_roomnumber().toLowerCase().equals(roomnumber.toLowerCase())){

                        uid = model.getZ05_uid();
                        downLoadWithBytes();

                        tname.setText(model.getZ02_name());
                        ttype.setText(model.getZ04_type());
                        tuid.setText(model.getZ05_uid());
                        tdate.setText(model.getZ06_date());
                        torder1.setText(model.getZ07_order1());
                        torder2.setText(model.getZ08_order2());
                        torder3.setText(model.getZ09_order3());
                        tmain.setText(model.getZ10_main());
                        tresident.setText(model.getZ11_resident());
                        tnurse.setText(model.getZ12_nurse());
                        tgender.setText(model.getZ03_gender());
                        getSupportActionBar().setSubtitle("unicastAddress: " + model.getZ13_address());
                        taddress.setText(model.getZ13_address());

                        sname.setText(model.getZ02_name());
                        stype.setText(model.getZ04_type());
                        suid.setText(model.getZ05_uid());
                        sdate.setText(model.getZ06_date());
                        sorder1.setText(model.getZ07_order1());
                        sorder2.setText(model.getZ08_order2());
                        sorder3.setText(model.getZ09_order3());
                        smain.setText(model.getZ10_main());
                        sresident.setText(model.getZ11_resident());
                        snurse.setText(model.getZ12_nurse());
                        sgender.setText(model.getZ03_gender());
                    }

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //*******************************************//

        final int modelId = 0x1000;
        //getSupportActionBar().setSubtitle(getString(R.string.model_id, CompositionDataParser.formatModelIdentifier(modelId, true)));

        recyclerViewAddresses = findViewById(R.id.recycler_view_addresses);
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));
        final ItemTouchHelper.Callback itemTouchHelperCallback = new RemovableItemTouchHelperCallback(this);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewAddresses);
        mSubscriptionAdapter = new GroupAddressAdapter(this, mViewModel.getNetworkLiveData().getMeshNetwork(), mViewModel.getSelectedModel());
        recyclerViewAddresses.setAdapter(mSubscriptionAdapter);

        recyclerViewBoundKeys = findViewById(R.id.recycler_view_bound_keys);
        recyclerViewBoundKeys.setLayoutManager(new LinearLayoutManager(this));
        final ItemTouchHelper.Callback itemTouchHelperCallbackKeys = new RemovableItemTouchHelperCallback(this);
        final ItemTouchHelper itemTouchHelperKeys = new ItemTouchHelper(itemTouchHelperCallbackKeys);
        itemTouchHelperKeys.attachToRecyclerView(recyclerViewBoundKeys);
        mBoundAppKeyAdapter = new BoundAppKeysAdapter(this, mViewModel.getNetworkLiveData().getAppKeys(), mViewModel.getSelectedModel());
        recyclerViewBoundKeys.setAdapter(mBoundAppKeyAdapter);

        mActionBindAppKey.setOnClickListener(v -> {
            final ProvisionedMeshNode node = mViewModel.getSelectedMeshNode().getValue();
            if (node != null && !node.isExist(SigModelParser.CONFIGURATION_SERVER)) {
                return;
            }
            if (!checkConnectivity()) return;
            final Intent bindAppKeysIntent = new Intent(BaseModelConfigurationActivity.this, AppKeysActivity.class);
            bindAppKeysIntent.putExtra(EXTRA_DATA, BIND_APP_KEY);
            startActivityForResult(bindAppKeysIntent, SELECT_KEY);
        });

        mPublishAddressView.setText(R.string.none);
        mActionSetPublication.setOnClickListener(v -> navigateToPublication());

        mActionClearPublication.setOnClickListener(v -> clearPublication());

        mActionSubscribe.setOnClickListener(v -> {
            if (!checkConnectivity()) return;
            //noinspection ConstantConditions
            final ArrayList<Group> groups = new ArrayList<>(mViewModel.getGroups().getValue());
            final ArrayList<Group> mygroups = new ArrayList<>(mViewModel.getGroups().getValue());
            final DialogFragmentGroupSubscription fragmentSubscriptionAddress = DialogFragmentGroupSubscription.newInstance(groups);
            fragmentSubscriptionAddress.show(getSupportFragmentManager(), null);
        });

        mViewModel.getSelectedModel().observe(this, model -> {
            if (model != null) {
                updateAppStatusUi(model);
                updatePublicationUi(model);
                updateSubscriptionUi(model);
            }
        });

        mViewModel.getTransactionStatus().observe(this, transactionStatus -> {
            if (transactionStatus != null) {
                hideProgressBar();
                final String message = getString(R.string.operation_timed_out);
                DialogFragmentTransactionStatus fragmentMessage = DialogFragmentTransactionStatus.newInstance("Transaction Failed", message);
                fragmentMessage.show(getSupportFragmentManager(), null);
            }
        });

        mViewModel.isConnectedToProxy().observe(this, isConnected -> {
            if (isConnected != null) {
                mIsConnected = isConnected;
                hideProgressBar();
                updateClickableViews();
            }
            invalidateOptionsMenu();
        });

        mViewModel.getMeshMessage().observe(this, this::updateMeshMessage);

        final Boolean isConnectedToNetwork = mViewModel.isConnectedToProxy().getValue();
        if (isConnectedToNetwork != null) {
            mIsConnected = isConnectedToNetwork;
        }
        invalidateOptionsMenu();

    }

    //Download image with byte[] add
    public void downLoadWithBytes() {

        StorageReference imageRef1 = storageReference.child("image/" + uid + ".png");

        long MAXBYTES = 1024*1024;

        imageRef1.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                //convert byte[] to bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.setActivityVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (mIsConnected) {
            getMenuInflater().inflate(R.menu.disconnect, menu);
        } else {
            getMenuInflater().inflate(R.menu.connect, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_connect:
                mViewModel.navigateToScannerActivity(this, false, CONNECT_TO_NETWORK, false);
                return true;
            case R.id.action_disconnect:
                mViewModel.disconnect();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PROGRESS_BAR_STATE, mProgressbar.getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(PROGRESS_BAR_STATE)) {
            mProgressbar.setVisibility(View.VISIBLE);
            disableClickableViews();
        } else {
            mProgressbar.setVisibility(View.INVISIBLE);
            enableClickableViews();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_KEY:
                if (resultCode == RESULT_OK) {
                    final ApplicationKey appKey = data.getParcelableExtra(RESULT_KEY);
                    if (appKey != null) {
                        bindAppKey(appKey.getKeyIndex());
                    }
                }
                break;
            case Utils.HEARTBEAT_SETTINGS_SET:
            case PublicationSettingsActivity.SET_PUBLICATION_SETTINGS:
                if (resultCode == RESULT_OK) {
                    showProgressbar();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.setActivityVisible(false);
        if (isFinishing()) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public Group createGroup(@NonNull final String name) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.createGroup(network.getSelectedProvisioner(), name);
    }

    @Override
    public Group createGroup(@NonNull final UUID uuid, final String name) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.createGroup(uuid, null, name);
    }

    @Override
    public boolean onGroupAdded(@NonNull final String name, final int address) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        final Group group = network.createGroup(network.getSelectedProvisioner(), address, name);
        if (network.addGroup(group)) {
            subscribe(group);
            return true;
        }
        return false;
    }

    @Override
    public boolean onGroupAdded(@NonNull final Group group) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        if (network.addGroup(group)) {
            subscribe(group);
            return true;
        }
        return false;
    }

    @Override
    public void subscribe(final Group group) {
        final ProvisionedMeshNode meshNode = mViewModel.getSelectedMeshNode().getValue();
        if (meshNode != null) {
            final Element element = mViewModel.getSelectedElement().getValue();
            if (element != null) {
                final int elementAddress = element.getElementAddress();
                final MeshModel model = mViewModel.getSelectedModel().getValue();
                if (model != null) {
                    final int modelIdentifier = model.getModelId();
                    final MeshMessage configModelSubscriptionAdd;
                    if (group.getAddressLabel() == null) {
                        configModelSubscriptionAdd = new ConfigModelSubscriptionAdd(elementAddress, group.getAddress(), modelIdentifier);
                    } else {
                        configModelSubscriptionAdd = new ConfigModelSubscriptionVirtualAddressAdd(elementAddress, group.getAddressLabel(), modelIdentifier);
                    }
                    sendMessage(meshNode.getUnicastAddress(), configModelSubscriptionAdd);
                }
            }
        }
    }

    @Override
    public void onItemDismiss(final RemovableViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        if (viewHolder instanceof GroupAddressAdapter.ViewHolder) {
            deleteSubscription(position);
        } else if (viewHolder instanceof BoundAppKeysAdapter.ViewHolder) {
            unbindAppKey(position);
        }
    }

    @Override
    public void onItemDismissFailed(final RemovableViewHolder viewHolder) {
    }

    @Override
    public void onDisconnected() {
        finish();
    }

    @Override
    public void onRefresh() {
        final MeshModel model = mViewModel.getSelectedModel().getValue();
        if (!checkConnectivity() || model == null) {
            mSwipe.setRefreshing(false);
        }
        final ProvisionedMeshNode node = mViewModel.getSelectedMeshNode().getValue();
        final Element element = mViewModel.getSelectedElement().getValue();
        if (node != null && element != null && model != null) {
            if (model instanceof SigModel) {
                if (!(model instanceof ConfigurationServerModel) && !(model instanceof ConfigurationClientModel)) {
                    mViewModel.displaySnackBar(this, mContainer, getString(R.string.listing_model_configuration), Snackbar.LENGTH_LONG);
                    final ConfigSigModelAppGet appGet = new ConfigSigModelAppGet(element.getElementAddress(), model.getModelId());
                    final ConfigSigModelSubscriptionGet subscriptionGet = new ConfigSigModelSubscriptionGet(element.getElementAddress(), model.getModelId());
                    mViewModel.getMessageQueue().add(appGet);
                    mViewModel.getMessageQueue().add(subscriptionGet);
                    queuePublicationGetMessage(element.getElementAddress(), model.getModelId());
                    //noinspection ConstantConditions
                    sendMessage(node.getUnicastAddress(), mViewModel.getMessageQueue().peek());
                } else {
                    mSwipe.setRefreshing(false);
                }

            } else {
                mViewModel.displaySnackBar(this, mContainer, getString(R.string.listing_model_configuration), Snackbar.LENGTH_LONG);
                final ConfigVendorModelAppGet appGet = new ConfigVendorModelAppGet(element.getElementAddress(), model.getModelId());
                final ConfigVendorModelSubscriptionGet subscriptionGet = new ConfigVendorModelSubscriptionGet(element.getElementAddress(), model.getModelId());
                mViewModel.getMessageQueue().add(appGet);
                mViewModel.getMessageQueue().add(subscriptionGet);
                queuePublicationGetMessage(element.getElementAddress(), model.getModelId());
                //noinspection ConstantConditions
                sendMessage(node.getUnicastAddress(), mViewModel.getMessageQueue().peek());
            }
        }
    }

    protected void navigateToPublication() {
        final MeshModel model = mViewModel.getSelectedModel().getValue();
        if (model != null && !model.getBoundAppKeyIndexes().isEmpty()) {
            final Intent publicationSettings = new Intent(this, PublicationSettingsActivity.class);
            startActivityForResult(publicationSettings, PublicationSettingsActivity.SET_PUBLICATION_SETTINGS);
        } else {
            mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
        }
    }

    private void bindAppKey(final int appKeyIndex) {
        final ProvisionedMeshNode meshNode = mViewModel.getSelectedMeshNode().getValue();
        if (meshNode != null) {
            final Element element = mViewModel.getSelectedElement().getValue();
            if (element != null) {
                final MeshModel model = mViewModel.getSelectedModel().getValue();
                if (model != null) {
                    final ConfigModelAppBind configModelAppUnbind = new ConfigModelAppBind(element.getElementAddress(), model.getModelId(), appKeyIndex);
                    sendMessage(meshNode.getUnicastAddress(), configModelAppUnbind);
                }
            }
        }
    }

    private void unbindAppKey(final int position) {
        if (mBoundAppKeyAdapter.getItemCount() != 0) {
            if (!checkConnectivity()) {
                mBoundAppKeyAdapter.notifyItemChanged(position);
                return;
            }
            final ApplicationKey appKey = mBoundAppKeyAdapter.getAppKey(position);
            final int keyIndex = appKey.getKeyIndex();
            final ProvisionedMeshNode meshNode = mViewModel.getSelectedMeshNode().getValue();
            if (meshNode != null) {
                final Element element = mViewModel.getSelectedElement().getValue();
                if (element != null) {
                    final MeshModel model = mViewModel.getSelectedModel().getValue();
                    if (model != null) {
                        final ConfigModelAppUnbind configModelAppUnbind = new ConfigModelAppUnbind(element.getElementAddress(), model.getModelId(), keyIndex);
                        sendMessage(meshNode.getUnicastAddress(), configModelAppUnbind);
                    }
                }
            }
        }
    }

    private void clearPublication() {
        final ProvisionedMeshNode meshNode = mViewModel.getSelectedMeshNode().getValue();
        if (meshNode != null) {
            final Element element = mViewModel.getSelectedElement().getValue();
            if (element != null) {
                final MeshModel model = mViewModel.getSelectedModel().getValue();
                if (model != null) {
                    if (!model.getBoundAppKeyIndexes().isEmpty()) {
                        final int address = MeshAddress.UNASSIGNED_ADDRESS;
                        final int appKeyIndex = model.getPublicationSettings().getAppKeyIndex();
                        final boolean credentialFlag = model.getPublicationSettings().getCredentialFlag();
                        final int ttl = model.getPublicationSettings().getPublishTtl();
                        final int publicationSteps = model.getPublicationSettings().getPublicationSteps();
                        final int publicationResolution = model.getPublicationSettings().getPublicationResolution();
                        final int retransmitCount = model.getPublicationSettings().getPublishRetransmitCount();
                        final int retransmitIntervalSteps = model.getPublicationSettings().getPublishRetransmitIntervalSteps();
                        final ConfigModelPublicationSet configModelPublicationSet = new ConfigModelPublicationSet(element.getElementAddress(), address, appKeyIndex,
                                credentialFlag, ttl, publicationSteps, publicationResolution, retransmitCount, retransmitIntervalSteps, model.getModelId());
                        sendMessage(meshNode.getUnicastAddress(), configModelPublicationSet);
                    } else {
                        mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                    }
                }
            }
        }
    }

    private void deleteSubscription(final int position) {
        if (mSubscriptionAdapter.getItemCount() != 0) {
            if (!checkConnectivity()) {
                mSubscriptionAdapter.notifyItemChanged(position);
                return;
            }
            final int address = mGroupAddress.get(position);
            final ProvisionedMeshNode meshNode = mViewModel.getSelectedMeshNode().getValue();
            if (meshNode != null) {
                final Element element = mViewModel.getSelectedElement().getValue();
                if (element != null) {
                    final MeshModel model = mViewModel.getSelectedModel().getValue();
                    if (model != null) {
                        MeshMessage subscriptionDelete = null;
                        if (MeshAddress.isValidGroupAddress(address)) {
                            subscriptionDelete = new ConfigModelSubscriptionDelete(element.getElementAddress(), address, model.getModelId());
                        } else {
                            final UUID uuid = model.getLabelUUID(address);
                            if (uuid != null)
                                subscriptionDelete = new ConfigModelSubscriptionVirtualAddressDelete(element.getElementAddress(), uuid, model.getModelId());
                        }

                        if (subscriptionDelete != null) {
                            sendMessage(meshNode.getUnicastAddress(), subscriptionDelete);
                        }
                    }
                }
            }
        }
    }

    protected final void showProgressbar() {
        mHandler.postDelayed(mOperationTimeout, MESSAGE_TIME_OUT);
        disableClickableViews();
        mProgressbar.setVisibility(View.VISIBLE);
    }

    protected final void hideProgressBar() {
        mSwipe.setRefreshing(false);
        enableClickableViews();
        mProgressbar.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(mOperationTimeout);
    }

    private final Runnable mOperationTimeout = () -> {
        hideProgressBar();
        mViewModel.getMessageQueue().clear();
        if (mViewModel.isActivityVisible()) {
            DialogFragmentTransactionStatus fragmentMessage = DialogFragmentTransactionStatus.
                    newInstance(getString(R.string.title_transaction_failed), getString(R.string.operation_timed_out));
            fragmentMessage.show(getSupportFragmentManager(), null);
        }
    };

    protected void enableClickableViews() {
        mActionBindAppKey.setEnabled(true);
        mActionSetPublication.setEnabled(true);
        mActionClearPublication.setEnabled(true);
        mActionSubscribe.setEnabled(true);

        if (mActionSetRelayState != null)
            mActionSetRelayState.setEnabled(true);
        if (mSetNetworkTransmitStateButton != null)
            mSetNetworkTransmitStateButton.setEnabled(true);

        if (mActionRead != null && !mActionRead.isEnabled())
            mActionRead.setEnabled(true);
    }

    protected void disableClickableViews() {
        mActionBindAppKey.setEnabled(false);
        mActionSetPublication.setEnabled(false);
        mActionClearPublication.setEnabled(false);
        mActionSubscribe.setEnabled(false);

        if (mActionSetRelayState != null)
            mActionSetRelayState.setEnabled(false);
        if (mSetNetworkTransmitStateButton != null)
            mSetNetworkTransmitStateButton.setEnabled(false);
        if (mActionRead != null)
            mActionRead.setEnabled(false);
    }

    /**
     * Update the mesh message
     *
     * @param meshMessage {@link MeshMessage} mesh message status
     */
    protected abstract void updateMeshMessage(final MeshMessage meshMessage);

    private void updateAppStatusUi(final MeshModel meshModel) {
        final List<Integer> keys = meshModel.getBoundAppKeyIndexes();
        mKeyIndexes.clear();
        mKeyIndexes.addAll(keys);
        if (!keys.isEmpty()) {
            mUnbindHint.setVisibility(View.VISIBLE);
            mAppKeyView.setVisibility(View.GONE);
            recyclerViewBoundKeys.setVisibility(View.VISIBLE);
        } else {
            mUnbindHint.setVisibility(View.GONE);
            mAppKeyView.setVisibility(View.VISIBLE);
            recyclerViewBoundKeys.setVisibility(View.GONE);
        }
    }

    private void updatePublicationUi(final MeshModel meshModel) {
        final PublicationSettings publicationSettings = meshModel.getPublicationSettings();
        if (publicationSettings != null) {
            final int publishAddress = publicationSettings.getPublishAddress();
            if (publishAddress != MeshAddress.UNASSIGNED_ADDRESS) {
                if (MeshAddress.isValidVirtualAddress(publishAddress)) {
                    final UUID uuid = publicationSettings.getLabelUUID();
                    if (uuid != null) {
                        mPublishAddressView.setText(uuid.toString().toUpperCase(Locale.US));
                    } else {
                        mPublishAddressView.setText(MeshAddress.formatAddress(publishAddress, true));
                    }
                } else {
                    mPublishAddressView.setText(MeshAddress.formatAddress(publishAddress, true));
                }
                mActionClearPublication.setVisibility(View.VISIBLE);
            } else {
                mPublishAddressView.setText(R.string.none);
                mActionClearPublication.setVisibility(View.GONE);
            }
        }
    }

    private void updateSubscriptionUi(final MeshModel meshModel) {
        final List<Integer> subscriptionAddresses = meshModel.getSubscribedAddresses();
        mGroupAddress.clear();
        mGroupAddress.addAll(subscriptionAddresses);
        if (!subscriptionAddresses.isEmpty()) {
            mSubscribeHint.setVisibility(View.VISIBLE);
            mSubscribeAddressView.setVisibility(View.GONE);
            recyclerViewAddresses.setVisibility(View.VISIBLE);
        } else {
            mSubscribeHint.setVisibility(View.GONE);
            mSubscribeAddressView.setVisibility(View.VISIBLE);
            recyclerViewAddresses.setVisibility(View.GONE);
        }
    }

    protected final boolean checkConnectivity() {
        if (!mIsConnected) {
            mViewModel.displayDisconnectedSnackBar(this, mContainer);
            return false;
        }
        return true;
    }

    protected void sendMessage(@NonNull final MeshMessage meshMessage) {
        try {
            if (!checkConnectivity())
                return;
            final ProvisionedMeshNode node = mViewModel.getSelectedMeshNode().getValue();
            if (node != null) {
                mViewModel.getMeshManagerApi().createMeshPdu(node.getUnicastAddress(), meshMessage);
                showProgressbar();
            }
        } catch (IllegalArgumentException ex) {
            hideProgressBar();
            final DialogFragmentError message = DialogFragmentError.
                    newInstance(getString(R.string.title_error), ex.getMessage());
            message.show(getSupportFragmentManager(), null);
        }
    }

    protected boolean handleStatuses() {
        final MeshMessage message = mViewModel.getMessageQueue().peek();
        if (message != null) {
            sendMessage(message);
            return true;
        } else {
            mViewModel.displaySnackBar(this, mContainer, getString(R.string.operation_success), Snackbar.LENGTH_SHORT);
        }
        return false;
    }

    protected void sendMessage(final int address, @NonNull final MeshMessage meshMessage) {
        try {
            if (!checkConnectivity())
                return;
            mViewModel.getMeshManagerApi().createMeshPdu(address, meshMessage);
            showProgressbar();
        } catch (IllegalArgumentException ex) {
            hideProgressBar();
            final DialogFragmentError message = DialogFragmentError.
                    newInstance(getString(R.string.title_error), ex.getMessage());
            message.show(getSupportFragmentManager(), null);
        }
    }

    private void updateClickableViews() {
        final MeshModel model = mViewModel.getSelectedModel().getValue();
        if (model != null && model.getModelId() == SigModelParser.CONFIGURATION_CLIENT)
            disableClickableViews();
    }

    protected void queuePublicationGetMessage(final int address, final int modelId) {
        final ConfigModelPublicationGet publicationGet = new ConfigModelPublicationGet(address, modelId);
        mViewModel.getMessageQueue().add(publicationGet);
    }

    protected void displayStatusDialogFragment(@NonNull final String title, @NonNull final String message) {
        if (mViewModel.isActivityVisible()) {
            DialogFragmentConfigStatus fragmentAppKeyBindStatus = DialogFragmentConfigStatus.
                    newInstance(title, message);
            fragmentAppKeyBindStatus.show(getSupportFragmentManager(), DIALOG_FRAGMENT_CONFIGURATION_STATUS);
        }
    }

    public Button.OnClickListener shotListener =
            new Button.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    //partial array ( Height * width )
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

                    synchronized (this) {
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(BaseModelConfigurationActivity.this);//跳出詢問對話框
                        builder.setTitle("警告");
                        builder.setMessage("確定更新此筆資料？");
                        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageView mImg = (ImageView) findViewById(R.id.yaa);
                                mImg.setImageBitmap(getScreenShot());
                                mImg.setVisibility(View.GONE);

                                update(tname.getText().toString(), tgender.getText().toString(), ttype.getText().toString(), tuid.getText().toString(), tdate.getText().toString(), torder1.getText().toString(),
                                        torder2.getText().toString(), torder3.getText().toString(), tmain.getText().toString(), tresident.getText().toString(), tnurse.getText().toString());
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                }
            };

    public void update(String name, String gender, String type, String uid, String date,String order1, String order2, String order3, String main, String resident, String nurse){

        int update_n = name_set + gender_set + type_set + uid_set + date_set + order1_set + order2_set + order3_set + maindoc_set + residentdoc_set + nurse_set;   //partial update number

        if (update_n == 0){   //全沒改

            //BW_bit = invert(BW_bit, 880, 528);   //反轉
            //WR_bit = invert(WR_bit, 880, 528);   //反轉

            for (int i = 0; i < 880 * 258; i++) {
                //換行
                if ((i % 880) == 0 && i != 0)
                    System.out.println();
                System.out.print(BW_bit[i]);
            }

            System.out.println();
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println();

            for (int i = 0; i < 880 * 528; i++) {
                //換行
                if ((i % 880) == 0 && i != 0)
                    System.out.println();
                System.out.print(WR_bit[i]);
            }

            name_set = 1;
            gender_set = 1;
            type_set = 1;
            uid_set = 1;
            date_set = 1;
            order1_set = 1;
            order2_set = 1;
            order3_set = 1;
            maindoc_set = 1;
            residentdoc_set = 1;
            nurse_set = 1;
            update_n = 12;


            //黑白bit陣列
            //sendGenericOnOff(true, (int) 0, (byte) 0, (byte) 0, (byte) 110,(byte) 66, true, encodeToByteArray(BW_bit), 2);

            //紅白bit陣列
            //sendGenericOnOff(true, (int) 0, (byte) 0, (byte) 0, (byte) 110,(byte) 66, false, encodeToByteArray(WR_bit), 2);
        }

        if (name_set == 1) {
            //           Y              Y  +  H
            for (int y = 96, z = 0; y < 96 + 88; y++) {
                //           X        X  +   W
                for (int x = 112; x < 112 + 280; x++) {
                    int i = y * 880 + x;
                    name_part[z++] = BW_bit[i];
                }
            }

            name_part = invert(name_part, 280, 88);
            System.out.println();
            System.out.println();
            System.out.println("name part  = ");
            for (int i = 0; i < 280 * 88; i++) {
                //換行
                if ((i % 280) == 0 && i != 0)
                    System.out.println();
                System.out.print(name_part[i]);
            }
            //                                                   X        W                  Y               W                  H

            name_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 112 / 8 - 280 / 8), (byte) (96 / 8), (byte) (280 / 8),  (byte) (96 / 8), true, encodeToByteArray(name_part), update_n);
        }



        if (gender_set == 1) {
            for (int y = 152, z = 0; y < 152 + 48; y++) {
                for (int x = 546; x < 546 + 48; x++) {
                    int i = y * 880 + x;
                    gender_part[z++] = BW_bit[i];
                }
            }

            gender_part = invert(gender_part, 48, 48);
            System.out.println();
            System.out.println();
            System.out.println("gender part  = ");
            for (int i = 0; i < 48 * 48; i++) {
                //換行
                if ((i % 48) == 0 && i != 0)
                    System.out.println();
                System.out.print(gender_part[i]);
            }

            gender_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 568 / 8 - 48 / 8), (byte) (152 / 8), (byte) (48 / 8), (byte) (48 / 8), true, encodeToByteArray(gender_part), update_n);
        }

        if (type_set == 1) {
            for (int y = 192, z = 0; y < 192 + 32; y++) {
                for (int x = 112; x < 112 + 280; x++) {
                    int i = y * 880 + x;
                    type_part[z++] = BW_bit[i];
                }
            }

            type_part = invert(type_part, 280, 32);
            System.out.println();
            System.out.println();
            System.out.println("type part  = ");
            for (int i = 0; i < 280 * 32; i++) {
                //換行
                if ((i % 280) == 0 && i != 0)
                    System.out.println();
                System.out.print(type_part[i]);
            }

            type_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 112 / 8 - 280 / 8), (byte) (200 / 8), (byte) (280 / 8), (byte) (32 / 8), true, encodeToByteArray(type_part), update_n);
        }

        if (uid_set == 1) {

            for (int y = 224, z = 0; y < 224 + 32; y++) {
                for (int x = 112; x < 112 + 280; x++) {
                    int i = y * 880 + x;
                    uid_part[z++] = BW_bit[i];
                }
            }

            uid_part = invert(uid_part, 280, 32);
            System.out.println();
            System.out.println();
            System.out.println("uid part  = ");
            for (int i = 0; i < 280 * 32; i++) {
                //換行
                if ((i % 280) == 0 && i != 0)
                    System.out.println();
                System.out.print(uid_part[i]);
            }

            for (int y = 120, z = 0; y < 120 + 152; y++) {
                for (int x = 688; x < 688 + 176; x++) {
                    int i = y * 880 + x;
                    qrcode_part[z++] = BW_bit[i];
                }
            }

            qrcode_part = invert(qrcode_part, 176, 152);
            System.out.println();
            System.out.println();
            System.out.println("qrcode part  = ");
            for (int i = 0; i < 176 * 152; i++) {
                //換行
                if ((i % 176) == 0 && i != 0)
                    System.out.println();
                System.out.print(qrcode_part[i]);
            }

            uid_set = 0;

            sendGenericOnOff(true, (int) 0, (byte) (110 - 112 / 8 - 280 / 8), (byte) (232 / 8), (byte) (280 / 8), (byte) (32 / 8), true, encodeToByteArray(uid_part), (update_n + 1));
            sendGenericOnOff(true, (int) 0, (byte) (110 - 688 / 8 - 176 / 8), (byte) (120 / 8), (byte) (176 / 8), (byte) (176 / 8), true, encodeToByteArray(qrcode_part), (update_n + 1));
        }

        if (date_set == 1) {
            for (int y = 264, z = 0; y < 264 + 24; y++) {
                for (int x = 112; x < 112 + 280; x++) {
                    int i = y * 880 + x;
                    date_part[z++] = BW_bit[i];
                }
            }

            date_part = invert(date_part, 280, 24);
            System.out.println();
            System.out.println();
            System.out.println("date part  = ");
            for (int i = 0; i < 280 * 24; i++) {
                //換行
                if ((i % 280) == 0 && i != 0)
                    System.out.println();
                System.out.print(date_part[i]);
            }

            date_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 112 / 8 - 280 / 8), (byte) (264 / 8), (byte) (280 / 8), (byte) (24 / 8), true, encodeToByteArray(date_part), update_n);

        }

        if (order1_set == 1) {
            for (int y = 288, z = 0; y < 288 + 64; y++) {
                for (int x = 16; x < 16 + 504; x++) {
                    int i = y * 880 + x;
                    order1_part[z++] = WR_bit[i];
                }
            }

            order1_part = invert(order1_part, 504, 64);
            System.out.println();
            System.out.println();
            System.out.println("order1 part  = ");
            for (int i = 0; i < 504 * 64; i++) {
                //換行
                if ((i % 504) == 0 && i != 0)
                    System.out.println();
                System.out.print(order1_part[i]);
            }

            order1_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 16 / 8 - 504 / 8), (byte) (304 / 8), (byte) (504 / 8), (byte) (72 / 8), false, encodeToByteArray(order1_part), update_n);
        }

        if (order2_set == 1) {
            //name part
            for (int y = 368, z = 0; y < 368 + 56; y++) {
                for (int x = 16; x < 16 + 504; x++) {
                    int i = y * 880 + x;
                    order2_part[z++] = WR_bit[i];
                }
            }

            order2_part = invert(order2_part, 504, 56);
            System.out.println();
            System.out.println();
            System.out.println("order2 part  = ");
            for (int i = 0; i < 504 * 56; i++) {
                //換行
                if ((i % 504) == 0 && i != 0)
                    System.out.println();
                System.out.print(order2_part[i]);
            }

            order2_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 16 / 8 - 504 / 8), (byte) (376 / 8), (byte) (504 / 8), (byte) (64 / 8), false, encodeToByteArray(order2_part), update_n);
        }


        if (order3_set == 1) {
            for (int y = 448, z = 0; y < 448 + 56; y++) {
                for (int x = 16; x < 16 + 504; x++) {
                    int i = y * 880 + x;
                    order3_part[z++] = WR_bit[i];
                }
            }

            order3_part = invert(order3_part, 504, 56);
            System.out.println();
            System.out.println();
            System.out.println("order3 part  = ");
            for (int i = 0; i < 504 * 56; i++) {
                //換行
                if ((i % 504) == 0 && i != 0)
                    System.out.println();
                System.out.print(order3_part[i]);
            }

            order3_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 16 / 8 - 504 / 8), (byte) (448 / 8), (byte) (504 / 8), (byte) (64 / 8), false, encodeToByteArray(order3_part), update_n);
        }

        if (maindoc_set == 1) {
            for (int y = 296, z = 0; y < 296 + 56; y++) {
                for (int x = 664; x < 664 + 184; x++) {
                    int i = y * 880 + x;
                    maindoc_part[z++] = WR_bit[i];
                }
            }

            maindoc_part = invert(maindoc_part, 184, 56);
            System.out.println();
            System.out.println();
            System.out.println("maindoc part  = ");
            for (int i = 0; i < 184 * 56; i++) {
                //換行
                if ((i % 184) == 0 && i != 0)
                    System.out.println();
                System.out.print(maindoc_part[i]);
            }

            maindoc_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 664 / 8 - 184 / 8), (byte) (304 / 8), (byte) (184 / 8), (byte) (56 / 8), false, encodeToByteArray(maindoc_part), update_n);
        }

        if (residentdoc_set == 1) {
            for (int y = 368, z = 0; y < 368 + 56; y++) {
                for (int x = 664; x < 664 + 184; x++) {
                    int i = y * 880 + x;
                    residentdoc_part[z++] = WR_bit[i];
                }
            }

            residentdoc_part = invert(residentdoc_part, 184, 56);
            System.out.println();
            System.out.println();
            System.out.println("residentdoc part  = ");
            for (int i = 0; i < 184 * 56; i++) {
                //換行
                if ((i % 184) == 0 && i != 0)
                    System.out.println();
                System.out.print(residentdoc_part[i]);
            }

            residentdoc_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 664 / 8 - 184 / 8), (byte) (376 / 8), (byte) (184 / 8), (byte) (56 / 8), false, encodeToByteArray(residentdoc_part), update_n);
        }

        if (nurse_set == 1) {
            for (int y = 448, z = 0; y < 448 + 56; y++) {
                for (int x = 664; x < 664 + 184; x++) {
                    int i = y * 880 + x;
                    nurse_part[z++] = WR_bit[i];
                }
            }

            nurse_part = invert(nurse_part, 184, 56);
            System.out.println();
            System.out.println();
            System.out.println("nurse part  = ");
            for (int i = 0; i < 184 * 56; i++) {
                //換行
                if ((i % 184) == 0 && i != 0)
                    System.out.println();
                System.out.print(nurse_part[i]);
            }

            nurse_set = 0;
            sendGenericOnOff(true, (int) 0, (byte) (110 - 664 / 8 - 184 / 8), (byte) (448 / 8), (byte) (184 / 8), (byte) (56 / 8), false, encodeToByteArray(nurse_part), update_n);
        }
        TextView taddress = findViewById(R.id.address);
        String sAddress = taddress.getText().toString();

        //更新資料
        myRef2.child(roomnumber).setValue(new TextString(roomnumber, name,  gender, type, uid, date, order1,
                order2, order3, main, resident, nurse, sAddress));
        myRef3.child(uid).setValue(new TextString(roomnumber, name, gender, type, uid, date, order1,
                order2, order3, main, resident, nurse, sAddress));
    }


    //清空資料
    public Button.OnClickListener clearListener =
            new Button.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseModelConfigurationActivity.this);//跳出詢問對話框
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
                            String uid = tuid.getText().toString();
                            String name = tname.getText().toString();
                            String gender = tgender.getText().toString();
                            String type = ttype.getText().toString();
                            String date = tdate.getText().toString();
                            String order1 = torder1.getText().toString();
                            String order2 = torder2.getText().toString();
                            String order3 = torder3.getText().toString();
                            String main = tmain.getText().toString();
                            String resident = tresident.getText().toString();
                            String nurse = tnurse.getText().toString();

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

                            myRef2.child(roomnumber).setValue(new TextString(roomnumber, a, a, a,a, a, a, a, a, a, a, a, a));
                            myRef3.child(uid).setValue(new TextString(a, name, gender, type, uid, date, order1,
                                    order2, order3, main, resident, nurse, a));
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

    public Button.OnClickListener backgroundListener =
            new Button.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseModelConfigurationActivity.this);//跳出詢問對話框
                    builder.setTitle("警告");
                    builder.setMessage("是否上傳此背景？");
                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getScreenShot();

                            BW_bit = invert(BW_bit, 880, 528);   //反轉
                            WR_bit = invert(WR_bit, 880, 528);   //反轉

                            //黑白bit陣列
                            sendGenericOnOff(true, (int) 0, (byte) 0, (byte) 0, (byte) 110,(byte) 66, true, encodeToByteArray(BW_bit), 2);
                            //紅白bit陣列
                            //sendGenericOnOff(true, (int) 0, (byte) 0, (byte) 0, (byte) 110,(byte) 66, false, encodeToByteArray(WR_bit), 2);


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
        ImageView tx = mView.findViewById(R.id.imageView);
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
        Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, 0, 250, tx.getWidth(), tx.getHeight());
        int newwidth=880;
        int newheight=528;
        float scalew=((float)newwidth/mBitmap.getWidth());
        float scaleh=((float)newheight/mBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scalew,scaleh);
        Bitmap newbitmap=Bitmap.createBitmap(mBitmap,0,0,mBitmap.getWidth(),mBitmap.getHeight(),matrix,true);

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
                    BW[index] = 0;
                else
                    BW[index] = 1;
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
    public int[] invert(int[] array, int width, int height){

        for (int h = 0; h < height; h++){
            for (int w = 0; w < width; w++){
                int index = h * width + w;
                int Rindex = (h + 1) * width - 1 - w;

                if(index >= Rindex){
                    w = width;
                    break;
                }
                else {
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

    protected void updateMeshMessage0(final MeshMessage meshMessage) {

        if (meshMessage instanceof ConfigModelAppStatus) {
            final ConfigModelAppStatus status = (ConfigModelAppStatus) meshMessage;
            if (status.isSuccessful()) {
                mViewModel.displaySnackBar(this, mContainer, getString(R.string.operation_success), Snackbar.LENGTH_SHORT);
            } else {
                displayStatusDialogFragment(getString(R.string.title_appkey_status), status.getStatusCodeName());
            }
        } else if (meshMessage instanceof ConfigSigModelAppList) {
            final ConfigSigModelAppList status = (ConfigSigModelAppList) meshMessage;
            mViewModel.removeMessage();
            if (status.isSuccessful()) {
                handleStatuses();
            } else {
                displayStatusDialogFragment(getString(R.string.title_sig_model_subscription_list), status.getStatusCodeName());
            }
        } else if (meshMessage instanceof ConfigModelPublicationStatus) {
            final ConfigModelPublicationStatus status = (ConfigModelPublicationStatus) meshMessage;
            mViewModel.removeMessage();
            if (status.isSuccessful()) {
                handleStatuses();
            } else {
                displayStatusDialogFragment(getString(R.string.title_publication_status), status.getStatusCodeName());
            }
        } else if (meshMessage instanceof ConfigModelSubscriptionStatus) {
            final ConfigModelSubscriptionStatus status = (ConfigModelSubscriptionStatus) meshMessage;
            mViewModel.removeMessage();
            if (status.isSuccessful()) {
                handleStatuses();
            } else {
                displayStatusDialogFragment(getString(R.string.title_subscription_status), status.getStatusCodeName());
            }
        } else if (meshMessage instanceof ConfigSigModelSubscriptionList) {
            final ConfigSigModelSubscriptionList status = (ConfigSigModelSubscriptionList) meshMessage;
            mViewModel.removeMessage();
            if (status.isSuccessful()) {
                handleStatuses();
            } else {
                displayStatusDialogFragment(getString(R.string.title_sig_model_subscription_list), status.getStatusCodeName());
            }
        }

        mSwipe.setOnRefreshListener(this);
        if (meshMessage instanceof GenericOnOffStatus) {
            final GenericOnOffStatus status = (GenericOnOffStatus) meshMessage;
            final boolean presentState = status.getPresentState();
            final Boolean targetOnOff = status.getTargetState();
            final int steps = status.getTransitionSteps();
            final int resolution = status.getTransitionResolution();

        }
        hideProgressBar();
    }

    public void sendGenericOnOff(boolean state, final Integer delay, byte x, byte y, byte w, byte h, boolean color, byte[] data, int partn) {
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
                        //final int address = element.getElementAddress();
                        TextView taddress = findViewById(R.id.address);
                        String sAddress = taddress.getText().toString();
                        int address = Integer.valueOf(sAddress);

                        byte zero = 0;
                        byte one = 1;

                        final byte[] empty = new byte[1];
                        empty[0] = 0;


                        final GenericOnOffSet genericOnOffSet0 = new GenericOnOffSet(appKey, state,
                                new Random().nextInt(), null,null, null, zero, zero, one, one, color, partn ,empty);
                        sendMessage(address, genericOnOffSet0);



                        for (int n = 0; n < data.length; n = n + 24) {
                            if((n / 24) %2 == 0)
                                state = false;

                            final GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, state,
                                    new Random().nextInt(), null, null, null, x, y, w, h, color,  n, data);
                            sendMessage(address, genericOnOffSet);
                        }

                        sendMessage(address, genericOnOffSet0);



                    }
                    else {
                        mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                    }
                }
            }
        }
    }
}
