package saleh.ma.mostafa.gmail.com.advancedplacepicker.utilities;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import saleh.ma.mostafa.gmail.com.advancedplacepicker.R;
import saleh.ma.mostafa.gmail.com.advancedplacepicker.network.Result;

/**
 * Created by Mostafa on 07/09/2017.
 */

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.ViewHolder> {

    private List<Result> mDataSet;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public NearbyPlacesAdapter(Context context, List<Result> dataSet) {
        mDataSet = dataSet == null ? new ArrayList<Result>() : dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_place, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Result result = mDataSet.get(position);
        Utils.loadImage(mContext, holder.imgIcon, mDataSet.get(position).getIcon());
        holder.tvTitle.setText(result.getName());
        if (holder.address != null && !TextUtils.isEmpty(holder.address)) {
            holder.tvAddress.setText(holder.address);
        } else {
            Result.Location location = result.getGeometry().getLocation();
            LatLng coordinates = new LatLng(location.getLat(), location.getLng());
            AddressResolver.getInstance().getAddress(mContext, coordinates, new OnFinishedListener<String>() {
                @Override
                public void onSuccess(@Nullable String address) {
                    holder.tvAddress.setText(address);
                    holder.address = address;
                }

                @Override
                public void onFailure(String errorMessage, int errorCode) {
                    Log.e("Error", errorMessage);
                }
            });
        }
    }


    public void addAll(List<Result> results) {
        mDataSet.addAll(results);
        notifyDataSetChanged();
    }

    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView tvTitle;
        TextView tvAddress;
        String address;

        public ViewHolder(View itemView) {
            super(itemView);
            findViewsById(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(mDataSet.get(getAdapterPosition()));
                    }
                }
            });
        }

        private void findViewsById(View view) {
            tvAddress = (TextView) view.findViewById(R.id.tv_address);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            imgIcon = (ImageView) view.findViewById(R.id.img_icon);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Result result);
    }

}
