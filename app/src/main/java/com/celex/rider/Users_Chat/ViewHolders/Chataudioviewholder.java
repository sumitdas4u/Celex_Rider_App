package com.celex.rider.Users_Chat.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celex.rider.R;
import com.celex.rider.Users_Chat.ChatAdapter;
import com.celex.rider.Users_Chat.Chat_GetSet;


public  class Chataudioviewholder extends RecyclerView.ViewHolder{

    public TextView datetxt,message_seen,total_time,msg_date;

    public ProgressBar p_bar;
    public SeekBar seekBar;
    private LinearLayout audio_bubble;
    public ImageView play_btn;


    private View view;






    public Chataudioviewholder(View itemView) {
        super(itemView);
        view = itemView;
        play_btn = view.findViewById(R.id.play_btn);
        audio_bubble=view.findViewById(R.id.audio_bubble);
        datetxt=view.findViewById(R.id.datetxt);
        message_seen=view.findViewById(R.id.message_seen);
        ImageView not_send_message_icon = view.findViewById(R.id.not_send_messsage);
        p_bar=view.findViewById(R.id.p_bar);
        msg_date=view.findViewById(R.id.msg_date);
        ImageView play_btn = view.findViewById(R.id.play_btn);
        this.seekBar=(SeekBar) view.findViewById(R.id.seek_bar);
        this.total_time=(TextView)view.findViewById(R.id.total_time);

    }





    public void bind(final int pos , final Chat_GetSet item,
                     final ChatAdapter.OnItemClickListener listener,
                     final ChatAdapter.OnLongClickListener long_listener) {

        audio_bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(pos,item,v);
            }
        });


        audio_bubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                long_listener.onLongclick(item,v);
                return false;
            }
        });

    }




}

