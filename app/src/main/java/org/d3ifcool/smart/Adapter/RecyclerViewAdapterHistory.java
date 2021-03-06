package org.d3ifcool.smart.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.d3ifcool.smart.Activity.TimeLineMarker;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.util.List;


public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.MyViewHolder> {
    private Context mContext;
    private List<User> mData;
    private OnItemClickListener mListener;


    public RecyclerViewAdapterHistory(FragmentActivity activity, List<User> mHistory) {

        this.mContext = activity;

        this.mData = mHistory;

    }

    @Override
    public int getItemViewType(int position) {

        final int size = mData.size() - 1;

        if (size == 0)

            return ItemType.ATOM;

        else if (position == 0)

            return ItemType.START;

        else if (position == size)

            return ItemType.END;

        else return ItemType.NORMAL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history, parent, false), i);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User currentUser = mData.get(position);

        holder.username.setText(currentUser.getFullname());

        holder.lock.setText(currentUser.getLock());

        holder.door.setText(currentUser.getDoor());

        holder.time.setText(currentUser.getTime());

        Picasso.with(mContext)
                .load(currentUser.getLockImage())
                .placeholder(R.drawable.lock_door)
                .fit()
                .centerInside()
                .into(holder.status);

        Picasso.with(mContext)
                .load(currentUser.getImageurl())
                .placeholder(R.drawable.user_fix)
                .fit()
                .centerInside()
                .into(holder.photo);

    }

    @Override
    public int getItemCount() {

        return mData.size();

    }

    public class ItemType {

        public final static int NORMAL = 0;

        public final static int HEADER = 1;

        public final static int FOOTER = 2;

        public final static int START = 4;

        public final static int END = 8;

        public final static int ATOM = 16;
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

        private TextView lock, username, door, time;
        private ImageView photo, status;

        public MyViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            username = itemView.findViewById(R.id.name_history);

            photo = itemView.findViewById(R.id.imageAccount_history);

            lock = itemView.findViewById(R.id.by);

            status = itemView.findViewById(R.id.status_activity);

            door = itemView.findViewById(R.id.door_where);

            time = itemView.findViewById(R.id.timeAccess);

            TimeLineMarker mMarker = itemView.findViewById(R.id.item_time_line_mark);

            if (type == ItemType.ATOM) {

                mMarker.setBeginLine(null);

                mMarker.setEndLine(null);

            } else if (type == ItemType.START) {

                mMarker.setBeginLine(null);

            } else if (type == ItemType.END) {

                mMarker.setEndLine(null);

            }

            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);

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

//            menu.setHeaderTitle("Select Action");
//
////            MenuItem showItem = menu.add( Menu.NONE, 1, 1, "Show");
//
//            MenuItem deleteItem = menu.add(Menu.NONE, 2, 2, "Delete");

//            showItem.setOnMenuItemClickListener(this);

//            deleteItem.setOnMenuItemClickListener(this);

        }

    }

}

