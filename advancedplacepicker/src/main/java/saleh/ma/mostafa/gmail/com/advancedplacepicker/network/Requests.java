package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Requests {

    @GET("maps/api/geocode/json")
    Call<AddressResponse> getAddress(@Query("latlng") String latlng,
                                     @Query("language") String language);

}
