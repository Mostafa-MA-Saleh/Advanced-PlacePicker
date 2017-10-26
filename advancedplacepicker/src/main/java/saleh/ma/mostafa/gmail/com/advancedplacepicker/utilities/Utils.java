package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Mostafa on 07/08/2017.
 */

public class Utils {

    public static void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().dontAnimate())
                .into(imageView);
    }

    public static Dialog showProgressDialog(@NonNull Context context, boolean showImmediately) {
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(new ProgressBar(context));
        //noinspection ConstantConditions
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        if (showImmediately) {
            progressDialog.show();
        }
        return progressDialog;
    }

}
