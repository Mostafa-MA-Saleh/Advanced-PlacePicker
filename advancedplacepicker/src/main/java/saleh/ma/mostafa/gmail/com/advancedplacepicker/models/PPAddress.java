package saleh.ma.mostafa.gmail.com.advancedplacepicker.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class PPAddress implements Parcelable {

    @Nullable
    private String address;

    @NonNull
    private LatLng coordinates = new LatLng(0, 0);

    public PPAddress() {
    }

    public PPAddress(@Nullable String address, @NonNull LatLng coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    @NonNull
    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(@NonNull LatLng coordinates) {
        this.coordinates = coordinates;
    }


    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeParcelable(this.coordinates, flags);
    }

    @Override
    public String toString() {
        return address + "\n" + coordinates.latitude + ", " + coordinates.longitude;
    }

    private PPAddress(Parcel in) {
        this.address = in.readString();
        this.coordinates = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<PPAddress> CREATOR = new Creator<PPAddress>() {
        @Override
        public PPAddress createFromParcel(Parcel source) {
            return new PPAddress(source);
        }

        @Override
        public PPAddress[] newArray(int size) {
            return new PPAddress[size];
        }
    };
}
