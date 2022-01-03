package com.example.go4lunch.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.di.DI;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.NotificationsHelper;
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
        boolean receiveNotifications = service.getReceiveNotifications();
        if (remoteMessage.getNotification() != null && receiveNotifications) {
            // Get message sent by Firebase
            sendVisualNotification();
        }
    }

    private void sendVisualNotification() {
        // Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);
        String currentUserId = userManager.getCurrentUser().getUid();
        String restId;
        String restName;
        String restAddress;
        ArrayList<String> workmatesNames;
        ArrayList<User> workmatesList = service.getWorkmatesList();

        // get my restaurant
        User me = NotificationsHelper.getMeInWorkmatesList(workmatesList, currentUserId);
        restId = me.getRestaurantId();
        restName = me.getRestaurantName();
        restAddress = me.getRestaurantAddress();

        assert restId != null;
        if(!restId.equals("")) {
            // get workmates in this restaurant
            workmatesNames = NotificationsHelper.getWorkmatesInRestaurant(workmatesList, restId, currentUserId);

            // build text to notify
            StringBuilder workmatesFormatted = NotificationsHelper.getWorkmatesFormatted(workmatesNames, restName, restAddress, getString(R.string.you_eat_to), getString(R.string.located_to), getString(R.string.you_eat_alone), getString(R.string.you_eat_with));

            // Build a Notification object
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_baseline_restaurant_24)
                            .setContentText(workmatesFormatted)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(workmatesFormatted));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Support Version >= Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence channelName = "Firebase Messages";
                int importance = NotificationManager.IMPORTANCE_LOW;
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
