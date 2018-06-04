package saleh.ma.mostafa.gmail.com.advancedplacepicker.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.dialogs.SelectedLocationDialog;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.models.PPAddress;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.network.NetworkManager;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.AddressResolver;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.ConnectionChangeReceiver;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.ConnectionManager;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.Constants;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.LocationManager;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.OnFinishedListener;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.PermissionUtils;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.Utils;


public class AdvancedPlacePicker extends AppCompatActivity
        implements SelectedLocationDialog.OnPlaceSelectedListener, View.OnClickListener, LocationListener {

    public static final String ADDRESS = "PPAddress";
    public static final int REQUEST_PLACE_PICKER = 193;

    private TextView searchTextView;

    private GoogleMap mGoogleMap;
    private LatLng currentLocation;
    private SupportMapFragment mSupportMapFragment;
    private LocationManager mLocationManager;

    private ConnectionChangeReceiver mConnectionChangeReceiver = new ConnectionChangeReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectionManager.isConnected()) {
                if (mGoogleMap == null) {
                    mSupportMapFragment.getMapAsync(mOnMapReadyCallback);
                }
                unregister(getApplicationContext());
            }
        }
    };

    /**
     * @deprecated you should use {@link #start(Activity)} or {@link #start(Fragment)} instead.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static Intent buildIntent(Context context) {
        return new Intent(context, AdvancedPlacePicker.class);
    }

    /**
     * Results will be delivered in onActivityResult with the requestCode {@link #REQUEST_PLACE_PICKER}
     */
    @SuppressWarnings("deprecation")
    public static void start(Activity activity) {
        Intent launchIntent = buildIntent(activity);
        activity.startActivityForResult(launchIntent, REQUEST_PLACE_PICKER);
    }

    /**
     * Results will be delivered in onActivityResult with the requestCode {@link #REQUEST_PLACE_PICKER}
     */
    @SuppressWarnings("deprecation")
    public static void start(Fragment fragment) {
        Intent launchIntent = buildIntent(fragment.getContext());
        fragment.startActivityForResult(launchIntent, REQUEST_PLACE_PICKER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_place_picker);
        NetworkManager.init(getApplicationContext());
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewsById();
        setOnClickListeners();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mSupportMapFragment = (SupportMapFragment) fragment;
        mSupportMapFragment.getMapAsync(mOnMapReadyCallback);
        mLocationManager = new LocationManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnectionChangeReceiver.register(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnectionChangeReceiver.unregister(getApplicationContext());
    }

    private void findViewsById() {
        searchTextView = findViewById(R.id.search_text_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_SEARCH:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    if (mGoogleMap != null) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), Constants.DEFAULT_ZOOM));
                    }
                    searchTextView.setText(place.getAddress());
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_search) {
            startPlacesAutocomplete();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locate();
        }
    }

    private void locate() {
        mLocationManager.getLocation(this);
    }

    private void setOnClickListeners() {
        findViewById(R.id.my_location_button).setOnClickListener(this);
        findViewById(R.id.search_card_view).setOnClickListener(this);
        findViewById(R.id.select_location_text_view).setOnClickListener(this);
    }

    private boolean hasLocationPermission() {
        return PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void startPlacesAutocomplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, Constants.REQUEST_SEARCH);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            Log.e("Error", "GooglePlayServicesNotAvailableException");
        }
    }

    @Override
    public void onPlaceSelected(PPAddress address) {
        Intent finishIntent = new Intent();
        finishIntent.putExtra(ADDRESS, address);
        setResult(RESULT_OK, finishIntent);
        finish();
    }

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
                @Override
                public void onPoiClick(PointOfInterest pointOfInterest) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));
                    AddressResolver.getInstance().getAddress(AdvancedPlacePicker.this, pointOfInterest.latLng, new OnFinishedListener<String>() {
                        @Override
                        public void onSuccess(@Nullable String obj) {
                            searchTextView.setText(obj);
                        }

                        @Override
                        public void onFailure(String errorMessage, int errorCode) {
                            Log.e("Error", errorMessage);
                        }
                    });
                }
            });
            googleMap.setOnMapLoadedCallback(mOnMapLoadedCallback);
        }
    };

    private GoogleMap.OnMapLoadedCallback mOnMapLoadedCallback = new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            if (hasLocationPermission()) {
                locate();
            }
            findViewById(R.id.select_location_text_view).setEnabled(true);
            if (currentLocation != null) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, Constants.DEFAULT_ZOOM));
                currentLocation = null;
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = Locale.getDefault();
        Resources res = newBase.getResources();
        Configuration configuration = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            newBase = newBase.createConfigurationContext(configuration);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
            Locale.setDefault(newLocale);
            newBase = newBase.createConfigurationContext(configuration);
        }
        super.attachBaseContext(newBase);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.my_location_button) {
            onMyLocationClick();
        } else if (i == R.id.search_card_view) {
            startPlacesAutocomplete();
        } else if (i == R.id.select_location_text_view) {
            onSelectLocationClick();
        }
    }

    private void onSelectLocationClick() {
        if (mGoogleMap != null) {
            final Dialog progressDialog = Utils.showProgressDialog(AdvancedPlacePicker.this, true);
            mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(final Bitmap bitmap) {
                    AddressResolver.getInstance().getAddress(AdvancedPlacePicker.this,
                            mGoogleMap.getCameraPosition().target, new OnFinishedListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String address) {
                                    searchTextView.setText(address);
                                    new SelectedLocationDialog(AdvancedPlacePicker.this, mGoogleMap.getCameraPosition().target, address, bitmap)
                                            .setOnPlaceSelectedListener(AdvancedPlacePicker.this).show();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onFailure(String errorMessage, int errorCode) {
                                    progressDialog.dismiss();
                                    new AlertDialog.Builder(AdvancedPlacePicker.this)
                                            .setMessage(errorMessage)
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show();
                                }
                            });
                }
            });
        }
    }

    private void onMyLocationClick() {
        if (hasLocationPermission()) {
            locate();
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, Constants.DEFAULT_ZOOM));
            AddressResolver.getInstance().getAddress(this, coordinates, new OnFinishedListener<String>() {
                @Override
                public void onSuccess(@Nullable String address) {
                    searchTextView.setText(TextUtils.isEmpty(address) ? getString(R.string.search) : address);
                }

                @Override
                public void onFailure(String errorMessage, int errorCode) {
                    Log.e("Error", errorMessage);
                }
            });
        } else {
            currentLocation = coordinates;
        }
    }
}
