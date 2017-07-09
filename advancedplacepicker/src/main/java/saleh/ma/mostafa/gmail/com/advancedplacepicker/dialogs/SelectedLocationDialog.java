package saleh.ma.mostafa.gmail.com.advancedplacepicker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.models.Address;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.AddressResolver;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.OnFinishedListener;

/**
 * Created by Mostafa on 07/08/2017.
 */

public class SelectedLocationDialog extends Dialog {

    private TextView tvCoordinates;
    private TextView tvAddress;
    private TextView tvChangeLocation;
    private TextView tvSelect;
    private ImageView imgMap;

    private LatLng mCoordinates;
    private Bitmap bitmapMap;
    private OnPlaceSelectedListener mListener;

    public SelectedLocationDialog(@NonNull Context context, LatLng coordinates, Bitmap mapImage) {
        super(context);
        setCanceledOnTouchOutside(false);
        mCoordinates = coordinates;
        bitmapMap = mapImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selected_location);
        setRelativeSize(0.9f, 0.6f);
        findViewsById();
        setOnClickListeners();
        setupTextViews();
        imgMap.setImageBitmap(bitmapMap);
    }

    private void findViewsById() {
        tvCoordinates = (TextView) findViewById(R.id.tv_coordinates);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvChangeLocation = (TextView) findViewById(R.id.tv_change_location);
        tvSelect = (TextView) findViewById(R.id.tv_select);
        imgMap = (ImageView) findViewById(R.id.img_map);
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
                    mListener.onPlaceSelected(new Address(tvAddress.getText().toString(), mCoordinates));
                }
                dismiss();
            }
        });
    }

    private void setupTextViews() {
        tvCoordinates.setText(String.format(Locale.getDefault(), "%.8f, %.8f", mCoordinates.latitude, mCoordinates.longitude));
        AddressResolver.getInstance().getAddress(getContext(), mCoordinates, new OnFinishedListener<String>() {
            @Override
            public void onSuccess(@Nullable String address) {
                tvAddress.setText(address);
            }

            @Override
            public void onFailure(String errorMessage, int errorCode) {
                Log.e("Error", errorMessage);
            }
        });
    }

    public SelectedLocationDialog setOnPlaceSelectedListener(OnPlaceSelectedListener mListener) {
        this.mListener = mListener;
        return this;
    }

    private void setRelativeSize(float width, float height) {
        Point screenSize = new Point();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(screenSize);
        if (getWindow() != null)
            getWindow().setLayout((int) (screenSize.x * width), (int) (screenSize.y * height));
    }

    public interface OnPlaceSelectedListener {
        void onPlaceSelected(Address address);
    }
}
