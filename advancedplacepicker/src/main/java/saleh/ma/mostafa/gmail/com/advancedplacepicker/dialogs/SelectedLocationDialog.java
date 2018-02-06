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

public class SelectedLocationDialog extends Dialog {

    private TextView tvCoordinates;
    private TextView tvAddress;
    private TextView tvChangeLocation;
    private TextView tvSelect;
    private ImageView imgMap;

    private LatLng mCoordinates;
    private String mAddress;
    private Bitmap bitmapMap;
    private OnPlaceSelectedListener mListener;

    public SelectedLocationDialog(@NonNull Context context, LatLng coordinates, String address, Bitmap mapImage) {
        super(context);
        setCanceledOnTouchOutside(false);
        mCoordinates = coordinates;
        mAddress = address;
        bitmapMap = mapImage;
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
        imgMap.setImageBitmap(bitmapMap);
    }

    private void findViewsById() {
        tvCoordinates = findViewById(R.id.tv_coordinates);
        tvAddress = findViewById(R.id.tv_address);
        tvChangeLocation = findViewById(R.id.tv_change_location);
        tvSelect = findViewById(R.id.tv_select);
        imgMap = findViewById(R.id.img_map);
    }

    private void setOnClickListeners() {
        tvChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPlaceSelected(new PPAddress(mAddress, mCoordinates));
                }
                dismiss();
            }
        });
    }

    private void setupTextViews() {
        tvCoordinates.setText(String.format(Locale.getDefault(), "%.8f, %.8f", mCoordinates.latitude, mCoordinates.longitude));
        tvAddress.setText(mAddress);
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

    public interface OnPlaceSelectedListener {
        void onPlaceSelected(PPAddress address);
    }
}
