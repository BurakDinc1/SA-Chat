package com.SAChatApp.chatapp.Model;

import com.SAChatApp.chatapp.Activities.MainActivity;

import java.sql.Timestamp;

public class Mesaj {
    private String mesaj;
    private long tarih;
    private String kimden;
    private boolean gorulme;
    private String type;

    public boolean getGorulme() {
        return gorulme;
    }

    public void setGorulme(boolean gorulme) {
        this.gorulme = gorulme;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public long getTarih() {
        return tarih;
    }

    public void setTarih(long tarih) {
        this.tarih = tarih;
    }

    public String getKimden() {
        return kimden;
    }

    public void setKimden(String kimden) {
        this.kimden = kimden;
    }

    public Mesaj() {
    }

    public Mesaj(String mesaj, String type) {
        this.mesaj = mesaj;
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        this.tarih = timestamp.getTime();
        this.kimden = MainActivity.vb.getUserID();
        this.gorulme=false;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
