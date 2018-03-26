package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Mostafa on 07/08/2017.
 */

public class LocationManager extends ContextWrapper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationListener locationListener;

    public LocationManager(Context context) {
        super(context);
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @SuppressWarnings("MissingPermission")
    public void getLocation(LocationListener locationListener) {
        if (mGoogleApiClient.isConnected()) {
            LocationRequest request = new LocationRequest().setNumUpdates(1);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, locationListener);
        } else {
            this.locationListener = locationListener;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (locationListener != null) {
            getLocation(locationListener);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
