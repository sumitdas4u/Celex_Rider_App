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
import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;
import com.celex.rider.DataModels.DocumentHomeModel;

import java.util.List;

public class DocumentHomeAdapter extends RecyclerView.Adapter<DocumentHomeAdapter.ChildViewHolder> {


    List<DocumentHomeModel> datalist;
    Context context;
    Adapter_ClickListener clickListener;


    public DocumentHomeAdapter(Context context, List<DocumentHomeModel> datalist, Adapter_ClickListener clickListener) {
        this.datalist = datalist;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_documents_layout, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

        DocumentHomeModel documentHomeModel = datalist.get(position);


        holder.tv_name_document.setText(documentHomeModel.type);
        holder.bind(position, documentHomeModel, clickListener);

        if (documentHomeModel.status.equals("1")) {
            holder.tv_status.setText(R.string.approved);
        } else if (documentHomeModel.status.equals("0")) {
            holder.tv_status.setText(R.string.pending);
        } else {
            holder.tv_status.setText(R.string.rejected);
        }


        if (datalist.size() > 0) {
            if (position == datalist.size() - 1) {
                // Empty
            } else {
                holder.drawable_rlt.setBackground(context.getResources().getDrawable(R.drawable.lite_gray_line));
            }

        }


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name_document, tv_status;
        LinearLayout main_div;
        RelativeLayout drawable_rlt;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name_document = itemView.findViewById(R.id.tv_name_document);
            tv_status = itemView.findViewById(R.id.tv_status);
            main_div = itemView.findViewById(R.id.main_div);
            drawable_rlt = itemView.findViewById(R.id.drawable_rlt);
        }

        public void bind(final int item, final DocumentHomeModel documentHomeModel, final Adapter_ClickListener listener) {

            main_div.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentHomeModel, v);


            });

        }
    }

    public String decodeString(String strData) {
        if (strData == null) {
            return "";
        }
        return strData.replaceAll("_", " ");
    }


}
