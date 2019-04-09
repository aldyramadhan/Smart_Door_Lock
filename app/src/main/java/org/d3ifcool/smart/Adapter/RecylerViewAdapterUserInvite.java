package org.d3ifcool.smart.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecylerViewAdapterUserInvite extends RecyclerView.Adapter<RecylerViewAdapterUserInvite.MyViewHolder> {
    private Context mContext;
    private List<User> mConnect;
    private OnItemClickListener mListener;
    Dialog myDialog;

    public RecylerViewAdapterUserInvite(FragmentActivity activity, List<User> mConnectlist) {
        mContext = activity;
        mConnect = mConnectlist;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_member, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        viewHolder.item_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "test click" + String.valueOf(viewHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        });

        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        User currentUser = mConnect.get(position);
        holder.username.setText(currentUser.getFullname());
        holder.email.setText(currentUser.getEmail());
        holder.start.setText("EXP :");
        holder.end.setText(currentUser.getExpired());
        Picasso.with(mContext)
                .load(currentUser.getImageurl())
                .placeholder(R.drawable.user_fix)
                .fit()
                .centerCrop()
                .into(holder.photo);

        Intent i = ((Activity) mContext).getIntent();
        final String name =i.getExtras().getString("NAME_KEY");
        final String deviceCode =i.getExtras().getString("DEVICECODE_KEY");

//        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceCode).child("Member");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot expSnapshot : dataSnapshot.getChildren()) {
//                    User upload = expSnapshot.getValue(User.class);
//                    String endTime = upload.getExpired();
//
//                    Log.d("en", "ondate" + endTime);
//                    if (endTime.equals("always")){
//
//                        holder.start.setVisibility(View.GONE);
//                        holder.end.setVisibility(View.GONE);
//
//                    }
//
//                    else {
//
//                        holder.start.setVisibility(View.VISIBLE);
//                        holder.end.setVisibility(View.VISIBLE);
//
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
    }


    @Override
    public int getItemCount() {
        return mConnect.size();
    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String today= dateFormat.format(date);
        return today;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShowItemClick(int position);
        void onDeleteItemClick(int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private FrameLayout item_user;
        private TextView fullname, username, email, start, end;
        private CircleImageView photo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_user = (FrameLayout) itemView.findViewById(R.id.item_user);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email_member);
            start = itemView.findViewById(R.id.start_access);
            end = itemView.findViewById(R.id.expired_member);
            photo = itemView.findViewById(R.id.imageMember);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem showItem = menu.add( Menu.NONE, 1, 1, "Show");
            MenuItem deleteItem = menu.add(Menu.NONE, 2, 2, "Delete");

            showItem.setOnMenuItemClickListener(this);
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onShowItemClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteItemClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

    }
}
