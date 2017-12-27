package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities.ConnectionManager;

public class ConnectivityInterceptor implements Interceptor {

    private Context context;

    ConnectivityInterceptor(Context context) {
        this.context = context;
        ConnectionManager.init(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!ConnectionManager.isConnected()) {
            throw new IOException(context.getString(R.string.no_internet_connection));
        } else {
            return chain.proceed(chain.request());
        }
    }
}