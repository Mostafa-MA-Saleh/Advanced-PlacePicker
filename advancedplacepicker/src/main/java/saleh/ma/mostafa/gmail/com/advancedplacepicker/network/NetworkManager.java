package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.OnFinishedListener;

/**
 * Created by Mostafa on 07/09/2017.
 */

public class NetworkManager {

    private Retrofit retrofit;
    private Requests requests;

    public NetworkManager() {
        requests = getClient().create(Requests.class);
    }

    public Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public void getNearbyPlaces(Context context, LatLng coordinates, final OnFinishedListener<List<Result>> onFinishedListener) {
        requests.getNearbyPlaces(coordinates.latitude + "," + coordinates.longitude, 1000,
                context.getString(R.string.google_api_key)).enqueue(new Callback<NearbyPlacesResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlacesResponse> call, @NonNull Response<NearbyPlacesResponse> response) {
                NearbyPlacesResponse placesResponse = response.body();
                if (placesResponse != null && placesResponse.getResults() != null && !placesResponse.getResults().isEmpty()) {
                    List<Result> places = placesResponse.getResults();
                    onFinishedListener.onSuccess(places);
                } else {
                    onFinishedListener.onFailure("Something went wrong!", -1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlacesResponse> call, @NonNull Throwable t) {
                onFinishedListener.onFailure(t.getLocalizedMessage(), -1);
            }
        });
    }

}
