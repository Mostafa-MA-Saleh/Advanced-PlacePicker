package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.support.annotation.Nullable;

public interface OnFinishedListener<T> {
    void onSuccess(@Nullable T obj);

    void onFailure(String errorMessage, int errorCode);
}