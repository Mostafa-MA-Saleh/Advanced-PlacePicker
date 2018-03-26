package saleh.ma.mostafa.gmail.com.advancedplacepicker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.models.PPAddress;

public class SelectedLocationDialog extends Dialog implements View.OnClickListener {

    private TextView coordinatesTextView;
    private TextView addressTextView;
    private ImageView mapImageView;

    private LatLng mCoordinates;
    private String mAddress;
    private Bitmap mMapBitmap;
    private OnPlaceSelectedListener mListener;

    public SelectedLocationDialog(@NonNull Context context, LatLng coordinates, String address, Bitmap mapImage) {
        super(context);
        setCanceledOnTouchOutside(false);
        mCoordinates = coordinates;
        mAddress = address;
        mMapBitmap = mapImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_selected_location);
        setRelativeSize(0.9f, 0.6f);
        findViewsById();
        setOnClickListeners();
        setupTextViews();
        mapImageView.setImageBitmap(mMapBitmap);
    }

    private void findViewsById() {
        coordinatesTextView = findViewById(R.id.coordinates_text_view);
        addressTextView = findViewById(R.id.address_text_view);
        mapImageView = findViewById(R.id.map_image_view);
    }

    private void setOnClickListeners() {
        findViewById(R.id.change_location_button).setOnClickListener(this);
        findViewById(R.id.select_button).setOnClickListener(this);
    }

    private void setupTextViews() {
        coordinatesTextView.setText(String.format(Locale.getDefault(), "%.8f, %.8f", mCoordinates.latitude, mCoordinates.longitude));
        addressTextView.setText(mAddress);
    }

    public SelectedLocationDialog setOnPlaceSelectedListener(OnPlaceSelectedListener mListener) {
        this.mListener = mListener;
        return this;
    }

    private void setRelativeSize(float width, float height) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        if (getWindow() != null)
            getWindow().setLayout((int) (displayMetrics.widthPixels * width), (int) (displayMetrics.heightPixels * height));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.select_button) {
            if (mListener != null) {
                mListener.onPlaceSelected(new PPAddress(mAddress, mCoordinates));
            }
        }
        dismiss();
    }

    public interface OnPlaceSelectedListener {
        void onPlaceSelected(PPAddress address);
    }
}
