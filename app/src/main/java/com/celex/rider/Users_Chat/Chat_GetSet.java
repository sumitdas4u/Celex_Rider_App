package com.celex.rider.Users_Chat;

import java.io.Serializable;


public class Chat_GetSet implements Serializable {

  public String
          receiver_id,
          sender_id,
          chat_id,
          sender_name,
          text,
          pic_url,
          status,
          time,
          timestamp,
          type;

   public Chat_GetSet() {

   }

   public String getReceiver_id() {
    return receiver_id;
  }


   public String getSender_id() {
    return sender_id;
  }


   public String getChat_id() {
    return chat_id;
  }


   public String getSender_name() {
    return sender_name;
  }


   public String getTimestamp() {
    return timestamp;
  }

   public String getText() {
    return text;
  }

   public void setText(String text) {
    this.text = text;
  }

   public String getStatus() {
    return status;
  }

   public String getTime() {
    return time;
  }


   public String getType() {
    return type;
  }

   public void setType(String type) {
    this.type = type;
  }

   public String getPic_url() {
    return pic_url;
  }


}
