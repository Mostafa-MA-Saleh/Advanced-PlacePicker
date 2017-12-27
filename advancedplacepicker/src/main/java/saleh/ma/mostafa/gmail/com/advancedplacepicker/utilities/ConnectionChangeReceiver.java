package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public abstract class ConnectionChangeReceiver extends BroadcastReceiver {

    public boolean isRegistered;

    @SuppressWarnings("UnusedReturnValue")
    public Intent register(Context context) {
        try {
            return !isRegistered
                    ? context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                    : null;
        } finally {
            isRegistered = true;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean unregister(Context context) {
        return isRegistered
                && unregisterInternal(context);
    }

    private boolean unregisterInternal(Context context) {
        context.unregisterReceiver(this);
        isRegistered = false;
        return true;
    }
}