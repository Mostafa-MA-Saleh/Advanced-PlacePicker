package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.network.NetworkManager;

/**
 * Created by Mostafa on 07/09/2017.
 */

public class AddressResolver {

    private static AddressResolver mInstance = new AddressResolver();
    private Map<LatLng, String> addressCache;

    private AddressResolver() {
        addressCache = new HashMap<>();
    }

    public static AddressResolver getInstance() {
        return mInstance;
    }

    public void getAddress(Context context, final LatLng coordinates, final OnFinishedListener<String> onFinishedListener) {
        String address = addressCache.get(coordinates);
        if (address != null) {
            onFinishedListener.onSuccess(address);
        } else {
            NetworkManager.getInstance().getAddress(context, coordinates, new OnFinishedListener<String>() {
                @Override
                public void onSuccess(@Nullable String obj) {
                    addressCache.put(coordinates, obj);
                    onFinishedListener.onSuccess(obj);
                }

                @Override
                public void onFailure(String errorMessage, int errorCode) {
                    onFinishedListener.onFailure(errorMessage, errorCode);
                }
            });
        }
    }
}
