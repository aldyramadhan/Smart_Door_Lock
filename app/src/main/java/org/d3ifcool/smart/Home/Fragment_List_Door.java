package org.d3ifcool.smart.Home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.wang.avi.AVLoadingIndicatorView;

import org.d3ifcool.smart.Adapter.RecyclerAdapterDoor;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import hari.bounceview.BounceView;
import info.androidramp.gearload.Loading;

public class Fragment_List_Door extends Fragment implements View.OnClickListener, RecyclerAdapterDoor.OnItemClickListener {

    FirebaseAuth auth;
    DatabaseReference reference, reference0;
    FirebaseUser firebaseUser;
    private RecyclerView mRecyclerView;
    private RecyclerAdapterDoor mAdapter;
    private AVLoadingIndicatorView mProgressBar;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase database;
    private ValueEventListener mDBListener;
    private List<Door> mDoor;
    private CircleButton addDoor;

    private Dialog dialog_Door;
    private Button commitDoor;
    private EditText addDoorEdtxt, doorPin;
    private CardView addDoorCard;
    private  ImageView closePoupUpDoor;
    private ProgressDialog pd;
    IndefinitePagerIndicator indicator;
    FrameLayout fader;



    public Fragment_List_Door() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_door_fragment,container,false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mRecyclerView = view.findViewById(R.id.mRecyclerView_detail);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);
        dialog_Door = new Dialog(getContext());

        mProgressBar = view.findViewById(R.id.myDataLoaderProgressBarDoor);

        addDoor = view.findViewById(R.id.addDoor_floating);
        addDoorCard = view.findViewById(R.id.cardAddDoor);
        indicator = view.findViewById(R.id.recyclerview_indicator_door);
        fader = view.findViewById(R.id.fader);
        addDoor.setOnClickListener(this);
        addDoorCard.setOnClickListener(this);

        mDoor = new ArrayList<>();
        mAdapter = new RecyclerAdapterDoor (getActivity(), mDoor);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        indicator.attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(this);

        getDoor();
        setLoadingAnimation();


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0){
                    addDoor.setVisibility(View.VISIBLE);

                }

                else if (dy > 0){
                    addDoor.setVisibility(View.GONE);
                }

                else if (dy == 0){
                    addDoor.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }


    public void checkDoor(){

        if (mAdapter.getItemCount() != 0){

            addDoor.setVisibility(View.VISIBLE);
            addDoorCard.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.VISIBLE);

        }

        else {

            addDoor.setVisibility(View.GONE);
            addDoorCard.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);

        }

    }


    public void getDoor() {
        Intent i = getActivity().getIntent();
        final String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("ListDoor");
//        mDatabaseRef = database.getInstance().getReference();
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mDatabaseRef == null) {
                    database = FirebaseDatabase.getInstance();
                    database.setPersistenceEnabled(true);
                    mDatabaseRef = database.getReference();
                }

                try {

                    mDoor.clear();

//                    for (DataSnapshot doorSnapshot : dataSnapshot.child("Devices").child(deviceCode).child("DoorApp").getChildren()) {
//                        String door = doorSnapshot.getValue(String.class);
//                        Door house = dataSnapshot.child("Devices").child(door).getValue(Door.class);

                    for (DataSnapshot doorSnapshot : dataSnapshot.getChildren()) {
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                        };
                        Map<String, Object> map = doorSnapshot.getValue(genericTypeIndicator);

                        Door uploadDoor = new Door();
                        uploadDoor.setDoorName((String) map.get("doorName"));
                        uploadDoor.setDoorPin((String) map.get("doorPin"));

                        Door door = doorSnapshot.getValue(Door.class);
                        mDoor.add(door);

                    }
                }catch (Exception e){}

                mAdapter.notifyDataSetChanged();
                stopLoadingAnimation();
                checkDoor();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                stopLoadingAnimation();
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();


            }

        });

    }


    private void setLoadingAnimation() {
        fader.setVisibility(View.VISIBLE);
        mProgressBar.show();
    }

    private void stopLoadingAnimation(){
        fader.setVisibility(View.GONE);
        mProgressBar.hide();
    }



    public void onItemClick(int position) {
//        Door clickedDoors= mDoor.get(position);
//        String[] doorsData={clickedDoors.getDoorName(), String.valueOf(clickedDoors.getDoorLock())};
//        Toast.makeText(getActivity(),clickedDoors.getDoorName(), Toast.LENGTH_SHORT).show();
////        openDetailActivity(doorsData);
    }


    @Override
    public void onShowItemClick(int position) {
//        Door clickedDoors= mDoor.get(position);
//        String[] doorsData={clickedDoors.getDoorName(), String.valueOf(clickedDoors.getDoorLock())};
////        openDetailActivity(doorsData);
    }


    @Override
    public void onDeleteItemClick(int position) {
        Door selectedItem = mDoor.get(position);
        final String selectedKey = selectedItem.getDoorPin();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("DELETE DOOR");
        builder.setMessage("are you sure to delete this door ?");
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = getActivity().getIntent();
                        String deviceCode = i.getExtras().getString("DEVICECODE_KEY");

                        reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("ListDoor");
                        reference.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builder.show();

    }


    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardAddDoor :
                showDialogAddDoor();
                break;

            case R.id.addDoor_floating :
                showDialogAddDoor();
                break;
        }

    }


    public void showDialogAddDoor(){
        dialog_Door.setContentView(R.layout.add_door_popup);
        dialog_Door.setCanceledOnTouchOutside(false);
        closePoupUpDoor = (ImageView) dialog_Door.findViewById(R.id.close_popup_door);
        commitDoor = (Button) dialog_Door.findViewById(R.id.add_door_btn);
        addDoorEdtxt = (EditText) dialog_Door.findViewById(R.id.door_name_txt);
        doorPin = (EditText) dialog_Door.findViewById(R.id.door_pin);
        commitDoor.setOnClickListener(this);
        BounceView.addAnimTo(dialog_Door);

        closePoupUpDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Door.dismiss();

            }

        });

        dialog_Door.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_Door.show();

        commitDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Please wait...");
                pd.show();

                Intent i = getActivity().getIntent();
                final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

                final String str_door = addDoorEdtxt.getText().toString();
                final String str_pin = doorPin.getText().toString();

                final Door door = new Door(str_door, str_pin);

                if (TextUtils.isEmpty(str_door)){
                    addDoorEdtxt.setError("Door name required");
                }

                else if (TextUtils.isEmpty(str_pin)){
                    doorPin.setError("Pin door required");

                }

                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Doors").child(str_pin);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reference0 = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("ListDoor").child(str_pin);

                        if (dataSnapshot.exists()) {
                            Door getDoor = dataSnapshot.getValue(Door.class);

                            reference0.setValue(door);
                            dialog_Door.dismiss();
                            Toast.makeText(getActivity(), str_door + " door added", Toast.LENGTH_SHORT).show();

                            pd.hide();
                            return;

                            }

                            else{
                            pd.hide();
                            doorPin.setError(str_pin + " Not found");
                            }

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

//                    reference.setValue(door);
//                    reference0.setValue(door);
//                    Data.doorName = str_door;
//                    dialog_Door.dismiss();
//
//
//                pd.hide();

            }
        });

    }
}
