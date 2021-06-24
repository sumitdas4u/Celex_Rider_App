package com.celex.rider.DataModels;

public class DocumentHomeModel {

    public String extension;
    public String file_name;
    public String file_size;
    public String verfy;
    public String user_name;
    public String link;

    public String  attachment;
    public String  type;
    public String  status;




    public DocumentHomeModel(String extension, String file_name, String file_size, String user_name, String link,String verfy) {
        this.extension = extension;
        this.file_name = file_name;
        this.file_size = file_size;
        this.user_name = user_name;
        this.link = link;
        this.verfy = verfy;
    }

    public DocumentHomeModel() {

    }






}
