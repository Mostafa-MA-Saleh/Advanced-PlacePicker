package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManager {

    private static ConnectivityManager cm;

    public static boolean isConnected() {
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } else {
            throw new IllegalStateException("You need to call init first!");
        }
    }

    public static void init(Context context) {
        if (cm == null) {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }
}
