package com.example.go4lunch.tools;
import com.example.go4lunch.models.User;

import java.util.ArrayList;

public class NotificationsHelper {

    public static User getMeInWorkmatesList(ArrayList<User> workmatesList, String currentUserId){
        User me = null;
        for (int i = 0; i < workmatesList.size(); i++){
            if(workmatesList.get(i).getUid().equals(currentUserId)){
                me = workmatesList.get(i);
                break;
            }
        }
        return me;
    }

    public static ArrayList<String> getWorkmatesInRestaurant(ArrayList<User> workmatesList, String restId, String currentUserId){
        ArrayList<String> workmatesNames = new ArrayList<>();
        for (int i = 0; i < workmatesList.size(); i++) {
            if (workmatesList.get(i).getRestaurantId().equals(restId) && !workmatesList.get(i).getUid().equals(currentUserId)) {
                workmatesNames.add(workmatesList.get(i).getUsername());
            }
        }
        return  workmatesNames;
    }

    public static StringBuilder getWorkmatesFormatted(ArrayList<String> workmatesNames, String restName, String restAddress, String you_eat_to, String located_to, String you_eat_alone, String you_eat_with){
        StringBuilder workmatesFormatted = new StringBuilder();
        workmatesFormatted.append(you_eat_to).append(restName).append("\n").append(located_to).append(restAddress).append("\n\n");
        if (workmatesNames.size() == 0) {
            workmatesFormatted.append(you_eat_alone);
        } else {
            workmatesFormatted.append(you_eat_with).append("\n");
            for (int i = 0; i < workmatesNames.size(); i++) {
                workmatesFormatted.append(workmatesNames.get(i)).append(i + 1 == workmatesNames.size() ? "." : ",\n");
            }
        }
        return  workmatesFormatted;
    }
}
