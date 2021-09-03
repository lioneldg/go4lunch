package com.example.go4lunch.ui;

import static com.example.go4lunch.tools.PhotoRefToBitmap.getBitmap;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.SpotListCellBinding;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PhotoRefToBitmap;
//VERIFIER LE CALCUL DES ETOILES CAR IL Y EN A TJS 3!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvViewHolder> {
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();

    @NonNull
    @Override
    public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.spot_list_cell, parent, false);
        return new RvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvViewHolder holder, int position) {
        holder.display(service.getNearbySearchResults().get(position));
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

        public RvViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cellTitle);
            address = itemView.findViewById(R.id.cellAddress);
            hours = itemView.findViewById(R.id.cellOpenHours);
            distance = itemView.findViewById(R.id.cellDistance);
            workmatesNumber = itemView.findViewById(R.id.cellWorkmatesNumber);
            image = itemView.findViewById(R.id.cellImage);
            star1 = itemView.findViewById(R.id.start1);
            star2 = itemView.findViewById(R.id.start2);
            star3 = itemView.findViewById(R.id.start3);


            /*itemView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle(currentPair.first)
                            .setMessage(currentPair.second)
                            .show();
                }
            });*/
        }

        public void display(NearbySearchResult nearbySearchResult) {
            title.setText(nearbySearchResult.getName());
            address.setText(nearbySearchResult.getVicinity());
            hours.setText(nearbySearchResult.getOpen_now() ? "Open": "Closed");
            distance.setText(nearbySearchResult.getDistanceBetween()+" m");
            workmatesNumber.setText("(3)");
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

}
