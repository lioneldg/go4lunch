package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.tools.PlaceSearchHelper;
import com.example.go4lunch.tools.NotificationsHelper;

import org.junit.Test;

import java.util.ArrayList;

public class MainUnitTest {
    private final User user1 = new User("idUser1", "nameUser1", null, "restName1", "restAddress1", "restId1");
    private final User user2 = new User("idUser2", "nameUser2", null, "restName1", "restAddress1", "restId1");
    private final User user3 = new User("idUser3", "nameUser3", null, "", "", "");
    private final User user4 = new User("idUser4", "nameUser4", null, "restName2", "restAddress2", "restId2");
    private final ArrayList<User> workmatesList = new ArrayList<>();
    private final String currentUserId = user1.getUid();

    {
        workmatesList.add(user1);
        workmatesList.add(user2);
        workmatesList.add(user3);
        workmatesList.add(user4);
    }
    String spotId1 = "ChIJBdhDoCDGDkgRT4dY16bCaC0";
    String spotId2 = "ChIJtcqTAzLeDkgRnmsS-LuhMc0";
    String spotId3 = "ChIJB1TMI_DJDkgRbobS19hYE1Q";
    String urlRequestPlaceResult = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"result\" : {\n" +
            "      \"formatted_phone_number\" : \"02 99 39 60 06\",\n" +
            "      \"geometry\" : {\n" +
            "         \"location\" : {\n" +
            "            \"lat\" : 48.33261719999999,\n" +
            "            \"lng\" : -1.5354094\n" +
            "         },\n" +
            "         \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "               \"lat\" : 48.3339740302915,\n" +
            "               \"lng\" : -1.534227669708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "               \"lat\" : 48.3312760697085,\n" +
            "               \"lng\" : -1.536925630291502\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      \"name\" : \"Auberge de la Tourelle\",\n" +
            "      \"opening_hours\" : {\n" +
            "         \"open_now\" : true,\n" +
            "         \"periods\" : [\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 0,\n" +
            "                  \"time\" : \"1400\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 0,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 1,\n" +
            "                  \"time\" : \"1400\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 1,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 2,\n" +
            "                  \"time\" : \"1400\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 2,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 4,\n" +
            "                  \"time\" : \"1400\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 4,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 5,\n" +
            "                  \"time\" : \"1400\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 5,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 5,\n" +
            "                  \"time\" : \"2100\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 5,\n" +
            "                  \"time\" : \"1900\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 6,\n" +
            "                  \"time\" : \"1400\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 6,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 6,\n" +
            "                  \"time\" : \"2100\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 6,\n" +
            "                  \"time\" : \"1900\"\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"weekday_text\" : [\n" +
            "            \"Monday: 12:00 – 2:00 PM\",\n" +
            "            \"Tuesday: 12:00 – 2:00 PM\",\n" +
            "            \"Wednesday: Closed\",\n" +
            "            \"Thursday: 12:00 – 2:00 PM\",\n" +
            "            \"Friday: 12:00 – 2:00 PM, 7:00 – 9:00 PM\",\n" +
            "            \"Saturday: 12:00 – 2:00 PM, 7:00 – 9:00 PM\",\n" +
            "            \"Sunday: 12:00 – 2:00 PM\"\n" +
            "         ]\n" +
            "      },\n" +
            "      \"photos\" : [\n" +
            "         {\n" +
            "            \"height\" : 2448,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101557958287251296998\\\"\\u003eAuberge de la Tourelle\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uECPiZqVG3c-XK4O8-mbCj9r8G_5-WPsO17AopM1jlFJrJaCoB-hkslC7I619vWDmi7fcnIGKU87C7IETXdDFlZ7g0O3s0XDlCf5OVZUc33w_nJhrhfDiZaczQ_A78awhNhAWOY0cCmgZmO64UfJlfSlpNZoC2xuv9T9KFy0SkGp4CDC\",\n" +
            "            \"width\" : 3264\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 3024,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/113424983221395508948\\\"\\u003ephilippe coeur-quêtin\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uED3zj8t_fMpQaDBpuT_ST8GC5Xc5uWvRQwqFZUZMyeleRWy325Tc3_dzgeH2dL5EwtEU_yJgb0pk7R4LMUCPmN7UtPcWvFDL2mtvcSLilP0uZE8mH1o7emYjcSKM2jozlAlwIiiA6SRr6sByKx0ktMINx0L0qhmLbxhZUFM4M1gNln4\",\n" +
            "            \"width\" : 4032\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 2448,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101557958287251296998\\\"\\u003eAuberge de la Tourelle\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uECsq0MrxHi1Ppy8QlN_aiX64Sme3ru6R4rr0lUIWb2IGn-7Y65QCxdh_MzHBKQHZsmuorizyWab9Gvl3FC-AZ4naLAV2qXIA8X_vxoflndPfYlXR85FasqYzM0XqnEXYfMJtw6sLgMKm-lb1XBQvTvyn6xVi_sZxb7hHNOpWlLxyh5n\",\n" +
            "            \"width\" : 3264\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 2448,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101557958287251296998\\\"\\u003eAuberge de la Tourelle\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uEAGuPwwfkCAiV17MC9U8edsRKljL137uyVmxXtYOjShU4blY8JOOVQLqTuqm0z1XWgFfkuTd8YByMJ8Z00qyJhuXKPnXhw2qwvJwA5UQgPWr8Ry0MLbbc8FjSYmipznzJTMEo7zpGjp8LU8FZ7NqyuXJI9-KRtLtWAAXsOrIk4EiV3J\",\n" +
            "            \"width\" : 3264\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 4608,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101722006951059937944\\\"\\u003eBenoit Prince\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uEC87EkZysV9KI7263_e7yGu9Mn5kcJ2BpgfOpB9MUFDQ_DrABSr24mks6RvD3tXPHj1z1TZBeii3V2CNrbniKCw6KkayW92mPMLxkKHSzcP9Gk4V763fFHbDYMg65Hqcw6uRaNnewZETk4AUY_Q2Uf4ZFTgfRUDm6GPenfMEix9kDz5\",\n" +
            "            \"width\" : 3456\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 2448,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101557958287251296998\\\"\\u003eAuberge de la Tourelle\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uEBwUMT_kSREcX7bsnvbOzedNtScA8vuO-v3xY3CxTYkHylCT0yg_TIda6gP24GfLuamOCi-cqDO6E9jT2gDpxjq6f_buDJJtU3GKTWWck4Jf-JbpnxZqrwJzQx3eZPBt2ypvRaDp2ks-YJXNS85Zw0Gtyb4Uh3VPmSH-PI1dw1v264i\",\n" +
            "            \"width\" : 3264\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 2736,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117914069272560112374\\\"\\u003eMoran Abadie\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uEBSzCwszOtNKOS9gliok9y7N05m88MNG9Llrrs0_i980Xv079cAPxqwXsxnSRdIdi91WG8MORoeXS2IkbHRr1YcNVj79VyvyA4Vc7BDreMbAsnAg2CLbQMfcnG9mrc9VuPweZ2pm8A5FRGsOmF9p5WFq7KP3ck4OBkHJQXst08C-IEx\",\n" +
            "            \"width\" : 3648\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 4128,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110230647279221952236\\\"\\u003efée éric\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uEA_KQWM30yRiAzgRQt5usyNZKxJspwFNsJuSPc0FVtKW1BI9KIhsCKuMkJuWsC42pW72RUwU4HTcQZiHdSEm8X9-nB1acju2ISHDkA903G84qcyCWGqwsapKp6v6WvxOsv0SbmSH2_LgNBtfbCuGJcQ-S5cdGPiM9C3ghS5cngQdjs7\",\n" +
            "            \"width\" : 2322\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 1920,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/111848990555124614205\\\"\\u003eChristophe Barbier\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uECdiZ5EpEfjQIwiIukQQClzLMJ5HivY2BiG_2RrgEQycJonfbaeOTBHf1hpCxmbHxmyjcZir_BcVTvWI_1UDdy8EUd7RvB9iFxIAMnwGY23lPp3CPRbOIW_sMXoh-O8pr2CkA3t6T4kLUYWdGq2ALSVsFl2EGryX0ZgQR_MpWZAxq-R\",\n" +
            "            \"width\" : 1080\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 2736,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117914069272560112374\\\"\\u003eMoran Abadie\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"Aap_uEBD4lB4iOe7b_ItNMIr0X9rKR2k6iCGBRUg0AkbarDYN0WMf6BmjwJezXglr2RlwzDa16p6nzekj97Jxs9DOJUIaLURBTJZpCKAGuVv3UGzJILqmi-trzVLLokhxQmA5jiYXfslRu1vOQqeDUPz7HzBHFZYPNsVEQon8OUVPxuULhO8\",\n" +
            "            \"width\" : 3648\n" +
            "         }\n" +
            "      ],\n" +
            "      \"rating\" : 4.4,\n" +
            "      \"vicinity\" : \"1 Place de la Mairie, Sens-de-Bretagne\",\n" +
            "      \"website\" : \"https://aubergelatourelle.wordpress.com/\"\n" +
            "   },\n" +
            "   \"status\" : \"OK\"\n" +
            "}\n";

