package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.celex.rider.DataModels.DocumentModel;
import com.celex.rider.DataModels.DriverModel;
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransferImageAdapter extends RecyclerView.Adapter<TransferImageAdapter.ChildViewHolder> {



    Context context;
    Adapter_ClickListener clickListener;
    private   List<String> url;

    public TransferImageAdapter(Context context, List<String> documentModelList, Adapter_ClickListener clickListener) {
        this.url = documentModelList;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer_image_my, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {


        Picasso.get().load(url.get(position))
                .placeholder(R.drawable.circle_image)
                .into(holder.image);
        holder.bind(position, url.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return url.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;


        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);



        }

        public void bind(final int item, final String documentModel, final Adapter_ClickListener listener) {

            image.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentModel, v);

            });




        }
    }


}
