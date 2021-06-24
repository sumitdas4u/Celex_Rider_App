package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.celex.rider.DataModels.SelectVehicleModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ChildViewHolder> {


    List<SelectVehicleModel> dataList, temp_modellist;
    Context context;
    Adapter_ClickListener clickListener;
    String vehicle_id;


    public VehicleAdapter(Context context, String vehicle_id, List<SelectVehicleModel> dataList, Adapter_ClickListener clickListener) {
        this.dataList = new ArrayList<>(dataList);
        this.temp_modellist = dataList;
        this.context = context;
        this.clickListener = clickListener;
        this.vehicle_id = vehicle_id;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_layout, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

        SelectVehicleModel selectVehicleModel = temp_modellist.get(position);

        String vehicle_name = selectVehicleModel.Vehicle_name;

        String vehicle_name_cap = vehicle_name.substring(0, 1).toUpperCase() + vehicle_name.substring(1);
        holder.tv_vehical.setText(vehicle_name_cap);

        if (vehicle_id != null) {
            if (vehicle_id.equals(selectVehicleModel.Vehicle_id)) {
                holder.iv_tick.setVisibility(View.VISIBLE);
            }
        }

        holder.bind(position, selectVehicleModel, clickListener);
    }

    @Override
    public int getItemCount() {
        return temp_modellist.size();
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<SelectVehicleModel> filteredList = new ArrayList<>();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList.addAll(dataList);
                } else {

                    filteredList.clear();
                    for (SelectVehicleModel row : dataList) {
                        if (row.Vehicle_name.toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(row);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                temp_modellist.clear();
                temp_modellist = (List<SelectVehicleModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tv_vehical;
        ImageView iv_tick;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_vehical = itemView.findViewById(R.id.name_txt);
            iv_tick = itemView.findViewById(R.id.iv_tick);
        }

        public void bind(final int item, final SelectVehicleModel all_apps_modelClass, final Adapter_ClickListener listener) {

            itemView.setOnClickListener(v -> {

                listener.On_Item_Click(item, all_apps_modelClass, v);


            });

        }
    }
}
