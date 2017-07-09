package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public void getAddress(Context context, LatLng coordinates, OnFinishedListener<String> onFinishedListener) {
        String address = addressCache.get(coordinates);
        if (address == null) {
            address = "";
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address firstAddress = addressList.get(0);
                    int i = 0;
                    while (firstAddress.getAddressLine(i) != null) {
                        address += firstAddress.getAddressLine(i++) + ", ";
                    }
                    address = address.substring(0, address.lastIndexOf(","));
                    addressCache.put(coordinates, address);
                }
            } catch (IOException e) {
                e.printStackTrace();
                onFinishedListener.onFailure(e.getLocalizedMessage(), -1);
                return;
            }
        }
        onFinishedListener.onSuccess(address);
    }
}
