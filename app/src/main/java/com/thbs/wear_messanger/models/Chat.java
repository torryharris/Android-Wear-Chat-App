package com.thbs.wear_messanger.models;

/**
 * Created by divya_ravikumar on 12/13/2017.
 */

public class Chat {

    private String message;
    private boolean isSender;
    private String date;
    private String time;

    public boolean isDateSame() {
        return isDateSame;
    }

    public void setDateSame(boolean dateSame) {
        isDateSame = dateSame;
    }

    private boolean isDateSame;


    public Chat(String message, boolean isSender, String date, String time, Boolean isDateSame) {
        this.message = message;
        this.isSender = isSender;
        this.date = date;
        this.time = time;
        this.isDateSame = isDateSame;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Chat() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                ", isSender=" + isSender +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", isDateSame=" + isDateSame +
                '}';
    }
}

