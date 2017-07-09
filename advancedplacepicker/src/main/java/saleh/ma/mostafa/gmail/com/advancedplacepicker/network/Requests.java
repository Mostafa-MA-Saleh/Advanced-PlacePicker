package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Mostafa on 07/08/2017.
 */

public interface Requests {

    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbyPlacesResponse> getNearbyPlaces(@Query("location") String location,
                                               @Query("radius") int radius,
                                               @Query("key") String key);

}
