package com.mdappsatrms.chatzz;

public class UserMessages {

    private String fromto, message,messageidtype,datetime, filename;

    public UserMessages(String fromto, String message, String messageidtype, String datetime, String filename) {
        this.fromto = fromto;
        this.message = message;
        this.messageidtype = messageidtype;
        this.datetime = datetime;
        this.filename = filename;
    }

    public UserMessages(){

    }

    public String getFromto() {
        return fromto;
    }

    public void setFromto(String fromto) {
        this.fromto = fromto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageidtype() {
        return messageidtype;
    }

    public void setMessageidtype(String messageidtype) {
        this.messageidtype = messageidtype;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }



}
