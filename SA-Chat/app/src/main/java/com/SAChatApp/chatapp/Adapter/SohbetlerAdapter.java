package com.SAChatApp.chatapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SAChatApp.chatapp.AppTemel.GetTimeAgo;
import com.SAChatApp.chatapp.Activities.MainActivity;
import com.SAChatApp.chatapp.Activities.MessageActivity;
import com.SAChatApp.chatapp.Model.Mesaj;
import com.SAChatApp.chatapp.Model.User;
import com.SAChatApp.chatapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SohbetlerAdapter extends RecyclerView.Adapter<SohbetlerAdapter.SohbetlerViewHolder> {

    private List<User> mesajlar;
    private Context context;
    private Activity activity;

    public SohbetlerAdapter(List<User> mesajlar, Context context, Activity activity) {
        this.mesajlar = mesajlar;
        this.context = context;
        this.activity = activity;
    }

    public void addAll(List<User> mesajlar){
        this.mesajlar = mesajlar;
    }

    @NonNull
    @Override
    public SohbetlerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_sohbetler_layout, parent, false);
        return new SohbetlerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SohbetlerViewHolder holder, final int position) {

        final User e = mesajlar.get(position);

        MainActivity.vb.getDbref("Mesajlar")
                .child(MainActivity.vb.getUserID())
                .child(e.getId())
                .limitToLast(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String atama = Objects.requireNonNull(dataSnapshot.child("mesaj").getValue()).toString();
                        long tarih = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("tarih").getValue()).toString());
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        String duzenleme = getTimeAgo.getTimeAgo(tarih);
                        String kimdense = Objects.requireNonNull(dataSnapshot.child("kimden").getValue()).toString();
                        String type = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();

                        holder.tarihi.setText(duzenleme);

                        if (kimdense.equals(MainActivity.vb.getUserID())) {
                            if(type.equals("mesaj")){
                                String atm = "Siz: " + atama;
                                holder.sonmesaj.setText(atm);
                                holder.sonmesaj.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            }
                        } else {
                            if(type.equals("mesaj")){
                                holder.sonmesaj.setText(atama);
                                holder.sonmesaj.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        MainActivity.vb.getDbref("Mesajlar")
                .child(e.getId())
                .child(MainActivity.vb.getUserID())
                .orderByChild("kimden").equalTo(e.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long sayi = 0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Mesaj mesaj = snapshot.getValue(Mesaj.class);
                                if (mesaj != null && !mesaj.getGorulme()) {
                                    sayi++;
                                }
                            }
                            if (sayi == 0) {
                                holder.gorulmeyen.setVisibility(View.GONE);
                            } else {
                                holder.gorulmeyen.setVisibility(View.VISIBLE);
                                String sayisi = String.valueOf(sayi);
                                holder.gorulmeyen.setText(sayisi);
                            }
                        } else {
                            holder.gorulmeyen.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.kullanicadi.setText(e.getUsername());

        if(e.getStatus().equals("online")){
            holder.shbt_onoff.setBackgroundResource(R.drawable.cember_yesil);
        }else{
            holder.shbt_onoff.setBackgroundResource(R.drawable.cember_gri);
        }

        if (e.getImageURL().equals("default")) {
            holder.profilresmi.setImageResource(R.drawable.ic_default_userimage);
        } else {
            Picasso.get().load(e.getImageURL()).into(holder.profilresmi);
        }

        holder.sohbetlercarview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go=new Intent(activity, MessageActivity.class);
                go.putExtra("userid",e.getId());
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mesajlar.size();
    }

    class SohbetlerViewHolder extends RecyclerView.ViewHolder  {
        CircleImageView profilresmi, shbt_onoff;
        TextView kullanicadi, sonmesaj, tarihi, gorulmeyen;
        RelativeLayout sohbetlercarview ;

        SohbetlerViewHolder(@NonNull View itemView) {
            super(itemView);
            profilresmi = itemView.findViewById(R.id.msjcycimage);
            kullanicadi = itemView.findViewById(R.id.msjcycusername);
            sonmesaj = itemView.findViewById(R.id.msjcycsonmesaj);
            tarihi = itemView.findViewById(R.id.msjcyctarih);
            gorulmeyen = itemView.findViewById(R.id.msjcycgorulmeyen);
            sohbetlercarview=itemView.findViewById(R.id.sohbetlercardview);
            shbt_onoff=itemView.findViewById(R.id.shbt_onoff);
        }
    }
}
