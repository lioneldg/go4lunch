package com.example.go4lunch.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.go4lunch.R;
import com.example.go4lunch.di.DI;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.ui.MainActivity;
import com.example.go4lunch.ui.manager.UserManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationsService extends FirebaseMessagingService {
    private final UserManager userManager = UserManager.getInstance();
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            // Get message sent by Firebase
            sendVisualNotification();
        }
    }

    private void sendVisualNotification() {

        // Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);
        String currentUserId = userManager.getCurrentUser().getUid();
        String restId = null;
        String restName = null;
        String restAddress = null;
        ArrayList<String> workmatesNames = new ArrayList<>();
        ArrayList<User> workmatesList = service.getWorkmatesList();

        // get my restaurant id
        for (int i = 0; i < workmatesList.size(); i++){
            if(workmatesList.get(i).getUid().equals(currentUserId)){
                restId = workmatesList.get(i).getRestaurantId();
                restName = workmatesList.get(i).getRestaurantName();
                restAddress = workmatesList.get(i).getRestaurantAddress();
                break;
            }
        }

        assert restId != null;
        if(!restId.equals("")) {
            // get workmates in this restaurant
            for (int i = 0; i < workmatesList.size(); i++) {
                if (workmatesList.get(i).getRestaurantId().equals(restId) && !workmatesList.get(i).getUid().equals(currentUserId)) {
                    workmatesNames.add(workmatesList.get(i).getUsername());
                }
            }

            StringBuilder workmatesFormatted = new StringBuilder();
            workmatesFormatted.append(getString(R.string.you_eat_to)).append(restName).append("\n").append(getString(R.string.located_to)).append(restAddress).append("\n\n");
            if (workmatesNames.size() == 0) {
                workmatesFormatted.append(getString(R.string.you_eat_alone));
            } else {
                workmatesFormatted.append(getString(R.string.you_eat_with)).append("\n");
                for (int i = 0; i < workmatesNames.size(); i++) {
                    workmatesFormatted.append(workmatesNames.get(i)).append(i + 1 == workmatesNames.size() ? "." : ",\n");
                }
            }

            // Build a Notification object
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_baseline_restaurant_24)
                            .setContentText(workmatesFormatted)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(workmatesFormatted))
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Support Version >= Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence channelName = "Firebase Messages";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            // Show notification
            int NOTIFICATION_ID = 7;
            String NOTIFICATION_TAG = "FIREBASENOTIF";
            notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}