package org.d3ifcool.smart.Home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.d3ifcool.smart.Adapter.RecyclerAdapterDoor;
import org.d3ifcool.smart.Data;
import org.d3ifcool.smart.Model.Door;
import org.d3ifcool.smart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;

public class Fragment_List_Door extends Fragment implements View.OnClickListener, RecyclerAdapterDoor.OnItemClickListener {

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private RecyclerView mRecyclerView;
    private RecyclerAdapterDoor mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Door> mDoor;
    private CircleButton addDoor;

    private Dialog dialog_Door;
    private Button commitDoor;
    private EditText addDoorEdtxt, doorPin;
    private CardView addDoorCard;
    private  ImageView closePoupUpDoor;
    private ProgressDialog pd;


    public Fragment_List_Door() {

    }
//
//    private void openDetailActivity(String[] data){
//        Intent intent = new Intent(this, DetailsActivity.class);
//        intent.putExtra("NAME_KEY",data[0]);
//        intent.putExtra("DESCRIPTION_KEY",data[1]);
//        intent.putExtra("IMAGE_KEY",data[2]);
//        startActivity(intent);
    //    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_door_fragment,container,false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mRecyclerView = view.findViewById(R.id.mRecyclerView_detail);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dialog_Door = new Dialog(getActivity());

        mProgressBar = view.findViewById(R.id.myDataLoaderProgressBarDoor);
        mProgressBar.setVisibility(View.VISIBLE);

        addDoor = view.findViewById(R.id.addDoor_floating);
        addDoorCard = view.findViewById(R.id.cardAddDoor);
        addDoor.setOnClickListener(this);
        addDoorCard.setOnClickListener(this);

        mDoor = new ArrayList<>();
        mAdapter = new RecyclerAdapterDoor (getActivity(), mDoor);
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(getActivity());

        getDoor();

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

        }

        else {

            addDoor.setVisibility(View.GONE);
            addDoorCard.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }

    public void getDoor() {
        Intent i = getActivity().getIntent();
        String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Doors");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDoor.clear();

                for (DataSnapshot doorSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                    };
                    Map<String, Object> map = doorSnapshot.getValue(genericTypeIndicator);

                    Door uploadDoor = new Door();
                    uploadDoor.setDoorName((String) map.get("doorName"));

                    Door door = doorSnapshot.getValue(Door.class);
//                    Log.d(TAG, "onDataChange: " + door.getDoorName());
                    mDoor.add(uploadDoor);
//                for (DataSnapshot doorSnapshot : dataSnapshot.getChildren()) {
//                    try{
//                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
//                        Map<String,Object> map =  doorSnapshot.getValue(genericTypeIndicator);
//
//                        Door uploadDoor = new Door();
//                        uploadDoor.setDoorName( (String) map.get("doorName"));
//                        //Toast.makeText(HouseDetail.this, (String) map.get("name"), Toast.LENGTH_SHORT).show();
////                    Door upload = houseSnapshot.getValue(Door.class);
////                    upload.setKey(houseSnapshot.getKey());
//                        mDoor.add(uploadDoor);
//                        Data.checkRecyclerDoor = uploadDoor;


                }

                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);

                checkDoor();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);

            }

        });

    }


    public void onItemClick(int position) {
//        Door clickedTeacher= mDoor.get(position);
//        String[] teacherData={clickedTeacher.getDoorName()};
//        openDetailActivity(teacherData);
    }

    @Override
    public void onShowItemClick(int position) {
        Toast.makeText(getActivity(), "Item test", Toast.LENGTH_SHORT).show();
//        Door clickedTeacher= mDoor.get(position);
//        String[] teacherData={clickedTeacher.getDoorName()};
//        openDetailActivity(teacherData);
    }

    @Override
    public void onDeleteItemClick(int position) {
        Intent i = getActivity().getIntent();
        String deviceCode = i.getExtras().getString("DEVICECODE_KEY");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Doors");
        Door selectedItem = mDoor.get(position);
        final String selectedKey = selectedItem.getDoorName();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(selectedKey);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabaseRef.removeValue();
                Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

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
        closePoupUpDoor = (ImageView) dialog_Door.findViewById(R.id.close_popup_door);
        commitDoor = (Button) dialog_Door.findViewById(R.id.add_door_btn);
        addDoorEdtxt = (EditText) dialog_Door.findViewById(R.id.door_name_txt);
        doorPin = (EditText) dialog_Door.findViewById(R.id.door_pin);
        commitDoor.setOnClickListener(this);


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
                String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

                String str_door = addDoorEdtxt.getText().toString();
                String str_pin = doorPin.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Doors").child(str_door);

                Door door = new Door(str_door, str_pin);

                if (TextUtils.isEmpty(str_door)){
                    addDoorEdtxt.setError("Door name required");
                }

                else if (TextUtils.isEmpty(str_pin)){
                    doorPin.setError("Pin door required");

                }

                else {
//                    String uploadId = reference.push().getKey();
//                    reference.child("name").setValue(Data.housenameid);
                    reference.setValue(door);
                    Data.doorName = str_door;
//                    reference.child(uploadId).setValue(door);
                    dialog_Door.dismiss();


                }

                pd.hide();


            }
        });

    }
}
