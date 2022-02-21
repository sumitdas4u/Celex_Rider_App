package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celex.rider.AllActivitys.TripActivity;
import com.celex.rider.DataModels.MeterReadingModel;
import com.celex.rider.DataModels.TripModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ChildViewHolder> {


    List<TripModel> datalist;
    Context context;
    Adapter_ClickListener clickListener;


    public TripAdapter(Context context, List<TripModel> datalist, Adapter_ClickListener clickListener) {
        this.datalist = datalist;
        this.context = context;
        this.clickListener = clickListener;
    }



    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_summary, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {



        TripModel tripModel = datalist.get(position);
        holder.tv_date.setText(tripModel.trip_date);
        holder.tv_km.setText(tripModel.trip_distance);
        holder.tv_duration.setText(tripModel.trip_duration);
        holder.tv_fixed_distance.setText(tripModel.trip_fixed_distance);
        holder.tv_request_distance.setText(tripModel.trip_request_distance);







        holder.bind(position, tripModel, clickListener);




    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date, tv_km, tv_duration, tv_request_distance, tv_fixed_distance;
        ImageView img_start,img_end;


        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

             tv_date=itemView.findViewById(R.id.tv_date);
             tv_km=itemView.findViewById(R.id.tv_km);
             tv_duration=itemView.findViewById(R.id.tv_duration);
             tv_request_distance=itemView.findViewById(R.id.tv_request_distance);
                tv_fixed_distance=itemView.findViewById(R.id.tv_fixed_distance);


        }

        public void bind(final int item, final TripModel tripModel, final Adapter_ClickListener listener) {

/*            img_start.setOnClickListener(v -> {

                listener.On_Item_Click(item, tripModel, v);


            });


            img_end.setOnClickListener(v -> {

                listener.On_Item_Click(item, tripModel, v);


            });*/

        }
    }


}
