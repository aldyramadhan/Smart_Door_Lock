package org.d3ifcool.smart.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.wang.avi.AVLoadingIndicatorView;

import org.d3ifcool.smart.Adapter.RecyclerAdapterHouse;
import org.d3ifcool.smart.BottomNavigation.BottomNavigationViewHelper;
import org.d3ifcool.smart.BottomNavigation.SectionsPageAdapter;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Family.FamilyActivity;
import org.d3ifcool.smart.Model.House;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.Notification.MyFirebaseMessagingService;
import org.d3ifcool.smart.Onvif.MainCamera;
import org.d3ifcool.smart.Onvif.OnvifInput;
import org.d3ifcool.smart.QrCode.Qr;
import org.d3ifcool.smart.R;
import org.d3ifcool.smart.Setting.SettingActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hari.bounceview.BounceView;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapterHouse.OnItemClickListener {

    private static final String TAG = "OuutputCamera";
    private SectionsPageAdapter mSectionsPageAdapter;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference Storage;
    private DatabaseReference mDatabaseRef;
    private RecyclerView recyclerView;
    private RecyclerAdapterHouse mAdapter;
    private ValueEventListener mDBListener;
    private List<House> mHouses;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, reference0, reference1, reference2;
    private AVLoadingIndicatorView mProgressBar;
    private View fader;
    private int counterInput = 0;
    private int countDown = 30;
    private String profileid;
    private ProgressDialog pd;
    private CardView addHome;
    private Button commitHome;
    private Dialog dialog, dialogAdd;
    private ImageView closePoupUp, toolAddHome,
            closePouUpOption, qrOption, inputOption;
    private EditText  deviceCode, houseNameEditTxt;
    private TextView  emptyInMemer, count, textConnection, retry;
    private IndefinitePagerIndicator indicator;
    private RelativeLayout rl;


    public void openDetailActivity(String[] data) {

        Intent intent = new Intent(this, HousesDetail.class);

        intent.putExtra("NAME_KEY", data[0]);

        intent.putExtra("DEVICECODE_KEY", data[1]);

        startActivity(intent);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        );

        startService(new Intent(this, MyFirebaseMessagingService.class));

        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);

        profileid = prefs.getString("profileid", "none");

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorage = FirebaseStorage.getInstance();

        database = FirebaseDatabase.getInstance();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        Storage = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this, R.style.MyAlertDialogStyle);


        //get method
        viewWidget();

        getHouse();

        Data.madeDateHouse = getDateToday();

        //set custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolhome);

        setSupportActionBar(toolbar);

        //set Custom bottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();

        MenuItem menuItem = menu.getItem(1);

        menuItem.setChecked(true);


        //Item BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_home:

                        break;

                    case R.id.nav_user:

                        Intent intent2 = new Intent(MainActivity.this, FamilyActivity.class);

                        startActivity(intent2);

                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        break;

                    case R.id.nav_setting:

                        Intent intent3 = new Intent(MainActivity.this, SettingActivity.class);

                        startActivity(intent3);

                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }

                return false;

            }

        });

        //Checking internet
        if (!isOnline()) {

            rl.setVisibility(View.VISIBLE);

            textConnection.setVisibility(View.VISIBLE);

//            alertConnection();

//            addHome.setVisibility(View.GONE);
//
//            recyclerView.setVisibility(View.GONE);

        }

        else {

            rl.setVisibility(View.GONE);

            textConnection.setVisibility(View.GONE);

//            addHome.setVisibility(View.VISIBLE);
//
//            recyclerView.setVisibility(View.VISIBLE);

        }

    }

    public void alertConnection(){

        new iOSDialogBuilder(this)

                .setTitle(getString(R.string.No_internet_access))

                .setSubtitle(getString(R.string.Please_Check_Internet))

                .setBoldPositiveLabel(true)

                .setCancelable(false)

                .setPositiveListener(getString(R.string.retry), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        recreate();

                    }
                }).setNegativeListener(getString(R.string.Close), new iOSDialogClickListener() {

            @Override
            public void onClick(iOSDialog dialog) {

                finish();

                onBackPressed();

            }
        }).build().show();

    }

    //check account login
    private void checkAccount() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail()
                .replace(".",","));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getContext() == null){

                    return;

                }

                User user = dataSnapshot.getValue(User.class);

                String check = user.getTypeAccount();

                if (check.equals("Owner")) {

                    emptyInMemer.setVisibility(View.GONE);

                    toolAddHome.setVisibility(View.VISIBLE);

                    recyclerView.setEnabled(true);

                }

                else {

                    emptyInMemer.setVisibility(View.VISIBLE);

                    addHome.setVisibility(View.GONE);

                    toolAddHome.setVisibility(View.GONE);

                    recyclerView.setEnabled(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void checkHouse() {

        if (mAdapter.getItemCount() !=  0){

            addHome.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);

            emptyInMemer.setVisibility(View.GONE);

            toolAddHome.setVisibility(View.VISIBLE);

            indicator.setVisibility(View.VISIBLE);

        }

        else {

            addHome.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);

            toolAddHome.setVisibility(View.GONE);

            indicator.setVisibility(View.GONE);

            checkAccount();

        }

    }

    private void notificationDoorKnock() {

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("RssPullService");

        Intent resultIntent = new Intent(this, StreamingActivity.class);

        PendingIntent resultPandingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                .setDefaults(NotificationCompat.DEFAULT_ALL)

                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lock))

                .setSmallIcon(R.drawable.logo_lock)

                .setContentTitle(getString(R.string.app_name))

                .setContentText("Guest")

                .setVibrate(new long[]{0, 500, 1000})

                .setContentIntent(resultPandingIntent)

                .setAutoCancel(true)

                .setPriority(Notification.PRIORITY_MAX);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification.setSound(uri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification.build());

    }

    public void getHouse(){
        setLoadingAnimation();
        mDatabaseRef = database.getInstance().getReference();
        mDatabaseRef.keepSynced(true);
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (mDatabaseRef == null) {

                    database = FirebaseDatabase.getInstance();

                    database.setPersistenceEnabled(true);

                    mDatabaseRef = database.getReference();

                }

                mHouses.clear();

                try {

                    for (DataSnapshot kodedeviceSnapshot : dataSnapshot.child("Users").child(firebaseUser.getEmail()
                            .replace(".", ",")).child("Houses").getChildren()) {

                        String kode_device = kodedeviceSnapshot.getValue(String.class);

                        Log.d(TAG, "onDataChange: " + kode_device);

                        House house = dataSnapshot.child("Devices").child("ListDevices").child(kode_device).getValue(House.class);

                        Log.d(TAG, "onDataChange: " + house.getName());

                        mHouses.add(house);

                    }

                }catch (Exception e){}

                mAdapter.notifyDataSetChanged();

                fader.setVisibility(View.GONE);

                stopLoadingAnimation();

                checkHouse();

                checkAccount();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                stopLoadingAnimation();

            }

        });

    }

    private String getDateToday(){

        DateFormat dateFormat=new SimpleDateFormat("dd/M/yyyy h:mm");

        Date date = new Date();

        String today= dateFormat.format(date);

        return today;

    }

    private boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    public boolean haveNetwork() {

        boolean have_WIFI = false;

        boolean have_MOBILE = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos){

            if (info.getTypeName().equalsIgnoreCase("WIFI"));

            if (info.isConnected())

                have_WIFI = true;

            if (info.getTypeName().equalsIgnoreCase("MOBILE"));

            if (info.isConnected())

                have_MOBILE = true;

        }

        return have_WIFI || have_MOBILE;

    }

    @Override
    public void onClick(View v) {

    }


    //all onclick
    public void onClickView(View view) {

        switch (view.getId()) {

            case R.id.action_add_home :

                showDialodAdd();

                break;

            case R.id.doorDetail :

//                House clickedHouse = mHouses.get(position);

//                String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};

//                openDetailActivity(houseData);

                break;

            case R.id.stream_action :

                startActivity(new Intent(MainActivity.this, OnvifInput.class));

//                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("object.p2pwificam.client");
//
//                if (launchIntent != null) {
//
//                    startActivity(launchIntent);
//
//                }
//
//                else {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//                    builder.setCancelable(false);
//
//                    builder.setTitle("DOWNLOAD APP");
//
//                    builder.setMessage("download this app for acces wifi camera ?");
//
//                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//
//                            });
//
//                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//
//                                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=object.p2pwificam.client"));
//
//                                    startActivity(intent);
//
//                                }
//
//                            });
//
//                    builder.show();

//                }

                break;

            case R.id.cards_add_home :

                showDialodAdd();

                break;

        }

    }

    public void anotherApp(Context context){

//        packageName = "object.p2pwificam.client";

        Intent intent = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {

            intent = context.getPackageManager().getLaunchIntentForPackage("object.p2pwificam.client");

        }

        if (intent == null) {

            try {

                intent = new Intent(Intent.ACTION_VIEW);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.setData(Uri.parse("play.google.com/store/apps/details?id=" + "object.p2pwificam.client"));

                context.startActivity(intent);

            } catch (android.content.ActivityNotFoundException anfe) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "object.p2pwificam.client")));

            }

        } else {

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

        }

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.CATEGORY_HOME);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

    }

    @Override
    public void onItemClick(int position) {

//        House clickedHouse= mHouses.get(position);

//        String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};

//        openDetailActivity(houseData);



    }

    @Override
    public void onShowItemClick(int position) {

        House clickedHouse = mHouses.get(position);

        String[] houseData={clickedHouse.getName(), clickedHouse.getDeviceCode()};

        openDetailActivity(houseData);

    }

    @Override
    public void onDeleteItemClick(int position) {

        House selectedItem = mHouses.get(position);

        final String selectedKey = selectedItem.getDeviceCode();

        final String selectEmail = firebaseUser.getEmail().replace(".", ",");

        new iOSDialogBuilder(MainActivity.this)

                .setTitle(getString(R.string.delete_device))

                .setSubtitle(getString(R.string.massegaeDelete))

                .setBoldPositiveLabel(true)

                .setCancelable(false)

                .setNegativeListener(getString(R.string.Canncel), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();

                    }

                })

                .setPositiveListener(getString(R.string.yes), new iOSDialogClickListener() {
                    @Override
                    public void onClick(final iOSDialog dialog) {

                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(selectEmail).child("Houses");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot data : dataSnapshot.getChildren()) {

                                    if(data.getValue(String.class).equals(deviceCode)){

                                        ref.child(data.getKey()).removeValue();

                                        Log.d("key", "data " + data);

                                    }

                                }

                                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices");

                                reference0 = FirebaseDatabase.getInstance().getReference().child("Devices").child(selectedKey);

                                reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child(selectedKey);

                                reference.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        reference0.child("Member").getRef().removeValue();

                                        reference1.child("ListDoor").getRef().removeValue();

                                        Toast.makeText(MainActivity.this, "Item deleted " + selectedKey, Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();

                                    }

                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });

                    }

                }).build().show();

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @SuppressLint("ResourceAsColor")
    private void setStatustBarColor(@ColorRes int statustBarColor) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            int color = ContextCompat.getColor(this, statustBarColor);

            Window window = getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);

            window.setTitleColor(R.color.black);

        }

    }

    private void showDialodAdd() {

        dialogAdd.setContentView(R.layout.dialog_add);

        dialogAdd.setCanceledOnTouchOutside(false);

        closePouUpOption = dialogAdd.findViewById(R.id.close_popup_add);

        qrOption = dialogAdd.findViewById(R.id.qr);

        inputOption = dialogAdd.findViewById(R.id.input);

        BounceView.addAnimTo(dialogAdd);

        closePouUpOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogAdd.dismiss();

            }

        });

        qrOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogAdd.dismiss();

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                startActivity(new Intent(MainActivity.this, Qr.class));

            }

        });

        inputOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogAddHome();

                dialogAdd.dismiss();

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        });

        dialogAdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogAdd.show();

    }

    //Show dialog addhome
    private void showDialogAddHome() {

        dialog.setContentView(R.layout.add_home_popup);

        dialog.setCanceledOnTouchOutside(false);

        closePoupUp = dialog.findViewById(R.id.close_popup_home);

        commitHome = dialog.findViewById(R.id.add_home_code);

        houseNameEditTxt = dialog.findViewById(R.id.house_name);

        count = dialog.findViewById(R.id.countDownTime);

        deviceCode = dialog.findViewById(R.id.code_device);

        commitHome.setOnClickListener(this);

        BounceView.addAnimTo(dialog);

        closePoupUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }

        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        commitHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setMessage("Please wait...");

                pd.show();

                final String str_house = houseNameEditTxt.getText().toString();

                final String str_device = deviceCode.getText().toString();

                final House house = new House(str_house, str_device);

                if (TextUtils.isEmpty(str_house)) {

                    counterInput = 0;

                    houseNameEditTxt.setError("House name required");

                    pd.hide();

                }

                if (TextUtils.isEmpty(str_device)) {

                    counterInput = 0;

                    deviceCode.setError("Device code required");

                    pd.hide();

                } else {

                    reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(str_device);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot deviceSnapshot) {

                            House getHouses = deviceSnapshot.getValue(House.class);

                            reference1 = FirebaseDatabase.getInstance().getReference().child("Devices").child("ListDevices").child(str_device);
                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot listSnapshot) {

                                    reference0 = FirebaseDatabase.getInstance().getReference().child("Users").
                                            child(firebaseUser.getEmail().replace(".", ",")).child("Houses");

                                    reference2 = FirebaseDatabase.getInstance().getReference().child("Devices").child(str_device);

                                    if (listSnapshot.exists() && deviceSnapshot.exists()){

                                        counterInput = 0;

                                        deviceCode.setError("Device " + str_device + " Already available");

                                        pd.hide();

                                    }

                                     else if (!listSnapshot.exists() && deviceSnapshot.exists()) {

                                        counterInput = 0;

                                        String uploadId = reference.push().getKey();

                                        reference0.child(str_device).setValue(str_device);

                                        reference1.setValue(house);

                                        reference2.child("name").setValue(str_house);

                                        dialog.dismiss();

                                        dialogAdd.dismiss();

                                        Toast.makeText(MainActivity.this, str_house + " added", Toast.LENGTH_SHORT).show();

                                        pd.hide();

                                    }

                                    else {

                                        counterInput++;

                                        Log.d("jumlah", "input" + counterInput);

                                        deviceCode.setError("Device " + str_device + " Not found");

                                        pd.hide();

                                        if (counterInput == 7) {

                                            new CountDownTimer(30000, 1000) {

                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                    commitHome.setEnabled(false);

                                                    closePoupUp.setEnabled(false);

                                                    houseNameEditTxt.setEnabled(false);

                                                    deviceCode.setEnabled(false);

                                                    count.setVisibility(View.VISIBLE);

                                                    count.setText("Try again in " + countDown + " seconds");

                                                    countDown--;

                                                }

                                                @Override
                                                public void onFinish() {

                                                    countDown = 30;

                                                    counterInput = 0;

                                                    closePoupUp.setEnabled(true);

                                                    commitHome.setEnabled(true);

                                                    houseNameEditTxt.setEnabled(true);

                                                    deviceCode.setEnabled(true);

                                                    count.setVisibility(View.GONE);

                                                }

                                            }.start();

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                }

            }

        });

    }

    public void viewWidget(){

        //find object with id
        toolAddHome =  findViewById(R.id.action_add_home);

        closePoupUp =  findViewById(R.id.close_popup_home);

        addHome =  findViewById(R.id.cards_add_home);

        emptyInMemer =  findViewById(R.id.empty_device);

        count = findViewById(R.id.countDownTime);

        textConnection = findViewById(R.id.text_status);

        commitHome =  findViewById(R.id.add_home_code);

        deviceCode = findViewById(R.id.code_device);

        mProgressBar = findViewById(R.id.myDataLoaderProgressBar);

        fader = findViewById(R.id.fader);

        indicator = findViewById(R.id.recyclerview_indicator);

        fader.setVisibility(View.VISIBLE);

        rl = findViewById(R.id.internet_status);

        retry = findViewById(R.id.txt_retry);

        setLoadingAnimation();

        dialog = new Dialog(this);

        dialogAdd = new Dialog(this);

        recyclerView = findViewById(R.id.mRecyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        new LinearSnapHelper().attachToRecyclerView(recyclerView);

        indicator.attachToRecyclerView(recyclerView);

        mHouses = new ArrayList<> ();

        mAdapter = new RecyclerAdapterHouse (MainActivity.this, mHouses);

        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(MainActivity.this);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

    }

    private void setLoadingAnimation() {

        mProgressBar.show();

        addHome.setVisibility(View.GONE);

        addHome.setEnabled(false);

    }

    private void stopLoadingAnimation(){

        mProgressBar.hide();

        addHome.setVisibility(View.VISIBLE);

        addHome.setEnabled(true);

    }


    protected void onDestroy() {

        super.onDestroy();

        mDatabaseRef.removeEventListener(mDBListener);

    }

}
