package com.example.go4lunch.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;

import java.util.ArrayList;

public class WorkmateListAdapter extends RecyclerView.Adapter<WorkmateListAdapter.WorkmateListViewHolder> {
    private ArrayList<User> workmateList;
    private Context context;
    private Boolean isFromAllWorkmates;
    private static RecyclerViewClickListener itemListener;

    public WorkmateListAdapter(ArrayList<User> list, Context c, Boolean fromAllWorkmates, RecyclerViewClickListener itemListener) {
        workmateList = list;
        context = c;
        isFromAllWorkmates = fromAllWorkmates;
        this.itemListener = itemListener;
    }
    public WorkmateListAdapter(ArrayList<User> list, Context c, Boolean fromAllWorkmates) {
        workmateList = list;
        context = c;
        isFromAllWorkmates = fromAllWorkmates;
    }

    @NonNull
    @Override
    public WorkmateListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.workmate_list_cell, parent, false);
        return new WorkmateListViewHolder(view, itemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateListViewHolder holder, int position) {
        User user = workmateList.get(position);
        holder.display(user, context, isFromAllWorkmates);
    }

    @Override
    public int getItemCount() {
        return workmateList.size();
    }

    public static class WorkmateListViewHolder extends RecyclerView.ViewHolder {
        private final ImageView photo;
        private final TextView text;
        private String spotId;

        public WorkmateListViewHolder(final View itemView, RecyclerViewClickListener itemListener) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.profilePicture);
            text = ((TextView) itemView.findViewById(R.id.workmateListCellText));
            itemView.setOnClickListener(view -> {
                itemListener.recyclerViewListClicked(spotId);
            });
        }

        public void display(User user, Context context, Boolean isFromAllWorkmates) {
            spotId = user.getRestaurantId();
            Glide.with(context).load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(photo);
            String textToPrint = user.getUsername() + (isFromAllWorkmates? (user.getRestaurantName().equals("") ? " hasn't decided yet" : " is eating at " + user.getRestaurantName()) :  " is joining!");
            text.setText(textToPrint);
        }
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(String spotId);
    }

}
