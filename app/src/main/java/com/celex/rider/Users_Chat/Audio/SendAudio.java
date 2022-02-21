package com.celex.rider.Users_Chat.Audio;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;

import com.celex.rider.Users_Chat.Chat_Activity;
import com.celex.rider.CodeClasses.Variables;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SendAudio {


    DatabaseReference rootref;
    String senderid = "";
    String Receiverid = "";
    String Receiver_name = "";
    String Receiver_pic = "null";
    String sender_name = "null";
    String order_id = "null";
    String token;
    Context context;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private DatabaseReference Adduser_to_inbox;
    EditText message_field;


    final String timestamp = "timestamp";
    final String status = "status";
    final String name_f = "name_f";
    final String message = "msg";


    public SendAudio(Context context, EditText message_field,
                     DatabaseReference rootref, DatabaseReference adduser_to_inbox, String sender_name, String senderid,
                     String receiverid, String receiver_name, String receiver_pic, String order_id
            , String token) {

        this.context = context;
        this.message_field = message_field;
        this.rootref = rootref;
        this.Adduser_to_inbox = adduser_to_inbox;
        this.senderid = senderid;
        this.Receiverid = receiverid;
        this.Receiver_name = receiver_name;
        this.Receiver_pic = receiver_pic;
        this.order_id = order_id;
        this.token = token;
        this.sender_name = sender_name;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";

    }


    public void startRecording() {

        try {


            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            mRecorder = new MediaRecorder();

            if (mRecorder != null)
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            if (mRecorder != null)
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            if (mRecorder != null)
                mRecorder.setOutputFile(mFileName);

            if (mRecorder != null)
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                if (mRecorder != null)
                    mRecorder.prepare();
            } catch (IOException e) {
                Log.e("resp", "prepare() failed");
            }
            if (mRecorder != null)
                mRecorder.start();

        } catch (Exception e) {

        }
    }


    public void stopRecording() {
        try {


            stop_timer_without_recoder();
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                // Runbeep("stop");
                UploadAudio();
            }

        } catch (Exception e) {

        }
    }

    // this method will upload audio  in firebase database
    public void UploadAudio() {

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.DATE_FORMAT.format(c);

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        DatabaseReference dref = rootref.child("chat").child(senderid + "-" + Receiverid).push();
        final String key = dref.getKey();
        Chat_Activity.uploading_Audio_id = key;
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "audio");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", sender_name);
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);


        Uri uri = Uri.fromFile(new File(mFileName));

        final StorageReference filepath = reference.child("Audio").child(key + ".mp3");

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Chat_Activity.uploading_Audio_id = "none";
                        HashMap message_user_map = new HashMap<>();
                        message_user_map.put("receiver_id", Receiverid);
                        message_user_map.put("sender_id", senderid);
                        message_user_map.put("chat_id", key);
                        message_user_map.put("text", "");
                        message_user_map.put("type", "audio");
                        message_user_map.put("pic_url", uri.toString());
                        message_user_map.put("status", "0");
                        message_user_map.put("time", "");
                        message_user_map.put("sender_name", sender_name);
                        message_user_map.put("timestamp", formattedDate);

                        HashMap user_map = new HashMap<>();

                        user_map.put(current_user_ref + "/" + key, message_user_map);
                        user_map.put(chat_user_ref + "/" + key, message_user_map);

                        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                                String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

                                HashMap sendermap = new HashMap<>();
                                sendermap.put("id", senderid);
                                sendermap.put("name", sender_name);
                                sendermap.put("store_id", Receiverid);
                                sendermap.put("pic", Variables.userDetails_pref.getString(Variables.user_pic, ""));
                                sendermap.put("msg", "Send an Audio...");
                                sendermap.put("status", "0");
                                sendermap.put("date", formattedDate);
                                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                                sendermap.put("order_id", order_id);
                                sendermap.put("type", "rider");

                                HashMap receivermap = new HashMap<>();
                                receivermap.put("id", Receiverid);
                                receivermap.put("store_id", Receiverid);
                                receivermap.put("name", Receiver_name);
                                receivermap.put("pic", Receiver_pic);
                                receivermap.put("msg", "Send an Audio...");
                                receivermap.put("status", "1");
                                receivermap.put("date", formattedDate);
                                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                                receivermap.put("order_id", order_id);
                                receivermap.put("type", "rider");

                                HashMap both_user_map = new HashMap<>();
                                both_user_map.put(inbox_sender_ref, receivermap);
                                both_user_map.put(inbox_receiver_ref, sendermap);

                                Chat_Activity.SendPushNotification((Activity) context, Receiver_name, "Send an Audio...",
                                        Receiverid, senderid,order_id);
                                /*Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener((OnCompleteListener<Void>) task -> {

                                });
*/

                            }
                        });

                    }
                });
            }
        });

    }

    public void stop_timer() {

        try {


            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            message_field.setText(null);

        } catch (Exception e) {

        }
    }


    public void stop_timer_without_recoder() {

        try {

            message_field.setText(null);

        } catch (Exception e) {

        }
    }

}
