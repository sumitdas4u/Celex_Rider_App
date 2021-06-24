package com.celex.rider.Users_Chat.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.R;
import com.celex.rider.Users_Chat.ChatAdapter;
import com.celex.rider.Users_Chat.Chat_GetSet;


public class Chatimageviewholder extends RecyclerView.ViewHolder {

    public ImageView chatimage;
    public TextView datetxt,message_seen , msg_date;
    public ProgressBar p_bar;
    public ImageView not_send_message_icon;
    public View view;







    public Chatimageviewholder(View itemView) {
        super(itemView);
        view = itemView;

        this.chatimage = view.findViewById(R.id.chatimage);
        this.datetxt = view.findViewById(R.id.datetxt);
        message_seen = view.findViewById(R.id.message_seen);
        not_send_message_icon = view.findViewById(R.id.not_send_messsage);
        p_bar = view.findViewById(R.id.p_bar);
        msg_date = view.findViewById(R.id.msg_date);
    }






    public void bind(int pos ,final Chat_GetSet item,
                     final ChatAdapter.OnItemClickListener listener,
                     final ChatAdapter.OnLongClickListener long_listener) {

        chatimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(pos,item,v);
            }
        });

        chatimage.setOnLongClickListener(v ->  {

                long_listener.onLongclick(item,v);
                return false;

        });
    }


}
