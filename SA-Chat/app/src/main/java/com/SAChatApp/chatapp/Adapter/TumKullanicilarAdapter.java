package com.SAChatApp.chatapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.Activities.MainActivity;
import com.SAChatApp.chatapp.Activities.MessageActivity;
import com.SAChatApp.chatapp.Model.User;
import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TumKullanicilarAdapter extends RecyclerView.Adapter<TumKullanicilarAdapter.TumKullanicilarViewHolder> {

    private List<User> kullanicilar;
    private Context tiklama_icin;
    private Activity activity;

    public void addAll(List<User> degisenList){
        this.kullanicilar.clear();
        this.kullanicilar.addAll(degisenList);
        this.notifyDataSetChanged();
    }

    public TumKullanicilarAdapter(List<User> kullanicilar,Context tiklama_icin, Activity activity) {
        this.kullanicilar = kullanicilar;
        this.tiklama_icin = tiklama_icin;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TumKullanicilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
        return new TumKullanicilarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TumKullanicilarViewHolder holder, final int position) {
        final User a = kullanicilar.get(position);
        holder.kullanicadi.setText(a.getUsername());
        holder.durumu.setText(a.getDurum());

        MainActivity.vb.getDbref("ArkadasListesi")
                .child(MainActivity.vb.getUserID())
                .child(a.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.user_single_delete.setVisibility(View.VISIBLE);
                        }else{
                            holder.user_single_delete.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (a.getImageURL().equals("default")) {
            holder.profilresmi.setImageResource(R.drawable.ic_default_userimage);
        } else {
            Picasso.get().load(a.getImageURL()).into(holder.profilresmi);
        }

        holder.tumkullanicilarview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goprofile = new Intent(activity, MessageActivity.class);
                goprofile.putExtra("userid", a.getId());
                activity.startActivity(goprofile);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        holder.user_single_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.vb.getDbref("ArkadasListesi")
                        .child(MainActivity.vb.getUserID())
                        .child(a.getId())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(activity,a.getUsername()+" Silindi !",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(activity,"Silinemedi !",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return kullanicilar.size();
    }

    class TumKullanicilarViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilresmi;
        private TextView kullanicadi, durumu;
        private LinearLayout tumkullanicilarview;
        private ImageButton user_single_delete;

        private TumKullanicilarViewHolder(@NonNull View itemView) {
            super(itemView);
            profilresmi = itemView.findViewById(R.id.cycimage);
            kullanicadi = itemView.findViewById(R.id.cycusername);
            durumu = itemView.findViewById(R.id.cycstatus);
            user_single_delete = itemView.findViewById(R.id.user_single_delete);
            tumkullanicilarview = itemView.findViewById(R.id.tumkullanicilarcardview);
        }
    }

}
