package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.OnFinishedListener;

public class NetworkManager {

    private static NetworkManager mInstance = new NetworkManager();

    private Retrofit retrofit;
    private Requests requests;

    private NetworkManager() {
        requests = getClient().create(Requests.class);
    }

    public static NetworkManager getInstance() {
        return mInstance;
    }

    private Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
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
                context.getString(R.string.picker_api_key)).enqueue(new Callback<NearbyPlacesResponse>() {
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

    public void getAddress(final Context context, LatLng coordinates, final OnFinishedListener<String> onFinishedListener) {
        requests.getAddress(coordinates.latitude + "," + coordinates.longitude, Locale.getDefault().getLanguage()).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddressResponse> call, @NonNull Response<AddressResponse> response) {
                AddressResponse addressResponse = response.body();
                if (addressResponse != null && !addressResponse.results.isEmpty()
                        && !TextUtils.isEmpty(addressResponse.results.get(0).formatted_address)) {
                    String address = addressResponse.results.get(0).formatted_address;
                    onFinishedListener.onSuccess(address);
                } else {
                    onFinishedListener.onFailure(context.getString(R.string.invalid_location), 404);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddressResponse> call, @NonNull Throwable t) {
                onFinishedListener.onFailure(t.getLocalizedMessage(), -1);
            }
        });
    }

}
