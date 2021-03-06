package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.network.NetworkManager;

public class AddressResolver {

    private static AddressResolver mInstance = new AddressResolver();

    private AddressResolver() {
    }

    public static AddressResolver getInstance() {
        return mInstance;
    }

    public void getAddress(Context context, final LatLng coordinates, final OnFinishedListener<String> onFinishedListener) {
        NetworkManager.getInstance().getAddress(context, coordinates, new OnFinishedListener<String>() {
            @Override
            public void onSuccess(@Nullable String obj) {
                onFinishedListener.onSuccess(obj);
            }

            @Override
            public void onFailure(String errorMessage, int errorCode) {
                if (errorCode == 404) {
                    onSuccess(null);
                } else {
                    onFinishedListener.onFailure(errorMessage, errorCode);
                }
            }
        });
    }
}
