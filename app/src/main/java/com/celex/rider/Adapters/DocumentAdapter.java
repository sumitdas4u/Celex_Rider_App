package com.celex.rider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.R;
import com.celex.rider.interfaces.Adapter_ClickListener;
import com.celex.rider.DataModels.DocumentModel;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ChildViewHolder> {


    List<DocumentModel> documentModelList;
    Context context;
    Adapter_ClickListener clickListener;


    public DocumentAdapter(Context context, List<DocumentModel> documentModelList, Adapter_ClickListener clickListener) {
        this.documentModelList = documentModelList;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload_document_layout, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

        DocumentModel documentModel = documentModelList.get(position);


        holder.tv_name_document.setText(documentModel.documnet_name);
        holder.document_img.setImageBitmap(documentModel.image);


        holder.bind(position, documentModel, clickListener);
    }

    @Override
    public int getItemCount() {
        return documentModelList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageButton delete_btn;
        ImageView document_img;
        TextView tv_name_document;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            delete_btn = itemView.findViewById(R.id.delete_btn);
            tv_name_document = itemView.findViewById(R.id.tv_name_document);
            document_img = itemView.findViewById(R.id.document_img);


        }

        public void bind(final int item, final DocumentModel documentModel, final Adapter_ClickListener listener) {

            itemView.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentModel, v);

            });

            delete_btn.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentModel, v);


            });

            tv_name_document.setOnClickListener(v -> {

                listener.On_Item_Click(item, documentModel, v);


            });


        }
    }


}
