package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.DataModels.DriverModel;
import com.celex.rider.DataModels.MeterReadingModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeliveryBoyAdapter extends RecyclerView.Adapter<DeliveryBoyAdapter.ChildViewHolder> {


    List<DriverModel> datalist;
    Context context;
    Adapter_ClickListener clickListener;
    private int selected_position = -1;

    public DeliveryBoyAdapter(Context context, List<DriverModel> datalist, Adapter_ClickListener clickListener) {
        this.datalist = datalist;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_boy, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {



        DriverModel driverModel = datalist.get(position);
        holder.tv_name.setText(driverModel.name);
        holder.tv_mobile.setText(driverModel.mobile);

        Picasso.get().load(driverModel.photo)
                .placeholder(R.drawable.ic_user_icon)
                .into(holder.img_profile);
        if (driverModel.is_online==1) {

            holder.img_status.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_image));
        } else {
            holder.img_status.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_image_offline));

        }

        if(selected_position!=position){
            holder.img_tick.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tick));

        }
        else
        {
            holder.img_tick.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tick_done));
        }





        holder.bind(position, driverModel, clickListener);




    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_mobile;
        ImageView img_profile,img_status,img_tick;
        RelativeLayout rl_holder ;


        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name=itemView.findViewById(R.id.tv_name);
            tv_mobile=itemView.findViewById(R.id.tv_mobile);

            img_profile=itemView.findViewById(R.id.img_profile);
            img_status=itemView.findViewById(R.id.img_status);
            img_tick=itemView.findViewById(R.id.img_tick);

            rl_holder=itemView.findViewById(R.id.rl_holder);

        }

        public void bind(final int item, final DriverModel documentHomeModel, final Adapter_ClickListener listener) {

            tv_mobile.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentHomeModel, v);


            });
            rl_holder.setOnClickListener(v -> {

                        if(listener != null) {
                            int position = getAdapterPosition();

                            if (position != RecyclerView.NO_POSITION) {
                                listener.On_Item_Click(item, documentHomeModel, v);
                                selected_position = position;
                                notifyDataSetChanged();
                            }

                        }



            });


        }
    }


}
