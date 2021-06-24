package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.DataModels.My_Orders_Model;
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ChildViewHolder> {


    List<My_Orders_Model> datalist;
    Context context;
    Adapter_ClickListener clickListener;


    public HistoryAdapter(Context context, List<My_Orders_Model> datalist, Adapter_ClickListener listener) {
        this.datalist = datalist;
        this.context = context;
        this.clickListener = listener;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_layout, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

        My_Orders_Model my_orders_model_ = datalist.get(position);

        if (my_orders_model_.receiver_name != null && !my_orders_model_.receiver_name.equalsIgnoreCase("") && my_orders_model_.receiver_name.length() > 0) {
            String person_name = Functions.toTitleCase(my_orders_model_.receiver_name);
            holder.tv_actual_name.setText(person_name);
        }
        String delivery_date = my_orders_model_.delivered;
        if (delivery_date != null && !delivery_date.equals("0000-00-00 00:00:00")) {
            delivery_date = Functions.convert_datetime(delivery_date, "history");
            holder.tv_actual_Delivery_time.setText(delivery_date);
        } else {
            holder.histroy_rlt.setVisibility(View.GONE);
        }
        if (my_orders_model_.undelivery_datetime.equals("0000-00-00 00:00:00")) {
            holder.txt_status.setText("Delivered");
        } else {
            holder.txt_status.setText("Not Delivered");
            holder.layout_delivered.setBackgroundResource(R.drawable.d_round_corner_red__bkg);
        }
        holder.tv_order_no.setText(context.getResources().getString(R.string.order) + my_orders_model_.consignment_id);
        holder.tv_actual_Pickup_location.setText(my_orders_model_.sender_location_string);
        holder.bind(position, my_orders_model_, clickListener);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_no, tv_actual_name, tv_actual_Delivery_time, tv_actual_Pickup_location,txt_status;
        LinearLayout histroy_rlt;
        RelativeLayout layout_delivered;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_order_no = itemView.findViewById(R.id.tv_order_no);
            histroy_rlt = itemView.findViewById(R.id.histroy_rlt);
            tv_actual_name = itemView.findViewById(R.id.tv_actual_name);
            tv_actual_Delivery_time = itemView.findViewById(R.id.tv_actual_drop_off);
            tv_actual_Pickup_location = itemView.findViewById(R.id.tv_actual_Pickup_location);
            txt_status = itemView.findViewById(R.id.txt_status);
            layout_delivered = itemView.findViewById(R.id.layout_delivered);

        }


        public void bind(final int item, final My_Orders_Model all_apps_modelClass, final Adapter_ClickListener listener) {

            itemView.setOnClickListener(v -> {

                listener.On_Item_Click(item, all_apps_modelClass, v);


            });

        }
    }

}