    String requestPredictionsResult = "{\n" +
            "   \"predictions\" : [\n" +
            "      {\n" +
            "         \"description\" : \"Auberge de la Tourelle, Place de la Mairie, Sens-de-Bretagne, France\",\n" +
            "         \"matched_substrings\" : [\n" +
            "            {\n" +
            "               \"length\" : 2,\n" +
            "               \"offset\" : 0\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJBdhDoCDGDkgRT4dY16bCaC0\",\n" +
            "         \"reference\" : \"ChIJBdhDoCDGDkgRT4dY16bCaC0\",\n" +
            "         \"structured_formatting\" : {\n" +
            "            \"main_text\" : \"Auberge de la Tourelle\",\n" +
            "            \"main_text_matched_substrings\" : [\n" +
            "               {\n" +
            "                  \"length\" : 2,\n" +
            "                  \"offset\" : 0\n" +
            "               }\n" +
            "            ],\n" +
            "            \"secondary_text\" : \"Place de la Mairie, Sens-de-Bretagne, France\"\n" +
            "         },\n" +
            "         \"terms\" : [\n" +
            "            {\n" +
            "               \"offset\" : 0,\n" +
            "               \"value\" : \"Auberge de la Tourelle\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 24,\n" +
            "               \"value\" : \"Place de la Mairie\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 44,\n" +
            "               \"value\" : \"Sens-de-Bretagne\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 62,\n" +
            "               \"value\" : \"France\"\n" +
            "            }\n" +
            "         ],\n" +
            "         \"types\" : [\n" +
            "            \"lodging\",\n" +
            "            \"bar\",\n" +
            "            \"restaurant\",\n" +
            "            \"food\",\n" +
            "            \"point_of_interest\",\n" +
            "            \"establishment\"\n" +
            "         ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"description\" : \"Au Marché des Lices, Place du Bas des Lices, Rennes, France\",\n" +
            "         \"matched_substrings\" : [\n" +
            "            {\n" +
            "               \"length\" : 2,\n" +
            "               \"offset\" : 0\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJtcqTAzLeDkgRnmsS-LuhMc0\",\n" +
            "         \"reference\" : \"ChIJtcqTAzLeDkgRnmsS-LuhMc0\",\n" +
            "         \"structured_formatting\" : {\n" +
            "            \"main_text\" : \"Au Marché des Lices\",\n" +
            "            \"main_text_matched_substrings\" : [\n" +
            "               {\n" +
            "                  \"length\" : 2,\n" +
            "                  \"offset\" : 0\n" +
            "               }\n" +
            "            ],\n" +
            "            \"secondary_text\" : \"Place du Bas des Lices, Rennes, France\"\n" +
            "         },\n" +
            "         \"terms\" : [\n" +
            "            {\n" +
            "               \"offset\" : 0,\n" +
            "               \"value\" : \"Au Marché des Lices\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 21,\n" +
            "               \"value\" : \"Place du Bas des Lices\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 45,\n" +
            "               \"value\" : \"Rennes\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 53,\n" +
            "               \"value\" : \"France\"\n" +
            "            }\n" +
            "         ],\n" +
            "         \"types\" : [\n" +
            "            \"meal_takeaway\",\n" +
            "            \"restaurant\",\n" +
            "            \"food\",\n" +
            "            \"point_of_interest\",\n" +
            "            \"store\",\n" +
            "            \"establishment\"\n" +
            "         ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"description\" : \"Auberge La Maison Neuve, La Maison Neuve, Chauvigné, France\",\n" +
            "         \"matched_substrings\" : [\n" +
            "            {\n" +
            "               \"length\" : 2,\n" +
            "               \"offset\" : 0\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJB1TMI_DJDkgRbobS19hYE1Q\",\n" +
            "         \"reference\" : \"ChIJB1TMI_DJDkgRbobS19hYE1Q\",\n" +
            "         \"structured_formatting\" : {\n" +
            "            \"main_text\" : \"Auberge La Maison Neuve\",\n" +
            "            \"main_text_matched_substrings\" : [\n" +
            "               {\n" +
            "                  \"length\" : 2,\n" +
            "                  \"offset\" : 0\n" +
            "               }\n" +
            "            ],\n" +
            "            \"secondary_text\" : \"La Maison Neuve, Chauvigné, France\"\n" +
            "         },\n" +
            "         \"terms\" : [\n" +
            "            {\n" +
            "               \"offset\" : 0,\n" +
            "               \"value\" : \"Auberge La Maison Neuve\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 25,\n" +
            "               \"value\" : \"La Maison Neuve\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 42,\n" +
            "               \"value\" : \"Chauvigné\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 53,\n" +
            "               \"value\" : \"France\"\n" +
            "            }\n" +
            "         ],\n" +
            "         \"types\" : [\n" +
            "            \"campground\",\n" +
            "            \"lodging\",\n" +
            "            \"park\",\n" +
            "            \"restaurant\",\n" +
            "            \"food\",\n" +
            "            \"point_of_interest\",\n" +
            "            \"establishment\"\n" +
            "         ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"description\" : \"Auberge de jeunesse HI Rennes, Canal Saint-Martin, Rennes, France\",\n" +
            "         \"matched_substrings\" : [\n" +
            "            {\n" +
            "               \"length\" : 2,\n" +
            "               \"offset\" : 0\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJw5xN3DveDkgRstIggTqE7Y8\",\n" +
            "         \"reference\" : \"ChIJw5xN3DveDkgRstIggTqE7Y8\",\n" +
            "         \"structured_formatting\" : {\n" +
            "            \"main_text\" : \"Auberge de jeunesse HI Rennes\",\n" +
            "            \"main_text_matched_substrings\" : [\n" +
            "               {\n" +
            "                  \"length\" : 2,\n" +
            "                  \"offset\" : 0\n" +
            "               }\n" +
            "            ],\n" +
            "            \"secondary_text\" : \"Canal Saint-Martin, Rennes, France\"\n" +
            "         },\n" +
            "         \"terms\" : [\n" +
            "            {\n" +
            "               \"offset\" : 0,\n" +
            "               \"value\" : \"Auberge de jeunesse HI Rennes\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 31,\n" +
            "               \"value\" : \"Canal Saint-Martin\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 51,\n" +
            "               \"value\" : \"Rennes\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 59,\n" +
            "               \"value\" : \"France\"\n" +
            "            }\n" +
            "         ],\n" +
            "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"description\" : \"Au Comptoir Vénitien, Rue Maurice Fabre, Rennes, France\",\n" +
            "         \"matched_substrings\" : [\n" +
            "            {\n" +
            "               \"length\" : 2,\n" +
            "               \"offset\" : 0\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJtYryVo3gDkgRKmjo9jLR2pM\",\n" +
            "         \"reference\" : \"ChIJtYryVo3gDkgRKmjo9jLR2pM\",\n" +
            "         \"structured_formatting\" : {\n" +
            "            \"main_text\" : \"Au Comptoir Vénitien\",\n" +
            "            \"main_text_matched_substrings\" : [\n" +
            "               {\n" +
            "                  \"length\" : 2,\n" +
            "                  \"offset\" : 0\n" +
            "               }\n" +
            "            ],\n" +
            "            \"secondary_text\" : \"Rue Maurice Fabre, Rennes, France\"\n" +
            "         },\n" +
            "         \"terms\" : [\n" +
            "            {\n" +
            "               \"offset\" : 0,\n" +
            "               \"value\" : \"Au Comptoir Vénitien\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 22,\n" +
            "               \"value\" : \"Rue Maurice Fabre\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 41,\n" +
            "               \"value\" : \"Rennes\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"offset\" : 49,\n" +
            "               \"value\" : \"France\"\n" +
            "            }\n" +
            "         ],\n" +
            "         \"types\" : [ \"cafe\", \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ]\n" +
            "      }\n" +
            "   ],\n" +
            "   \"status\" : \"OK\"\n" +
            "}\n";
    @Test
    public void getMeInWorkListTest(){
        assertEquals(user1, NotificationsHelper.getMeInWorkmatesList(workmatesList, currentUserId));
        assertNotEquals(user1, NotificationsHelper.getMeInWorkmatesList(workmatesList, user2.getUid()));
        assertNotEquals(user1, NotificationsHelper.getMeInWorkmatesList(workmatesList, null));
        assertNotEquals(user1, NotificationsHelper.getMeInWorkmatesList(new ArrayList<>(), currentUserId));
    }

