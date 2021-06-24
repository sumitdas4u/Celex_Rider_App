package com.celex.rider.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.DataModels.OrderModel;
import com.celex.rider.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Random;


public class OrderDetail_Adapter extends RecyclerView.Adapter<OrderDetail_Adapter.CustomViewHolder > {

    public Context context;
    ArrayList<OrderModel> dataList;
    int counter = 0;
    int lower=0;
    int higher;

    public OrderDetail_Adapter(Context context, ArrayList<OrderModel> dataList) {
        this.context = context;
        this.dataList =dataList;
    }


    @NonNull
    @Override
    public OrderDetail_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_detail_layout,null);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final OrderModel item= dataList.get(position);

        holder.reg_no.setText(item.getReg_No());

           int[] images = new int[3];
        images[0] = R.drawable.blue_back;
        images[1] = R.drawable.green_back;
        images[2] = R.drawable.undelivered_back;
        higher=images.length;
        if (counter >= lower)
            counter++;
        if (counter == higher)
            counter=lower;
        holder.reg_no.setBackground( ContextCompat.getDrawable(context,  images[counter])  );

    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView order_image;
        TextView reg_no ;

        public CustomViewHolder(View view) {
            super(view);

            reg_no =view.findViewById(R.id.order_reg_no);







            //  engine_no =view.findViewById(R.id.order_engine_no);
        //    order_image=view.findViewById(R.id.product_img);
           // chassis_no =view.findViewById(R.id.order_chassis_no);
          //  orderDetailPrice =view.findViewById(R.id.order_engine_no);

        }
    }
}