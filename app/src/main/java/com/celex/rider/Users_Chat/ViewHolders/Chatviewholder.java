package com.celex.rider.Users_Chat.ViewHolders;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.R;
import com.celex.rider.Users_Chat.ChatAdapter;
import com.celex.rider.Users_Chat.Chat_GetSet;


public class Chatviewholder extends RecyclerView.ViewHolder {

    public TextView message,datetxt,message_seen,msg_date;
    public View view;

    public Chatviewholder(View itemView) {
        super(itemView);
        view = itemView;

        this.message = view.findViewById(R.id.msgtxt);
        this.datetxt=view.findViewById(R.id.datetxt);
        message_seen=view.findViewById(R.id.message_seen);
        msg_date=view.findViewById(R.id.msg_date);

    }





    public void bind(final Chat_GetSet item,
                     final ChatAdapter.OnLongClickListener long_listener) {
        message.setOnLongClickListener(v ->  {
                long_listener.onLongclick(item,v);
                return false;

        });
    }
}

