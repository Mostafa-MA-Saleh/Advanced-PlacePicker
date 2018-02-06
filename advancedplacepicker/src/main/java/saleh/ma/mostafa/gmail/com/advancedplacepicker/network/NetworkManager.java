package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

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

    private static NetworkManager mInstance;

    private Retrofit retrofit;
    private Requests requests;

    private NetworkManager(Context context) {
        retrofit = getClient(context);
        requests = retrofit.create(Requests.class);
    }

    public static void init(Context context) {
        mInstance = new NetworkManager(context);
    }

    public static NetworkManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("You need to call init first before trying to get an instance!");
        }
        return mInstance;
    }

    private Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ConnectivityInterceptor(context))
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
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
