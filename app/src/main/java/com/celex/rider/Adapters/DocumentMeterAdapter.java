package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.DataModels.DocumentHomeModel;
import com.celex.rider.DataModels.MeterReadingModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DocumentMeterAdapter extends RecyclerView.Adapter<DocumentMeterAdapter.ChildViewHolder> {


    List<MeterReadingModel> datalist;
    Context context;
    Adapter_ClickListener clickListener;


    public DocumentMeterAdapter(Context context, List<MeterReadingModel> datalist, Adapter_ClickListener clickListener) {
        this.datalist = datalist;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meter_reading, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {



        MeterReadingModel documentHomeModel = datalist.get(position);
        holder.tv_date.setText(documentHomeModel.date_reading);
        holder.tv_start_km.setText(documentHomeModel.start_km);
        holder.tv_end_km.setText(documentHomeModel.end_km);
        holder.tv_total_km.setText(documentHomeModel.total);

        //holder.img_start.(documentHomeModel.date_reading);





        holder.bind(position, documentHomeModel, clickListener);




    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date, tv_start_km, tv_end_km, tv_total_km;
        ImageView img_start,img_end;


        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

             tv_date=itemView.findViewById(R.id.tv_date);
             tv_start_km=itemView.findViewById(R.id.tv_start_km);
             tv_end_km=itemView.findViewById(R.id.tv_end_km);
             tv_total_km=itemView.findViewById(R.id.tv_total_km);

            img_start=itemView.findViewById(R.id.img_start);
            img_end=itemView.findViewById(R.id.img_end);

        }

        public void bind(final int item, final MeterReadingModel documentHomeModel, final Adapter_ClickListener listener) {

            img_start.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentHomeModel, v);


            });


            img_end.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentHomeModel, v);


            });

        }
    }


}
