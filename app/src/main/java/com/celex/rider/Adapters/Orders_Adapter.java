package com.celex.rider.Adapters;

import android.content.Context;
import android.graphics.Color;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Orders_Adapter extends RecyclerView.Adapter<Orders_Adapter.FilterViewHolder> {

    List<My_Orders_Model> dataList;
    Context context;
    Adapter_ClickListener adapter_clickListener;

    public Orders_Adapter(Context context, List<My_Orders_Model> dataList, Adapter_ClickListener adapter_clickListener) {

        this.context = context;
        this.dataList = dataList;
        this.adapter_clickListener = adapter_clickListener;
    }


    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_orders_layout, null);
        return new FilterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {

        My_Orders_Model my_orders_model_ = dataList.get(position);

        if (my_orders_model_.order_person_name != null && !my_orders_model_.equals("")) {
            String person_name = Functions.toTitleCase(my_orders_model_.order_person_name);
            holder.tvperson_name.setText(person_name);
        }

        String order_create_date = my_orders_model_.order_create_date;
        String order_pick_up_date = my_orders_model_.pickup_datetime;
        String on_the_way_date = my_orders_model_.on_the_way_to_dropoff;
        String deliver_date = my_orders_model_.delivery_datetime;
        if (order_create_date != null && !order_create_date.equals("0000-00-00 00:00:00")) {
            holder.order_create_date.setText(Functions.getTimeAgo(order_create_date));
        }
        if (order_pick_up_date != null && !order_pick_up_date.equals("0000-00-00 00:00:00")) {

            holder.order_create_date.setText(Functions.getTimeAgo(order_pick_up_date));
        }
        if (on_the_way_date != null && !on_the_way_date.equals("0000-00-00 00:00:00")) {

            holder.order_create_date.setText(Functions.getTimeAgo(on_the_way_date));
        }
        if (deliver_date != null && !deliver_date.equals("0000-00-00 00:00:00")) {

            holder.order_create_date.setText(Functions.getTimeAgo(deliver_date));
        }


        holder.tvOrderNo.setText(my_orders_model_.consignment_id);
        holder.tvQty.setText(my_orders_model_.qty);
        holder.tvWeight.setText(my_orders_model_.weight);
        holder.tv_actual_drop_off.setText(my_orders_model_.delievery_address);

        if (my_orders_model_.current_view.equals("Pending Orders")) {
            holder.btn_no.setVisibility(View.GONE);
            holder.btn_yes.setVisibility(View.VISIBLE);
            holder.btn_transfer.setVisibility(View.GONE);
            holder.div_accept.setVisibility(View.GONE);
            if (my_orders_model_.is_transferred.equals("1")) {

                holder.rlt_detail_div.setVisibility(View.VISIBLE);
                holder.tvOrderStatus.setTextColor(Color.RED);
                holder.tvOrderStatus.setText(my_orders_model_.transfer_reason);
            }else{
                holder.tvOrderStatus.setVisibility(View.GONE);
            }

        } else if (my_orders_model_.current_view.equals("Active Orders")) {
            holder.btn_no.setVisibility(View.VISIBLE);
            holder.btn_transfer.setVisibility(View.VISIBLE);
            holder.cancel_detail_div.setVisibility(View.VISIBLE);
            holder.btn_yes.setVisibility(View.GONE);
            holder.rlt_detail_div.setVisibility(View.VISIBLE);
            holder.tvOrderStatus.setText("Picked Up");
        } else {
            holder.tvOrderStatus.setText(my_orders_model_.current_view);
            holder.rlt_detail_div.setVisibility(View.VISIBLE);
            holder.cancel_detail_div.setVisibility(View.GONE);
            holder.div_accept.setVisibility(View.GONE);
            holder.btn_yes.setVisibility(View.GONE);
            holder.tvOrderStatus.setText("On The Way");

        }

        holder.bind(position, my_orders_model_, adapter_clickListener);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderNo, tvQty, tvWeight, tvperson_name, tvDate, tv_actual_drop_off, tvActualPickupLocation, tvOrderStatus, order_create_date;
        RelativeLayout btn_yes, btn_no, btn_transfer, btn_details_p;
        RelativeLayout rlt_detail_div, btn_details;
        LinearLayout div_accept,cancel_detail_div;
        LinearLayout histroy_rlt;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            order_create_date = itemView.findViewById(R.id.order_create_date);
            tvperson_name = itemView.findViewById(R.id.tv_person_name);
            tv_actual_drop_off = itemView.findViewById(R.id.tv_actual_drop_off);
            tvActualPickupLocation = itemView.findViewById(R.id.tv_actual_Pickup_location);
            btn_yes = itemView.findViewById(R.id.btn_yes);
            histroy_rlt = itemView.findViewById(R.id.histroy_rlt);
            btn_no = itemView.findViewById(R.id.btn_no);
            btn_transfer = itemView.findViewById(R.id.btn_transfer);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvWeight = itemView.findViewById(R.id.tv_weigh);
            btn_transfer = itemView.findViewById(R.id.btn_transfer);
            btn_details = itemView.findViewById(R.id.btn_details);
            tvDate = itemView.findViewById(R.id.tv_last_date);
            rlt_detail_div = itemView.findViewById(R.id.rlt_detail_div);
            div_accept = itemView.findViewById(R.id.div_accept);
            cancel_detail_div = itemView.findViewById(R.id.cancel_detail_div);
            btn_details_p = itemView.findViewById(R.id.btn_details_p);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
        }


        public void bind(final int item, final My_Orders_Model my_orders_model_, final Adapter_ClickListener listener) {

            itemView.setOnClickListener(v -> {

                listener.On_Item_Click(item, my_orders_model_, v);


            });

            btn_details.setOnClickListener(v -> {

                listener.On_Item_Click(item, my_orders_model_, v);


            });

            btn_yes.setOnClickListener(v -> {

                listener.On_Item_Click(item, my_orders_model_, v);


            });

            btn_no.setOnClickListener(v -> {

                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_transfer.setOnClickListener(v -> {

                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_details_p.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });

        }

    }


}
