package com.example.go4lunch.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.R;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PhotoRefToBitmap;
import com.example.go4lunch.ui.manager.UserManager;

import java.util.ArrayList;

public class RestaurantsListAdapter extends RecyclerView.Adapter<RestaurantsListAdapter.RvViewHolder> {
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private Context context;
    private static RecyclerViewClickListener itemListener;

    public RestaurantsListAdapter(Context context, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_spot_list_cell, parent, false);
        return new RvViewHolder(view, itemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RvViewHolder holder, int position) {
        holder.display(service.getNearbySearchResults().get(position), position, context);
    }

    @Override
    public int getItemCount() {
        return service.getNearbySearchResults().size();
    }


    public static class RvViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView address;
        private final TextView hours;
        private final TextView distance;
        private final TextView workmatesNumber;
        private final ImageView image;
        private final TextView star1;
        private final TextView star2;
        private final TextView star3;
        private int itemPosition;
        private String placeId;
        private UserManager userManager = UserManager.getInstance();

        public RvViewHolder(final View itemView, RecyclerViewClickListener itemListener) {
            super(itemView);
            title = itemView.findViewById(R.id.cellTitle);
            address = itemView.findViewById(R.id.cellAddress);
            hours = itemView.findViewById(R.id.cellOpenHours);
            distance = itemView.findViewById(R.id.cellDistance);
            workmatesNumber = itemView.findViewById(R.id.cellWorkmatesNumber);
            image = itemView.findViewById(R.id.cellImage);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);


            itemView.setOnClickListener(view -> {
                itemListener.recyclerViewListClicked(placeId);
            });
        }

        public void display(NearbySearchResult nearbySearchResult, int position, Context context) {
            placeId = nearbySearchResult.getPlace_id();
            //workmateList observer
            final Observer<ArrayList<User>> workmateListObserver = workmateList -> {
                workmatesNumber.setText("("+workmateList.size()+")");
            };
            userManager.getWorkmatesList(placeId, nearbySearchResult.getName()).observe((LifecycleOwner) context, workmateListObserver);

            itemPosition = position;
            title.setText(nearbySearchResult.getName());
            address.setText(nearbySearchResult.getVicinity());
            hours.setText(nearbySearchResult.getOpen_now() ? "Open": "Closed");
            distance.setText(nearbySearchResult.getDistanceBetween()+" m");
            Bitmap photo = PhotoRefToBitmap.getBitmap(nearbySearchResult.getPhoto_reference(), 200);
            image.setImageBitmap(photo);
            int starNumber = nearbySearchResult.getRating();
            switch(starNumber){
                case 3: star3.setVisibility(View.VISIBLE);
                        star1.setVisibility(View.VISIBLE);
                        star1.setVisibility(View.VISIBLE);
                        break;

                case 2: star3.setVisibility(View.INVISIBLE);
                        star1.setVisibility(View.VISIBLE);
                        star1.setVisibility(View.VISIBLE);
                        break;

                case 1: star3.setVisibility(View.INVISIBLE);
                        star2.setVisibility(View.INVISIBLE);
                        star1.setVisibility(View.VISIBLE);
                        break;

                default: star3.setVisibility(View.INVISIBLE);
                         star2.setVisibility(View.INVISIBLE);
                         star1.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(String place_id);
    }

}
