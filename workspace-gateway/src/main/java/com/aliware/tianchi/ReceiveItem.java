package com.aliware.tianchi;

import java.util.Date;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 19:26:36
 */
public class ReceiveItem {

    private String message;

    private Date date;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ReceiveItem(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        return "ReceiveItem{" +
                "message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
