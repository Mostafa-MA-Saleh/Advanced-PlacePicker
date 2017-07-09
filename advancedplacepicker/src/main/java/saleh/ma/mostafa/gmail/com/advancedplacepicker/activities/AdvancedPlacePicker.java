package saleh.ma.mostafa.gmail.com.advancedplacepicker.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.List;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.dialogs.SelectedLocationDialog;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.models.Address;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.network.NetworkManager;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.network.Result;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.AddressResolver;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.Constants;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.LocationManager;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.NearbyPlacesAdapter;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.OnFinishedListener;


public class AdvancedPlacePicker extends AppCompatActivity implements OnMapReadyCallback, SelectedLocationDialog.OnPlaceSelectedListener, GoogleMap.OnMapLoadedCallback {

    private TextView tvSearch;
    private ProgressBar progressBar;
    private RecyclerView recNearbyPlaces;
    private FloatingActionButton btnMyLocation;
    private CardView cardSearch;
    private TextView tvSelectLocation;

    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private NetworkManager mNetworkManager;
    private Marker mMarker;
    private BottomSheetBehavior bottomSheetBehavior;
    private NearbyPlacesAdapter adapter;
    private Handler populatePlacesHandler;
    private Runnable populatePlacesRunnable;

    public static Intent buildIntent(Context context) {
        return new Intent(context, AdvancedPlacePicker.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_place_picker);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewsById();
        setOnClickListeners();
        setResult(RESULT_CANCELED);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;
        supportmapfragment.getMapAsync(this);
        mLocationManager = new LocationManager(this);
        mNetworkManager = new NetworkManager();
        setupNearbyPlacesBottomSheet();
    }

    private void findViewsById() {
        recNearbyPlaces = (RecyclerView) findViewById(R.id.rec_nearby_places);
        progressBar = (ProgressBar) findViewById(R.id.progress_places);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        btnMyLocation = (FloatingActionButton) findViewById(R.id.btn_my_location);
        cardSearch = (CardView) findViewById(R.id.card_search);
        tvSelectLocation = (TextView) findViewById(R.id.tv_select_location);
    }

    private Runnable getPopulatePlacesRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                mNetworkManager.getNearbyPlaces(AdvancedPlacePicker.this, mGoogleMap.getCameraPosition().target, new OnFinishedListener<List<Result>>() {
                    @Override
                    public void onSuccess(@Nullable final List<Result> obj) {
                        adapter.addAll(obj);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(String errorMessage, int errorCode) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

    private void setupNearbyPlacesBottomSheet() {
        recNearbyPlaces.setLayoutManager(new LinearLayoutManager(this));
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    getSupportActionBar().show();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    getSupportActionBar().hide();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        populatePlacesHandler = new Handler();
        populatePlacesRunnable = getPopulatePlacesRunnable();
        adapter = new NearbyPlacesAdapter(AdvancedPlacePicker.this, null);
        adapter.setOnItemClickListener(new NearbyPlacesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result result) {
                Result.Location location = result.getGeometry().getLocation();
                LatLng coordinates = new LatLng(location.getLat(), location.getLng());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, Constants.DEFAULT_ZOOM));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                AddressResolver.getInstance().getAddress(AdvancedPlacePicker.this, coordinates, new OnFinishedListener<String>() {
                    @Override
                    public void onSuccess(@Nullable String address) {
                        tvSearch.setText(TextUtils.isEmpty(address) ? getString(R.string.search) : address);
                    }

                    @Override
                    public void onFailure(String errorMessage, int errorCode) {
                        Log.e("Error", errorMessage);
                    }
                });
            }
        });
        recNearbyPlaces.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_SEARCH:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), Constants.DEFAULT_ZOOM));
                    tvSearch.setText(place.getAddress());
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
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            startPlacesAutocomplete();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locate();
        }
    }

    private void locate() {
        mLocationManager.getLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, Constants.DEFAULT_ZOOM));
                AddressResolver.getInstance().getAddress(AdvancedPlacePicker.this, coordinates, new OnFinishedListener<String>() {
                    @Override
                    public void onSuccess(@Nullable String address) {
                        tvSearch.setText(TextUtils.isEmpty(address) ? getString(R.string.search) : address);
                    }

                    @Override
                    public void onFailure(String errorMessage, int errorCode) {
                        Log.e("Error", errorMessage);
                    }
                });
            }
        });
    }

    private void populateNearbyPlaces() {
        populatePlacesHandler.postDelayed(populatePlacesRunnable, 1000);
    }

    private void setOnClickListeners() {
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasLocationPermission()) {
                    locate();
                } else {
                    ActivityCompat.requestPermissions(AdvancedPlacePicker.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            }
        });
        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacesAutocomplete();
            }
        });
        tvSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressResolver.getInstance().getAddress(AdvancedPlacePicker.this, mMarker.getPosition(), new OnFinishedListener<String>() {
                    @Override
                    public void onSuccess(@Nullable String address) {
                        tvSearch.setText(TextUtils.isEmpty(address) ? getString(R.string.search) : address);
                    }

                    @Override
                    public void onFailure(String errorMessage, int errorCode) {
                        Log.e("Error", errorMessage);
                    }
                });
                final ProgressDialog pleaseWaitDialog = ProgressDialog.show(AdvancedPlacePicker.this, null, getString(R.string.please_wait));
                pleaseWaitDialog.setCancelable(false);
                mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        new SelectedLocationDialog(AdvancedPlacePicker.this, mMarker.getPosition(), bitmap)
                                .setOnPlaceSelectedListener(AdvancedPlacePicker.this).show();
                        pleaseWaitDialog.dismiss();
                    }
                });
            }
        });
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(AdvancedPlacePicker.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startPlacesAutocomplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(AdvancedPlacePicker.this);
            startActivityForResult(intent, Constants.REQUEST_SEARCH);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            Log.e("Error", "GooglePlayServicesNotAvailableException");
        }
    }

    @Override
    public void onPlaceSelected(Address address) {
        Intent finishIntent = new Intent();
        finishIntent.putExtra("Address", address);
        setResult(RESULT_OK, finishIntent);
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));
                AddressResolver.getInstance().getAddress(AdvancedPlacePicker.this, pointOfInterest.latLng, new OnFinishedListener<String>() {
                    @Override
                    public void onSuccess(@Nullable String obj) {
                        tvSearch.setText(obj);
                    }

                    @Override
                    public void onFailure(String errorMessage, int errorCode) {
                        Log.e("Error", errorMessage);
                    }
                });
            }
        });
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                populatePlacesHandler.removeCallbacks(populatePlacesRunnable);
            }
        });
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMarker.setPosition(mGoogleMap.getCameraPosition().target);
            }
        });
        googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                populateNearbyPlaces();
            }
        });
        if(hasLocationPermission()){
            locate();
        }
    }
}
