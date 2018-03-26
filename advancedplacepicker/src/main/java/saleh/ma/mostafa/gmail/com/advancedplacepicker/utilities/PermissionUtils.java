package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Mostafa on 03/26/2018.
 */

public class PermissionUtils {

    public static boolean hasPermission(@NonNull Context context,@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(@NonNull Activity activity, @NonNull String permission, int requestCode){
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }
}
