package com.celex.rider.Users_Chat;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.Users_Chat.ViewHolders.Alertviewholder;
import com.celex.rider.Users_Chat.ViewHolders.Chataudioviewholder;
import com.celex.rider.Users_Chat.ViewHolders.Chatimageviewholder;
import com.celex.rider.Users_Chat.ViewHolders.Chatviewholder;
import com.celex.rider.CodeClasses.Variables;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Chat_GetSet> mDataSet;
    String myID;
    private static final int mychat = 1;
    private static final int friendchat = 2;

    private static final int mychatimage = 3;
    private static final int otherchatimage = 4;


    private static final int alert_message = 7;

    private static final int my_audio_message = 8;
    private static final int other_audio_message = 9;


    Context context;
    Integer today_day = 0;

    private OnItemClickListener listener;
    private ChatAdapter.OnLongClickListener long_listener;


    public interface OnItemClickListener {
        void onItemClick(int postion, Chat_GetSet item, View view);
    }


    public interface OnLongClickListener {
        void onLongclick(Chat_GetSet item, View view);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     *                Device id
     */

    ChatAdapter(List<Chat_GetSet> dataSet, String id, Context context, ChatAdapter.OnItemClickListener listener, ChatAdapter.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID = id;
        this.context = context;
        this.listener = listener;
        this.long_listener = long_listener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);
    }


    // this is the all types of view that is used in the chat
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype) {
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case mychat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chatviewholder mychatHolder = new Chatviewholder(v);
                return mychatHolder;
            case friendchat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chatviewholder friendchatHolder = new Chatviewholder(v);
                return friendchatHolder;
            case mychatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                Chatimageviewholder mychatimageHolder = new Chatimageviewholder(v);
                return mychatimageHolder;
            case otherchatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                Chatimageviewholder otherchatimageHolder = new Chatimageviewholder(v);
                return otherchatimageHolder;

            case my_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_my, viewGroup, false);
                Chataudioviewholder chataudioviewholder = new Chataudioviewholder(v);
                return chataudioviewholder;

            case other_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_other, viewGroup, false);
                Chataudioviewholder other = new Chataudioviewholder(v);
                return other;

            case alert_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alertviewholder alertviewholder = new Alertviewholder(v);
                return alertviewholder;

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat_GetSet chat = mDataSet.get(position);

        if (chat.getType().equals("text")) {
            Chatviewholder chatviewholder = (Chatviewholder) holder;
            // check if the message is from sender or receiver

            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatviewholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                else
                    chatviewholder.message_seen.setText("Sent");
            } else {

                chatviewholder.message_seen.setText("");
            }

            chatviewholder.message.setText(chat.getText());
            chatviewholder.msg_date.setText(Show_Message_Time(chat.getTimestamp()));

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                Chat_GetSet chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chatviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
            }

            chatviewholder.bind(chat, long_listener);

        } else if (chat.getType().equals("image")) {
            final Chatimageviewholder chatimageholder = (Chatimageviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatimageholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                else
                    chatimageholder.message_seen.setText("Sent");

            } else {
                chatimageholder.message_seen.setText("");
            }
            if (chat.getPic_url().equals("none")) {
                if (Chat_Activity.uploading_image_id.equals(chat.getChat_id())) {
                    chatimageholder.p_bar.setVisibility(View.VISIBLE);
                    chatimageholder.message_seen.setText("");
                } else {
                    chatimageholder.p_bar.setVisibility(View.GONE);
                    chatimageholder.not_send_message_icon.setVisibility(View.VISIBLE);
                    chatimageholder.message_seen.setText("Not delivered. ");
                }
            } else {
                chatimageholder.not_send_message_icon.setVisibility(View.GONE);
                chatimageholder.p_bar.setVisibility(View.GONE);
            }
            chatimageholder.msg_date.setText(Show_Message_Time(chat.getTimestamp()));
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                Chat_GetSet chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

                Picasso.get().load(chat.getPic_url()).placeholder(R.drawable.image_placeholder).resize(400, 400).centerCrop().into(chatimageholder.chatimage);
            } else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                Picasso.get().load(chat.getPic_url()).placeholder(R.drawable.image_placeholder).resize(400, 400).centerCrop().into(chatimageholder.chatimage);
            }

            chatimageholder.bind(position, mDataSet.get(position), listener, long_listener);
        } else if (chat.getType().equals("audio")) {
            final Chataudioviewholder chataudioviewholder = (Chataudioviewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chataudioviewholder.message_seen.setText("Seen at " + ChangeDate_to_time(chat.getTime()));
                else
                    chataudioviewholder.message_seen.setText("Sent");

            } else {
                chataudioviewholder.message_seen.setText("");
            }

            chataudioviewholder.msg_date.setText(Show_Message_Time(chat.getTimestamp()));

            if (chat.getSender_id().equals(myID) && chat.getPic_url().equals("none")) {
                chataudioviewholder.p_bar.setVisibility(View.VISIBLE);
            } else {
                chataudioviewholder.p_bar.setVisibility(View.GONE);
            }


            String downloadid = Variables.download_sharedPreferences.getString(chat.getChat_id(), "");
            if (!downloadid.equals("")) {
                String status = Functions.Check_Image_Status(context, Long.parseLong(downloadid));
                if (status.equals("STATUS_FAILED") || status.equals("STATUS_SUCCESSFUL")) {
                    chataudioviewholder.p_bar.setVisibility(View.GONE);
                    Variables.download_sharedPreferences.edit().remove(chat.getChat_id()).commit();
                } else {
                    chataudioviewholder.p_bar.setVisibility(View.VISIBLE);
                }
            }


            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                Chat_GetSet chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chataudioviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                    chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
            } else {
                chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

            chataudioviewholder.msg_date.setText(Show_Message_Time(chat.getTimestamp()));
            chataudioviewholder.seekBar.setEnabled(false);

            File fullpath = new File(Variables.folder_freshly_rider + chat.chat_id + ".mp3");
            if (fullpath.exists()) {
                chataudioviewholder.total_time.setText(getfileduration(Uri.parse(fullpath.getAbsolutePath())));
            } else {
                chataudioviewholder.total_time.setText(null);
            }

            if (Chat_Activity.playing_id.equals(chat.chat_id) && Chat_Activity.mediaplayer != null) {
                chataudioviewholder.play_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_icon));
                chataudioviewholder.seekBar.setProgress(Chat_Activity.media_player_progress);
            } else {
                chataudioviewholder.play_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_icon));
                chataudioviewholder.seekBar.setProgress(0);
            }
            chataudioviewholder.bind(position, mDataSet.get(position), listener, long_listener);

        } else if (chat.getType().equals("delete")) {
            Alertviewholder alertviewholder = (Alertviewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.textColor));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_border_gray_line));

            alertviewholder.message.setText(R.string.deleted_message);

            if (position != 0) {
                Chat_GetSet chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

            } else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

        }


    }

    @Override
    public int getItemViewType(int position) {
        // get the type it view ( given message is from sender or receiver)
        if (mDataSet.get(position).getType().equals("text")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return mychat;
            }
            return friendchat;
        } else if (mDataSet.get(position).getType().equals("image")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return mychatimage;
            }

            return otherchatimage;
        } else if (mDataSet.get(position).getType().equals("audio")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return my_audio_message;
            }
            return other_audio_message;
        } else {
            return alert_message;
        }
    }

    /**
     * Inner Class for a recycler view
     */


    // change the date into (today ,yesterday and date)
    public String ChangeDate(String date) {
        //current date in millisecond
        long currenttime = System.currentTimeMillis();

        //database date in millisecond
        long databasedate = 0;
        Date d = null;
        try {
            d = Variables.DATE_FORMAT.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if (today_day == chatday) {
                return "Today";
            } else if ((today_day - chatday) == 1) {
                return "Yesterday";
            }
        } else if (difference < 172800000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if ((today_day - chatday) == 1) {
                return "Yesterday";
            }

        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");

        if (d != null) {
            return sdf.format(d);
        } else {
            return "";
        }
    }


    public String Show_Message_Time(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        Date d = null;
        try {
            d = Variables.DATE_FORMAT.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d != null) {
            return sdf.format(d);
        } else
            return "null";
    }


    // change the date into (today ,yesterday and date)
    public String ChangeDate_to_time(String date) {


        Date d = null;
        try {
            d = Variables.DATE_FORMAT1.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }


    // get the audio file duration that is store in our directory
    public String getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            long second = (file_duration / 1000) % 60;
            long minute = (file_duration / (1000 * 60)) % 60;

            return String.format("%02d:%02d", minute, second);
        } catch (Exception e) {
            return null;
        }


    }


}