    @Test
    public void placeResultToRestaurantTest(){
        Restaurant restaurantFromHelper = PlaceSearchHelper.placeResultToRestaurant(urlRequestPlaceResult, spotId1);
        Restaurant restaurant = new Restaurant(spotId1, "Auberge de la Tourelle");
        restaurant.setPhone("02 99 39 60 06");
        restaurant.setWebsite("https://aubergelatourelle.wordpress.com/");
        restaurant.setPhoto_reference("Aap_uECPiZqVG3c-XK4O8-mbCj9r8G_5-WPsO17AopM1jlFJrJaCoB-hkslC7I619vWDmi7fcnIGKU87C7IETXdDFlZ7g0O3s0XDlCf5OVZUc33w_nJhrhfDiZaczQ_A78awhNhAWOY0cCmgZmO64UfJlfSlpNZoC2xuv9T9KFy0SkGp4CDC");
        restaurant.setVicinity("1 Place de la Mairie, Sens-de-Bretagne");
        restaurant.setRating(3);
        restaurant.setLat(48.33261719999999);
        restaurant.setLng(-1.5354094);
        restaurant.setOpen_now(true);
        assertEquals(restaurantFromHelper.getId(), restaurant.getId());
        assertEquals(restaurantFromHelper.getName(), restaurant.getName());
        assertEquals(restaurantFromHelper.getPhone(), restaurant.getPhone());
        assertEquals(restaurantFromHelper.getWebsite(), restaurant.getWebsite());
        assertEquals(restaurantFromHelper.getPhoto_reference(), restaurant.getPhoto_reference());
        assertEquals(restaurantFromHelper.getVicinity(), restaurant.getVicinity());
        assertEquals(restaurantFromHelper.getRating(), restaurant.getRating());
        assertEquals(restaurantFromHelper.getLat(), restaurant.getLat(), 0.0);
        assertEquals(restaurantFromHelper.getLng(), restaurant.getLng(), 0.0);
        assertEquals(restaurantFromHelper.getOpen_now(), restaurant.getOpen_now());
    }

    @Test
    public void placeResultToPredictionsTest(){
        ArrayList<Restaurant> restaurantListFromHelper = PlaceSearchHelper.placeResultToPredictions(requestPredictionsResult);
        Restaurant restaurant1 = new Restaurant(spotId1, "Auberge de la Tourelle, Place de la Mairie, Sens-de-Bretagne, France");
        Restaurant restaurant2 = new Restaurant(spotId2, "Au Marché des Lices, Place du Bas des Lices, Rennes, France");
        Restaurant restaurant3 = new Restaurant(spotId3, "Auberge La Maison Neuve, La Maison Neuve, Chauvigné, France");

        assertEquals(restaurantListFromHelper.get(0).getId(), restaurant1.getId());
        assertEquals(restaurantListFromHelper.get(0).getName(), restaurant1.getName());

        assertEquals(restaurantListFromHelper.get(1).getId(), restaurant2.getId());
        assertEquals(restaurantListFromHelper.get(1).getName(), restaurant2.getName());

        assertEquals(restaurantListFromHelper.get(2).getId(), restaurant3.getId());
        assertEquals(restaurantListFromHelper.get(2).getName(), restaurant3.getName());
    }
}
