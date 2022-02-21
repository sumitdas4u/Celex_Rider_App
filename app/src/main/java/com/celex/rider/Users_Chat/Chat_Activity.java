package com.celex.rider.Users_Chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.celex.rider.CodeClasses.ApiRequest;
import com.celex.rider.CodeClasses.Api_urls;
import com.celex.rider.CodeClasses.Functions;
import com.celex.rider.R;
import com.celex.rider.Users_Chat.Audio.Play_Audio_F;
import com.celex.rider.Users_Chat.Audio.SendAudio;
import com.celex.rider.CodeClasses.RootFragment;
import com.celex.rider.CodeClasses.Variables;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.celex.rider.interfaces.Callback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class Chat_Activity extends RootFragment {

    DatabaseReference rootref;
    String senderid = "";
    String Receiverid = "";
    String store_id = "";
    String Receiver_name = "";
    String Receiver_pic = "null";
    String fragment_type, action_type, order_id = "null";
    public static String token = "null";
    EditText message;
    RelativeLayout write_layout;
    private DatabaseReference Adduser_to_inbox;
    private DatabaseReference mchatRef_reteriving;
    private DatabaseReference send_typing_indication;
    private DatabaseReference receive_typing_indication;
    RecyclerView chatrecyclerview;
    TextView reciver_name;
    private List<Chat_GetSet> mChats = new ArrayList<>();
    ChatAdapter mAdapter;
    ProgressBar p_bar;
    Query query_getchat;
    ImageView profileimage;
    public String sender_name, rider_pic;
    private Context context;
    private View view;
    private ImageView sendbtn;
    private RecordButton mic_btn;
    private SendAudio sendAudio;
    File direct;


    public static MediaPlayer mediaplayer;
    public static String playing_id = "none";
    public static int media_player_progress = 0;
    public int audio_postion;


    public static String uploading_image_id = "none";
    public static String uploading_Audio_id = "none";


    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_chat, container, false);

        context = getContext();

        Bundle bundle = getArguments();


        direct = new File(Variables.folder_freshly_rider);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);

        // intialize the database refer
        rootref = FirebaseDatabase.getInstance().getReference();
        Adduser_to_inbox = FirebaseDatabase.getInstance().getReference();
        message = (EditText) view.findViewById(R.id.msgedittext);
        write_layout = view.findViewById(R.id.write_layout);
        reciver_name = view.findViewById(R.id.reciver_name);
        profileimage = view.findViewById(R.id.profileimage);

