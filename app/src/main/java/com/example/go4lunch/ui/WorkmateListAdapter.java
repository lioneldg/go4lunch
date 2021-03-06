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
    private final ArrayList<User> workmateList;
    private final Context context;
    private final Boolean isFromAllWorkmates;
    private static ClickListener itemListener;

    public WorkmateListAdapter(ArrayList<User> list, Context c, Boolean fromAllWorkmates, ClickListener itemListener) {
        workmateList = list;
        context = c;
        isFromAllWorkmates = fromAllWorkmates;
        WorkmateListAdapter.itemListener = itemListener;
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

        public WorkmateListViewHolder(final View itemView, ClickListener itemListener) {
            super(itemView);
            photo = itemView.findViewById(R.id.profilePicture);
            text = itemView.findViewById(R.id.workmateListCellText);
            itemView.setOnClickListener(view -> itemListener.listClicked(spotId));
        }

        public void display(User user, Context context, Boolean isFromAllWorkmates) {
            spotId = user.getRestaurantId();
            Glide.with(context).load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(photo);
            String textToPrint = user.getUsername() + (isFromAllWorkmates? (user.getRestaurantName().equals("") ? " " + context.getString(R.string.hasn_t_decided_yet) : " " + context.getString(R.string.is_eating_at) + " " + user.getRestaurantName()) :  " " + context.getString(R.string.is_joining));
            text.setText(textToPrint);
        }
    }

    public interface ClickListener {
        void listClicked(String spotId);
    }

}
