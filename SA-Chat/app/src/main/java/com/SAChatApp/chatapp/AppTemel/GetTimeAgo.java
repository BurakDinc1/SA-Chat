package com.SAChatApp.chatapp.AppTemel;

import android.annotation.SuppressLint;
import android.app.Application;

@SuppressLint("Registered")
public class GetTimeAgo extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Şimdi";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Bir dakika önce";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " dakika önce";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Bir saat önce";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " saat önce";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Dün";
        } else {
            if(diff / DAY_MILLIS < 7){
                return diff / DAY_MILLIS + " gün önce";
            }else if(diff / DAY_MILLIS >= 7 && diff / DAY_MILLIS < 30){
                return  (diff / DAY_MILLIS) / 7 +" hafta önce";
            }else if(diff / DAY_MILLIS >= 30 && diff / DAY_MILLIS < 365){
                return  (diff / DAY_MILLIS) / 30 +" ay önce";
            }else{
                return  (diff / DAY_MILLIS) / 365 +" yıl önce";
            }
        }
    }
}
