package com.example.go4lunch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.tools.PlaceSearchHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class MapFragmentTest {
    String urlRequest = "{\n" +
            "            \"html_attributions\" : [],\n" +
            "            \"results\" : [\n" +
            "    {\n" +
            "        \"business_status\" : \"OPERATIONAL\",\n" +
            "            \"geometry\" : {\n" +
            "        \"location\" : {\n" +
            "            \"lat\" : 48.33311699999999,\n" +
            "                    \"lng\" : -1.5359901\n" +
            "        },\n" +
            "        \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "                \"lat\" : 48.33448858029149,\n" +
            "                        \"lng\" : -1.534777119708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "                \"lat\" : 48.33179061970849,\n" +
            "                        \"lng\" : -1.537475080291502\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "        \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png\",\n" +
            "            \"icon_background_color\" : \"#FF9E67\",\n" +
            "            \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/restaurant_pinlet\",\n" +
            "            \"name\" : \"No LIMIT kébab Pizzas\",\n" +
            "            \"opening_hours\" : {\n" +
            "        \"open_now\" : false\n" +
            "    },\n" +
            "        \"photos\" : [\n" +
            "        {\n" +
            "            \"height\" : 3472,\n" +
            "                \"html_attributions\" : [\n" +
            "            \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110664791566947225951\\\"\\u003eNo LIMIT kébab Pizzas\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "            \"photo_reference\" : \"Aap_uEC9TQjEleCuch6_zPXonjjzIqaiRexQsK2zDhmo__3EMv47ZR9WCwiRAih762weQ5Cusve5b57NX8xCJ8OrfXUNkzWhaBi7VqsfER_cpSzL0Cm7mz7wY1qIo9871mfUQ2iHnKm_9Y9-GLEBbddD9pcQ1bJeUEe5pfaqjBWADfXa17n0\",\n" +
            "                \"width\" : 3472\n" +
            "        }\n" +
            "         ],\n" +
            "        \"place_id\" : \"ChIJF_4VemjHDkgRI0lv0xtApcM\",\n" +
            "            \"plus_code\" : {\n" +
            "        \"compound_code\" : \"8FM7+6J Sens-de-Bretagne, France\",\n" +
            "                \"global_code\" : \"8CWW8FM7+6J\"\n" +
            "    },\n" +
            "        \"rating\" : 4.6,\n" +
            "            \"reference\" : \"ChIJF_4VemjHDkgRI0lv0xtApcM\",\n" +
            "            \"scope\" : \"GOOGLE\",\n" +
            "            \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "        \"user_ratings_total\" : 63,\n" +
            "            \"vicinity\" : \"25 Place de la Mairie, Sens-de-Bretagne\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"business_status\" : \"OPERATIONAL\",\n" +
            "            \"geometry\" : {\n" +
            "        \"location\" : {\n" +
            "            \"lat\" : 48.33261719999999,\n" +
            "                    \"lng\" : -1.5354094\n" +
            "        },\n" +
            "        \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "                \"lat\" : 48.3339740302915,\n" +
            "                        \"lng\" : -1.534227669708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "                \"lat\" : 48.3312760697085,\n" +
            "                        \"lng\" : -1.536925630291502\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "        \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png\",\n" +
            "            \"icon_background_color\" : \"#FF9E67\",\n" +
            "            \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/restaurant_pinlet\",\n" +
            "            \"name\" : \"Auberge de la Tourelle\",\n" +
            "            \"opening_hours\" : {\n" +
            "        \"open_now\" : false\n" +
            "    },\n" +
            "        \"photos\" : [\n" +
            "        {\n" +
            "            \"height\" : 2448,\n" +
            "                \"html_attributions\" : [\n" +
            "            \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101557958287251296998\\\"\\u003eAuberge de la Tourelle\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "            \"photo_reference\" : \"Aap_uEBNedxNwrhKvx6mOqvShj674wKNo-xH5rtlThvpVfM-Z1ocvz0iZJZcislby9fSuFIMv9LbGFzcWUdP2D8skqKk_wTWrMJvCd769QjH3I1p7h5-qDeitRZzFlLv0hg7s6oGKxAeoTAgdCB5bG9nM995ugGm_KcspDdY9v1LNssNslEL\",\n" +
            "                \"width\" : 3264\n" +
            "        }\n" +
            "         ],\n" +
            "        \"place_id\" : \"ChIJBdhDoCDGDkgRT4dY16bCaC0\",\n" +
            "            \"plus_code\" : {\n" +
            "        \"compound_code\" : \"8FM7+2R Sens-de-Bretagne, France\",\n" +
            "                \"global_code\" : \"8CWW8FM7+2R\"\n" +
            "    },\n" +
            "        \"rating\" : 4.4,\n" +
            "            \"reference\" : \"ChIJBdhDoCDGDkgRT4dY16bCaC0\",\n" +
            "            \"scope\" : \"GOOGLE\",\n" +
            "            \"types\" : [\n" +
            "        \"lodging\",\n" +
            "                \"bar\",\n" +
            "                \"restaurant\",\n" +
            "                \"food\",\n" +
            "                \"point_of_interest\",\n" +
            "                \"establishment\"\n" +
            "         ],\n" +
            "        \"user_ratings_total\" : 154,\n" +
            "            \"vicinity\" : \"1 Place de la Mairie, Sens-de-Bretagne\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"business_status\" : \"OPERATIONAL\",\n" +
            "            \"geometry\" : {\n" +
            "        \"location\" : {\n" +
            "            \"lat\" : 48.3334142,\n" +
            "                    \"lng\" : -1.5416506\n" +
            "        },\n" +
            "        \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "                \"lat\" : 48.3346951302915,\n" +
            "                        \"lng\" : -1.540317369708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "                \"lat\" : 48.3319971697085,\n" +
            "                        \"lng\" : -1.543015330291502\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "        \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png\",\n" +
            "            \"icon_background_color\" : \"#FF9E67\",\n" +
            "            \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/restaurant_pinlet\",\n" +
            "            \"name\" : \"Sens\",\n" +
            "            \"opening_hours\" : {\n" +
            "        \"open_now\" : true\n" +
            "    },\n" +
            "        \"place_id\" : \"ChIJiaui-SPGDkgRffLQeBOzmZk\",\n" +
            "            \"plus_code\" : {\n" +
            "        \"compound_code\" : \"8FM5+98 Sens-de-Bretagne, France\",\n" +
            "                \"global_code\" : \"8CWW8FM5+98\"\n" +
            "    },\n" +
            "        \"rating\" : 4.1,\n" +
            "            \"reference\" : \"ChIJiaui-SPGDkgRffLQeBOzmZk\",\n" +
            "            \"scope\" : \"GOOGLE\",\n" +
            "            \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "        \"user_ratings_total\" : 8,\n" +
            "            \"vicinity\" : \"45 La Petite Barre, Sens-de-Bretagne\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"business_status\" : \"OPERATIONAL\",\n" +
            "            \"geometry\" : {\n" +
            "        \"location\" : {\n" +
            "            \"lat\" : 48.3191921,\n" +
            "                    \"lng\" : -1.5330278\n" +
            "        },\n" +
            "        \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "                \"lat\" : 48.3205556802915,\n" +
            "                        \"lng\" : -1.531701919708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "                \"lat\" : 48.3178577197085,\n" +
            "                        \"lng\" : -1.534399880291502\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "        \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png\",\n" +
            "            \"icon_background_color\" : \"#FF9E67\",\n" +
            "            \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/restaurant_pinlet\",\n" +
            "            \"name\" : \"PSITT'PIZZA\",\n" +
            "            \"opening_hours\" : {\n" +
            "        \"open_now\" : false\n" +
            "    },\n" +
            "        \"place_id\" : \"ChIJnUxb56fIDkgR4pRgEBRVuC0\",\n" +
            "            \"plus_code\" : {\n" +
            "        \"compound_code\" : \"8F98+MQ Sens-de-Bretagne, France\",\n" +
            "                \"global_code\" : \"8CWW8F98+MQ\"\n" +
            "    },\n" +
            "        \"rating\" : 3.9,\n" +
            "            \"reference\" : \"ChIJnUxb56fIDkgR4pRgEBRVuC0\",\n" +
            "            \"scope\" : \"GOOGLE\",\n" +
            "            \"types\" : [\n" +
            "        \"meal_takeaway\",\n" +
            "                \"restaurant\",\n" +
            "                \"food\",\n" +
            "                \"point_of_interest\",\n" +
            "                \"establishment\"\n" +
            "         ],\n" +
            "        \"user_ratings_total\" : 7,\n" +
            "            \"vicinity\" : \"13 Sautoger, Sens-de-Bretagne\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"business_status\" : \"OPERATIONAL\",\n" +
            "            \"geometry\" : {\n" +
            "        \"location\" : {\n" +
            "            \"lat\" : 48.315588,\n" +
            "                    \"lng\" : -1.5360037\n" +
            "        },\n" +
            "        \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "                \"lat\" : 48.3170327802915,\n" +
            "                        \"lng\" : -1.534415719708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "                \"lat\" : 48.31433481970851,\n" +
            "                        \"lng\" : -1.537113680291502\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "        \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/restaurant-71.png\",\n" +
            "            \"icon_background_color\" : \"#FF9E67\",\n" +
            "            \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/restaurant_pinlet\",\n" +
            "            \"name\" : \"Les Ajoncs\",\n" +
            "            \"opening_hours\" : {\n" +
            "        \"open_now\" : false\n" +
            "    },\n" +
            "        \"photos\" : [\n" +
            "        {\n" +
            "            \"height\" : 3024,\n" +
            "                \"html_attributions\" : [\n" +
            "            \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110786354907916591722\\\"\\u003eketty Masson\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "            \"photo_reference\" : \"Aap_uED8de9ZZmbyTNlg3pccCY_VwzYe6cpv3bouuNXJ4r7PG_lHFISNFbhPqzorXXwTjjb-QSkSok9VPykHp27psgNZ61AgweVZVDAR_yqivDBKbjC47CQAKZGGpdwE6HGII3Lq1FJPt3y_sjhPSzEBHyj0TQvHiPnUTErKe1LKHbWP7Fit\",\n" +
            "                \"width\" : 4032\n" +
            "        }\n" +
            "         ],\n" +
            "        \"place_id\" : \"ChIJaeucfALGDkgRLz8FW0pIDBo\",\n" +
            "            \"plus_code\" : {\n" +
            "        \"compound_code\" : \"8F87+6H Sens-de-Bretagne, France\",\n" +
            "                \"global_code\" : \"8CWW8F87+6H\"\n" +
            "    },\n" +
            "        \"price_level\" : 2,\n" +
            "            \"rating\" : 3.8,\n" +
            "            \"reference\" : \"ChIJaeucfALGDkgRLz8FW0pIDBo\",\n" +
            "            \"scope\" : \"GOOGLE\",\n" +
            "            \"types\" : [\n" +
            "        \"meal_takeaway\",\n" +
            "                \"restaurant\",\n" +
            "                \"food\",\n" +
            "                \"point_of_interest\",\n" +
            "                \"store\",\n" +
            "                \"establishment\"\n" +
            "         ],\n" +
            "        \"user_ratings_total\" : 342,\n" +
            "            \"vicinity\" : \"Les Saudrais, Sens-de-Bretagne\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"business_status\" : \"OPERATIONAL\",\n" +
            "            \"geometry\" : {\n" +
            "        \"location\" : {\n" +
            "            \"lat\" : 48.38066990000001,\n" +
            "                    \"lng\" : -1.4930039\n" +
            "        },\n" +
            "        \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "                \"lat\" : 48.3820325802915,\n" +
            "                        \"lng\" : -1.491767769708498\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "                \"lat\" : 48.3793346197085,\n" +
            "                        \"lng\" : -1.494465730291502\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "        \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/gas_station-71.png\",\n" +
            "            \"icon_background_color\" : \"#909CE1\",\n" +
            "            \"icon_mask_base_uri\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/v2/gas_pinlet\",\n" +
            "            \"name\" : \"TotalEnergies\",\n" +
            "            \"opening_hours\" : {\n" +
            "        \"open_now\" : true\n" +
            "    },\n" +
            "        \"photos\" : [\n" +
            "        {\n" +
            "            \"height\" : 2240,\n" +
            "                \"html_attributions\" : [\n" +
            "            \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101008090200999953728\\\"\\u003eJacky Pigeard\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "            \"photo_reference\" : \"Aap_uECmTc4IPJU1ebl5X6rTFUddLH0PR7KCJHd-zR4gn94tOpFmcR_l-uhcHmxU-KCJeAwQavjAk-kMLs8VKAphZrvEBL5Er1X6PsxNGfoBzPTAwRmVRwrDq6vatrvfN8sXwYXikuXw-p4UcvqBu1VUbaOlSrzb8al2EBMlpaPtwM7QD-w\",\n" +
            "                \"width\" : 3968\n" +
            "        }\n" +
            "         ],\n" +
            "        \"place_id\" : \"ChIJJfUb7NLJDkgRofQMjm-SCN8\",\n" +
            "            \"plus_code\" : {\n" +
            "        \"compound_code\" : \"9GJ4+7Q Romazy, France\",\n" +
            "                \"global_code\" : \"8CWW9GJ4+7Q\"\n" +
            "    },\n" +
            "        \"rating\" : 3.2,\n" +
            "            \"reference\" : \"ChIJJfUb7NLJDkgRofQMjm-SCN8\",\n" +
            "            \"scope\" : \"GOOGLE\",\n" +
            "            \"types\" : [\n" +
            "        \"gas_station\",\n" +
            "                \"car_wash\",\n" +
            "                \"convenience_store\",\n" +
            "                \"cafe\",\n" +
            "                \"restaurant\",\n" +
            "                \"food\",\n" +
            "                \"point_of_interest\",\n" +
            "                \"store\",\n" +
            "                \"establishment\"\n" +
            "         ],\n" +
            "        \"user_ratings_total\" : 17,\n" +
            "            \"vicinity\" : \"Route du Mont-Saint Michel, D175, Romazy\"\n" +
            "    }\n" +
            "   ],\n" +
            "           \"status\" : \"OK\"\n" +
            "}";

    ArrayList<NearbySearchResult> resultArrayList;

    @Before
    public void init(){
        resultArrayList = PlaceSearchHelper.getNearBySearchResult(urlRequest);
    }

    @Test
    public void getNearBySearchResultSizeTest(){
        assertEquals(resultArrayList.size(), 6);
    }

    @Test
    public void getNearBySearchResultContentTest(){
        assertEquals(resultArrayList.get(0).getRating(), 3);
        assertEquals(resultArrayList.get(0).getLat(), 48.33311699999999, 0);
        assertEquals(resultArrayList.get(0).getLng(), -1.5359901, 0);
        assertEquals(resultArrayList.get(0).getName(), "No LIMIT kébab Pizzas");
        assertFalse(resultArrayList.get(0).getOpen_now());
        assertEquals(resultArrayList.get(0).getPhoto_reference(), "Aap_uEC9TQjEleCuch6_zPXonjjzIqaiRexQsK2zDhmo__3EMv47ZR9WCwiRAih762weQ5Cusve5b57NX8xCJ8OrfXUNkzWhaBi7VqsfER_cpSzL0Cm7mz7wY1qIo9871mfUQ2iHnKm_9Y9-GLEBbddD9pcQ1bJeUEe5pfaqjBWADfXa17n0");
        assertEquals(resultArrayList.get(0).getPlace_id(), "ChIJF_4VemjHDkgRI0lv0xtApcM");
        assertEquals(resultArrayList.get(0).getVicinity(), "25 Place de la Mairie, Sens-de-Bretagne");
    }
}