//        String l_name = (Variables.userDetails_pref.getString(Variables.lname, ""));
        String f_name = (Variables.userDetails_pref.getString(Variables.name, ""));
        rider_pic = (Variables.userDetails_pref.getString(Variables.user_pic, ""));

        if (rider_pic == null || (rider_pic.equals("") || rider_pic.equals("null"))) {
            rider_pic = "";
        }

        sender_name = f_name;

        if (bundle != null) {

            senderid = Variables.userDetails_pref.getString(Variables.id, "");
            Receiverid = bundle.getString("st_store_user_id");
            store_id = bundle.getString("store_id");
            order_id = bundle.getString("order_id");
            Receiver_name = bundle.getString("store_name");
            Receiver_pic = bundle.getString("store_img");
            fragment_type = bundle.getString("fragment");
            action_type = bundle.getString("type");
            if (Receiver_pic != null && !Receiver_pic.equals("")) {
                Picasso.get().load(Api_urls.IMAGE_BASE_URL + Receiver_pic)
                        .placeholder(R.drawable.ic_user_icon)
                        .into(profileimage);
            }
            reciver_name.setText(Receiver_name);

            Variables.userDetails_pref.edit().putString(Variables.chat_reciever_id, Receiverid).apply();

        }
        token = "null";
        rootref.child("Users").child(Receiverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    token = dataSnapshot.child("token").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //on cancelled this method will call
            }
        });


        p_bar = view.findViewById(R.id.progress_bar);

        chatrecyclerview = (RecyclerView) view.findViewById(R.id.chatlist);
        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setStackFromEnd(true);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        ((SimpleItemAnimator) chatrecyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new ChatAdapter(mChats, senderid, context, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, Chat_GetSet item, View v) {

                if (item.getType().equals("image"))
                    OpenfullsizeImage(item);

                if (v.getId() == R.id.audio_bubble) {
                    RelativeLayout mainlayout = (RelativeLayout) v.getParent();
                    File fullpath = new File(Variables.folder_freshly_rider + item.chat_id + ".mp3");
                    if (fullpath.exists()) {

                        if (playing_id.equals(item.chat_id)) {
                            Stop_playing();
                        } else {
                            Play_audio(postion, item);
                        }

                    } else {
                        download_audio((ProgressBar) mainlayout.findViewById(R.id.p_bar), item);
                    }

                }
            }

        }, new ChatAdapter.OnLongClickListener() {
            @Override
            public void onLongclick(Chat_GetSet item, View view) {

                if (senderid.equals(item.getSender_id()))
                    delete_Message_dialog(item);

            }
        });

        chatrecyclerview.setAdapter(mAdapter);

        chatrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size() > 9)) {
                    userScrolled = false;
                    rootref.child("chat").child(senderid + "-" + store_id).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<Chat_GetSet> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Chat_GetSet item = snapshot.getValue(Chat_GetSet.class);
                                        arrayList.add(item);
                                    }
                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        mChats.add(0, arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();

                                    if (arrayList.size() > 8) {
                                        chatrecyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // on cancel this method will be call
                                }
                            });
                }
            }
        });
        sendbtn = view.findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(message.getText().toString())) {
                SendMessage(message.getText().toString());
                message.setText(null);

            }
        });

        view.findViewById(R.id.uploadimagebtn).setOnClickListener(v -> {

            selectfile();

        });

        view.findViewById(R.id.Goback).setOnClickListener(v -> {

            Functions.hideSoftKeyboard(getActivity());
            getFragmentManager().popBackStack();

        });

        message.setOnFocusChangeListener((v, hasFocus) -> {

            if (!hasFocus) {
                SendTypingIndicator(false);
            }

        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    SendTypingIndicator(false);
                }
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SendTypingIndicator(true);
                    sendbtn.setVisibility(View.VISIBLE);
                    mic_btn.setVisibility(View.GONE);
                } else {
                    sendbtn.setVisibility(View.VISIBLE);
                    mic_btn.setVisibility(View.GONE);
                    SendTypingIndicator(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendAudio = new SendAudio(context, message, rootref, Adduser_to_inbox, sender_name,
                senderid, Receiverid, Receiver_name, Receiver_pic, order_id, token);


        mic_btn = view.findViewById(R.id.mic_btn);
        RecordView recordView = (RecordView) view.findViewById(R.id.record_view);
        mic_btn.setRecordView(recordView);
        recordView.setSoundEnabled(true);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                view.findViewById(R.id.write_layout).setVisibility(View.GONE);
                view.findViewById(R.id.uploadimagebtn).setVisibility(View.GONE);
                if (check_Recordpermission() && check_writeStoragepermission()) {
                    sendAudio.startRecording();
                }
            }

            @Override
            public void onCancel() {

                sendAudio.stop_timer();
                view.findViewById(R.id.write_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.uploadimagebtn).setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime) {
                sendAudio.stopRecording();

                view.findViewById(R.id.write_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.uploadimagebtn).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                sendAudio.stop_timer_without_recoder();
                view.findViewById(R.id.write_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.uploadimagebtn).setVisibility(View.VISIBLE);
            }
        });
        recordView.setSlideToCancelText("Slide to cancel");
        mic_btn.setListenForRecord(true);
        recordView.setLessThanSecondAllowed(false);


        ReceivetypeIndication();


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                getFragmentManager().popBackStack();
                return false;
            }
            return true;

        });


        return view;
    }


    ValueEventListener valueEventListener;
    ChildEventListener eventListener;

    @Override
    public void onStart() {
        super.onStart();

        Variables.Opened_Chat_id = Receiverid;

        mChats.clear();
        mchatRef_reteriving = FirebaseDatabase.getInstance().getReference();
        query_getchat = mchatRef_reteriving.child("chat").child(senderid + "-" + Receiverid + "-" + order_id);


        // this will get all the messages between two users
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Chat_GetSet model = dataSnapshot.getValue(Chat_GetSet.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatrecyclerview.scrollToPosition(mChats.size() - 1);
                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
                ChangeStatus();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        Chat_GetSet model = dataSnapshot.getValue(Chat_GetSet.class);

                        for (int i = mChats.size() - 1; i >= 0; i--) {
                            if (mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())) {
                                mChats.remove(i);
                                mChats.add(i, model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // on child remove this method will be call
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // on child moved this method will be call
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        };
        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                p_bar.setVisibility(View.GONE);
                query_getchat.removeEventListener(valueEventListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on cancel this method will be call
            }
        };

        query_getchat.limitToLast(20).addChildEventListener(eventListener);
        mchatRef_reteriving.child("chat").addValueEventListener(valueEventListener);
    }


    // this method will change the status to ensure that
    // user is seen all the message or not (in both chat node and Chatinbox node)
    public void ChangeStatus() {
        final Date c = Calendar.getInstance().getTime();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(Receiverid + "-" + senderid).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(senderid + "-" + Receiverid).orderByChild("status").equalTo("0");

        final DatabaseReference inbox_change_status_1 = reference.child("Inbox").child(senderid + "/" + Receiverid);
        final DatabaseReference inbox_change_status_2 = reference.child("Inbox").child(Receiverid + "/" + senderid);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", Variables.DATE_FORMAT1.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on cancel this method will be call
            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", Variables.DATE_FORMAT1.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on cancel or error this method will be call
            }
        });


        inbox_change_status_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.child("id").getValue().equals(Receiverid)) {
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("status", "1");
                    inbox_change_status_1.updateChildren(result);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on cancel or error this method will be call
            }
        });


        inbox_change_status_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.child("id").getValue().equals(Receiverid)) {
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("status", "1");
                    inbox_change_status_2.updateChildren(result);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.DATE_FORMAT.format(c);


        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid + "-" + order_id;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid + "-" + order_id;

        DatabaseReference reference = rootref.child("chat").child(senderid + "-" + Receiverid + "-" + order_id).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", sender_name);
        message_user_map.put("timestamp", formattedDate);


        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, (databaseError, databaseReference) -> {
            //if first message then set the visibility of whoops layout gone
            String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
            String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;


            HashMap sendermap = new HashMap<>();
            sendermap.put("id", senderid);
            sendermap.put("name", sender_name);
            sendermap.put("msg", message);
            sendermap.put("pic", rider_pic);
            sendermap.put("status", "0");
            sendermap.put("type", "rider");
            sendermap.put("order_id", order_id);
            sendermap.put("store_id", senderid);
            sendermap.put("timestamp", -1 * System.currentTimeMillis());
            sendermap.put("date", formattedDate);


            HashMap receivermap = new HashMap<>();
            receivermap.put("id", Receiverid);
            receivermap.put("name", Receiver_name);
            receivermap.put("msg", message);
            receivermap.put("pic", Receiver_pic);
            receivermap.put("status", "1");
            receivermap.put("type", "rider");
            receivermap.put("store_id", Receiverid);
            receivermap.put("order_id", order_id);
            receivermap.put("timestamp", -1 * System.currentTimeMillis());
            receivermap.put("date", formattedDate);


            HashMap both_user_map = new HashMap<>();
            both_user_map.put(inbox_sender_ref, receivermap);
            both_user_map.put(inbox_receiver_ref, sendermap);

            Chat_Activity.SendPushNotification(getActivity(), Receiver_name, message, Receiverid, senderid, order_id);

        });
    }

    // this method will upload the image in chhat
    public void UploadImage(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] data = byteArrayOutputStream.toByteArray();

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.DATE_FORMAT.format(c);

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        DatabaseReference dref = rootref.child("chat").child(senderid + "-" + Receiverid + "-" + order_id).push();
        final String key = dref.getKey();
        uploading_image_id = key;
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid + "-" + order_id;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid + "-" + order_id;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "image");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", sender_name);
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);

        reference.child("images").child(key + ".jpg").putBytes(data).addOnSuccessListener(taskSnapshot -> {


            reference.child("images").child(key + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {

                uploading_image_id = "none";

                HashMap message_user_map = new HashMap<>();
                message_user_map.put("receiver_id", Receiverid);
                message_user_map.put("sender_id", senderid);
                message_user_map.put("chat_id", key);
                message_user_map.put("text", "");
                message_user_map.put("type", "image");
                message_user_map.put("pic_url", uri.toString());
                message_user_map.put("status", "0");
                message_user_map.put("time", "");
                message_user_map.put("sender_name", sender_name);
                message_user_map.put("timestamp", formattedDate);


                HashMap user_map = new HashMap<>();

                user_map.put(current_user_ref + "/" + key, message_user_map);
                user_map.put(chat_user_ref + "/" + key, message_user_map);

                rootref.updateChildren(user_map, (databaseError, databaseReference) -> {
                    String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                    String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;


                    HashMap sendermap = new HashMap<>();
                    sendermap.put("id", senderid);
                    sendermap.put("name", sender_name);
                    sendermap.put("msg", "Send an Image");
                    sendermap.put("pic", rider_pic);
                    sendermap.put("status", "0");
                    sendermap.put("order_id", order_id);
                    sendermap.put("type", "rider");
                    sendermap.put("timestamp", -1 * System.currentTimeMillis());
                    sendermap.put("date", formattedDate);
                    sendermap.put("store_id", Receiverid);

                    HashMap receivermap = new HashMap<>();
                    receivermap.put("id", Receiverid);
                    receivermap.put("name", Receiver_name);
                    receivermap.put("msg", "Send an Image");
                    receivermap.put("pic", Receiver_pic);
                    receivermap.put("status", "1");
                    receivermap.put("type", "rider");
                    receivermap.put("order_id", order_id);
                    receivermap.put("timestamp", -1 * System.currentTimeMillis());
                    receivermap.put("date", formattedDate);
                    receivermap.put("store_id", Receiverid);

                    HashMap both_user_map = new HashMap<>();
                    both_user_map.put(inbox_sender_ref, receivermap);
                    both_user_map.put(inbox_receiver_ref, sendermap);

                    Chat_Activity.SendPushNotification(getActivity(), Receiver_name, "Send an Image....",
                            Receiverid, senderid, order_id);

                });


            });


        });
    }


    // this is the delete message diloge which will show after long press in chat message
    private void delete_Message_dialog(final Chat_GetSet chat_getSet) {
        final CharSequence[] options;
        if (chat_getSet.getType().equals("text")) {
            options = new CharSequence[]{"Copy", "Delete this message", "Cancel"};
        } else {

            options = new CharSequence[]{"Delete this message", "Cancel"};
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete this message")) {
                    update_message(chat_getSet);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                } else if (options[item].equals("Copy")) {

                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", chat_getSet.getText());
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        builder.show();
    }


    // we will update the privious message means we will tells the other user that we have seen your message
    public void update_message(Chat_GetSet item) {
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid + "-" + order_id;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid + "-" + order_id;


        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", item.getReceiver_id());
        message_user_map.put("sender_id", item.getSender_id());
        message_user_map.put("chat_id", item.getChat_id());
        message_user_map.put("text", "Delete this message");
        message_user_map.put("type", "delete");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", sender_name);
        message_user_map.put("timestamp", item.getTimestamp());

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + item.getChat_id(), message_user_map);
        user_map.put(chat_user_ref + "/" + item.getChat_id(), message_user_map);

        rootref.updateChildren(user_map);

    }


    // this method will show the dialog of selete the either take a picture form camera or pick the image from gallary
    private void selectfile() {

        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.chose_from_gallery), getString(R.string.cancel_txt)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle("Select");

        builder.setItems(options, (dialog, item) -> {


            if (options[item].equals(getString(R.string.take_photo))) {
                if (check_camrapermission())
                    openCameraIntent();
            } else if (options[item].equals(getString(R.string.chose_from_gallery))) {
                if (check_ReadStoragepermission()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            } else if (options[item].equals(getString(R.string.cancel_txt))) {

                dialog.dismiss();
            }

        });

        builder.show();
    }

    // below tis the four types of permission
    //get the permission to record audio
    public boolean check_Recordpermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    Variables.PERMISSION_RECORDING_AUDIO);
        }
        return false;
    }

    private boolean check_camrapermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA}, Variables.PERMISSION_CAMERA_CODE);
        }
        return false;
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Variables.PERMISSION_READ_DATA);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private boolean check_writeStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Variables.PERMISSION_WRITE_DATA);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Variables.PERMISSION_CAMERA_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == Variables.PERMISSION_READ_DATA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Tap again", Toast.LENGTH_SHORT).show();

        }

        if (requestCode == Variables.PERMISSION_WRITE_DATA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Tap Again", Toast.LENGTH_SHORT).show();

        }


        if (requestCode == Variables.PERMISSION_RECORDING_AUDIO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Tap Again", Toast.LENGTH_SHORT).show();

        }
    }

    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, 1);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    //on image select activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Matrix matrix = Functions.Get_orentation(imageFilePath);
                Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                String path = getPath(selectedImage);

                Matrix matrix = Functions.Get_orentation(path);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);
            }
        }
    }

    // send the type indicator if the user is typing message
    public void SendTypingIndicator(boolean indicate) {
        // if the type incator is present then we remove it if not then we create the typing indicator
        if (indicate) {
            final HashMap message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", Receiverid);
            message_user_map.put("sender_id", senderid);
            message_user_map.put("order_id", order_id);

            send_typing_indication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
            send_typing_indication.child(senderid + "-" + Receiverid + "-"+order_id).setValue(message_user_map).addOnSuccessListener(aVoid -> {

                send_typing_indication.child(Receiverid + "-" + senderid + "-"+order_id).setValue(message_user_map);

            });
        } else {
            send_typing_indication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");

            send_typing_indication.child(senderid + "-" + Receiverid + "-"+order_id).removeValue().addOnCompleteListener(task -> {

                send_typing_indication.child(Receiverid + "-" + senderid + "-"+order_id).removeValue();


            });
        }
    }

    // receive the type indication to show that your friend is typing or not
    LinearLayout mainlayout;

    public void ReceivetypeIndication() {
        mainlayout = view.findViewById(R.id.typeindicator);

        receive_typing_indication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
        receive_typing_indication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Receiverid + "-" + senderid + "-"+order_id).exists()) {
                    String receiver = String.valueOf(dataSnapshot.child(Receiverid + "-" + senderid + "-"+order_id).child("sender_id").getValue());
                    if (receiver.equals(Receiverid)) {
                        mainlayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // if there is some kind of problem in getting this data this method will be call

            }
        });
    }

    // on destory delete the typing indicator
    @Override
    public void onDestroy() {
        super.onDestroy();

        uploading_image_id = "none";
        Variables.Opened_Chat_id = "null";
        SendTypingIndicator(false);
        query_getchat.removeEventListener(eventListener);
        Variables.userDetails_pref.edit().putString(Variables.chat_reciever_id, null).apply();
    }

    // on stop delete the typing indicator and remove the value event listener
    @Override
    public void onStop() {
        super.onStop();
        SendTypingIndicator(false);
        query_getchat.removeEventListener(eventListener);
        Variables.userDetails_pref.edit().putString(Variables.chat_reciever_id, null).apply();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Variables.userDetails_pref.edit().putString(Variables.chat_reciever_id, null).apply();
    }

    //this method will get the big size of image in private chat
    public void OpenfullsizeImage(Chat_GetSet item) {
        Preview_F see_image_f = new Preview_F();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", item.getPic_url());
        args.putSerializable("chat_id", item.getChat_id());
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, see_image_f).commit();
    }


    //this method will get the big size of image in private chat
    public void OpenAudio(String path) {

        Play_Audio_F play_audio_f = new Play_Audio_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("path", path);
        play_audio_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, play_audio_f).commit();

    }

    public void Play_audio(int postion, Chat_GetSet item) {

        audio_postion = postion;
        media_player_progress = 0;

        Stop_playing();

        File fullpath = new File(Variables.folder_freshly_rider + item.chat_id + ".mp3");
        if (fullpath.exists()) {
            Uri uri = Uri.parse(fullpath.getAbsolutePath());

            mediaplayer = MediaPlayer.create(context, uri);

            if (mediaplayer != null) {
                mediaplayer.start();
                Countdown_timer(true);

                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Stop_playing();
                    }
                });
                playing_id = item.chat_id;
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    public void Stop_playing() {
        playing_id = "none";
        Countdown_timer(false);
        mAdapter.notifyDataSetChanged();
        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
            mediaplayer = null;
        }
    }


    CountDownTimer countDownTimer;

    public void Countdown_timer(boolean starttimer) {

        if (countDownTimer != null)
            countDownTimer.cancel();


        if (starttimer) {
            countDownTimer = new CountDownTimer(mediaplayer.getDuration(), 300) {
                @Override
                public void onTick(long millisUntilFinished) {

                    media_player_progress = ((mediaplayer.getCurrentPosition() * 100) / mediaplayer.getDuration());
                    if (media_player_progress > 95) {
                        Countdown_timer(false);
                        media_player_progress = 0;
                    }
                    mAdapter.notifyItemChanged(audio_postion);
                }

                @Override
                public void onFinish() {
                    media_player_progress = 0;
                    Countdown_timer(false);
                    mAdapter.notifyItemChanged(audio_postion);
                }
            };
            countDownTimer.start();


        }

    }

    public void download_audio(final ProgressBar p_bar, Chat_GetSet item) {
        p_bar.setVisibility(View.VISIBLE);
        PRDownloader.download(item.getPic_url(), direct.getPath(), item.getChat_id() + ".mp3")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        p_bar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
    }


    // this mehtos the will add a node of notification in to database
    // then our firebase cloud function will listen node and send the notification to spacific user
    public static void SendPushNotification(Activity context, String name, String message, String receiverid, String senderid, String order_id) {
        JSONObject notimap = new JSONObject();
        try {
            notimap.put("title", name);
            notimap.put("message", message);
            notimap.put("sender_id", senderid);
            notimap.put("receiver_id", receiverid);
            if (order_id != null && !order_id.equals("")) {
                notimap.put("order_id", order_id);
            }
            notimap.put("type", "rider");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Api_urls.SEND_MESSAGE_NOTIFICATION, notimap, new Callback() {
            @Override
            public void Responce(String resp) {

            }
        });
    }
}

