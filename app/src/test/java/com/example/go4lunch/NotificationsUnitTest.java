package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.go4lunch.models.User;
import com.example.go4lunch.tools.NotificationsHelper;

import org.junit.Test;

import java.util.ArrayList;

public class NotificationsUnitTest {
    private final User user1 = new User("idUser1", "nameUser1", null, "restName1", "restAddress1", "restId1");
    private final User user2 = new User("idUser2", "nameUser2", null, "restName1", "restAddress1", "restId1");
    private final User user3 = new User("idUser3", "nameUser3", null, "", "", "");
    private final User user4 = new User("idUser4", "nameUser4", null, "restName2", "restAddress2", "restId2");
    private final ArrayList<User> workmatesList = new ArrayList<>();
    private final String currentUserId = user1.getUid();
    private final String currentUserRestId = user1.getRestaurantId();
    private final String currentUserRestName = user1.getRestaurantName();
    private final String currentUserRestAddress = user1.getRestaurantAddress();
    {
        workmatesList.add(user1);
        workmatesList.add(user2);
        workmatesList.add(user3);
        workmatesList.add(user4);
    }

    @Test
    public void getMeInWorkListTest(){
        assertEquals(user1, NotificationsHelper.getMeInWorkmatesList(workmatesList, currentUserId));
        assertNotEquals(user1, NotificationsHelper.getMeInWorkmatesList(workmatesList, user2.getUid()));
        assertNotEquals(user1, NotificationsHelper.getMeInWorkmatesList(workmatesList, null));
        assertNotEquals(user1, NotificationsHelper.getMeInWorkmatesList(new ArrayList<>(), currentUserId));
    }

    @Test
    public void getWorkmatesInRestaurantTest(){
        ArrayList<String> workmatesInRestaurant = new ArrayList<>();
        assertNotEquals(workmatesInRestaurant, NotificationsHelper.getWorkmatesInRestaurant(workmatesList, currentUserRestId, currentUserId));
        workmatesInRestaurant.add(user2.getUsername());
        assertEquals(workmatesInRestaurant, NotificationsHelper.getWorkmatesInRestaurant(workmatesList, currentUserRestId, currentUserId));
    }

    @Test
    public void getWorkmatesFormattedTest(){
        ArrayList<String> workmatesNames = new ArrayList<>();
        String workmatesFormatted = "You eat to" + currentUserRestName
                + "\nLocated to" + currentUserRestAddress
                + "\n\nYou will be alone";
        assertEquals(workmatesFormatted, NotificationsHelper.getWorkmatesFormatted(workmatesNames, currentUserRestName, currentUserRestAddress, "You eat to", "Located to", "You will be alone", "There will be :").toString());
        workmatesNames.add(user2.getUsername());
        workmatesFormatted = "You eat to" + currentUserRestName
                + "\nLocated to" + currentUserRestAddress
                + "\n\nThere will be :\n"
                + user2.getUsername() + ".";
        assertEquals(workmatesFormatted, NotificationsHelper.getWorkmatesFormatted(workmatesNames, currentUserRestName, currentUserRestAddress, "You eat to", "Located to", "", "There will be :").toString());
    }
}