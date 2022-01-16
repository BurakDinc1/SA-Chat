package com.SAChatApp.chatapp.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SAChatApp.chatapp.AppTemel.GetTimeAgo;
import com.SAChatApp.chatapp.Activities.MainActivity;
import com.SAChatApp.chatapp.Model.Mesaj;
import com.SAChatApp.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Mesaj> mesajlist;
    private static int GIDEN_MESAJ = 1;
    private static int GIDEN_RESIM = 2;
    private static int GELEN_MESAJ = 3;
    private static int GELEN_RESIM = 4;
    private Context context;

    public MesajAdapter(List<Mesaj> mesajlist, Context context) {
        this.mesajlist = mesajlist;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == GIDEN_MESAJ) {
            view = LayoutInflater.from(context).inflate(R.layout.single_giden_mesaj_layout, viewGroup, false);
            return new GidenMesajViewHolder(view);
        } else if (viewType == GELEN_MESAJ) {
            view = LayoutInflater.from(context).inflate(R.layout.single_gelen_mesaj_layout, viewGroup, false);
            return new GelenMesajViewHolder(view);
        } else if (viewType == GIDEN_RESIM) {
            //view = LayoutInflater.from(context).inflate(R.layout.single_mesaj_giden_kombin_layout, viewGroup, false);
            //return new GidenKombinViewHolder(view);
        } else if (viewType == GELEN_RESIM) {
            //view = LayoutInflater.from(context).inflate(R.layout.single_mesaj_gelen_kombin_layout, viewGroup, false);
            //return new GelenKombinViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == GIDEN_MESAJ) {
            ((GidenMesajViewHolder) holder).setGidenMesajAyarla(mesajlist.get(position));
        } else if (getItemViewType(position) == GELEN_MESAJ) {
            ((GelenMesajViewHolder) holder).setGelenMesajAyarla(mesajlist.get(position));
        } else if (getItemViewType(position) == GIDEN_RESIM) {
            //((GidenKombinViewHolder) holder).setGidenKombinAyarla(mesajlist.get(position));
        } else if (getItemViewType(position) == GELEN_RESIM) {
            //((GelenKombinViewHolder) holder).setGelenKombinAyarla(mesajlist.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mesajlist.get(position).getKimden().equals(MainActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("mesaj")) {
            return GIDEN_MESAJ;
        }  else if (mesajlist.get(position).getKimden().equals(MainActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("resim")) {
            return GIDEN_RESIM;
        } else if (!mesajlist.get(position).getKimden().equals(MainActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("mesaj")) {
            return GELEN_MESAJ;
        } else if (!mesajlist.get(position).getKimden().equals(MainActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("resim")) {
            return GELEN_RESIM;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mesajlist.size();
    }

    private class GelenMesajViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView msjimage;
        private TextView msjtext, msjtarih;

        private GelenMesajViewHolder(@NonNull View itemView) {
            super(itemView);
            msjimage = itemView.findViewById(R.id.msjimage);
            msjtext = itemView.findViewById(R.id.msjtext);
            msjtarih = itemView.findViewById(R.id.msjtarih);
        }

        private void setGelenMesajAyarla(Mesaj mesaj) {
            msjtext.setText(mesaj.getMesaj());
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            msjtarih.setText(getTimeAgo.getTimeAgo(mesaj.getTarih()));
            MainActivity.vb.getDbref("Users")
                    .child(mesaj.getKimden())
                    .child("imageURL")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String profresmi = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                            if (profresmi.equals("default")) {
                                msjimage.setImageResource(R.drawable.ic_default_userimage);
                            } else {
                                Picasso.get().load(profresmi).into(msjimage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private class GidenMesajViewHolder extends RecyclerView.ViewHolder {
        private TextView msjtext, msjtarih;

        private GidenMesajViewHolder(@NonNull View itemView) {
            super(itemView);
            msjtext = itemView.findViewById(R.id.msjtext1);
            msjtarih = itemView.findViewById(R.id.msjtarih1);
        }

        private void setGidenMesajAyarla(Mesaj mesaj) {
            msjtext.setText(mesaj.getMesaj());
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            msjtarih.setText(getTimeAgo.getTimeAgo(mesaj.getTarih()));
            if (mesaj.getGorulme()) {
                msjtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cifttik_mavi, 0);
            } else {
                msjtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cifttik, 0);
            }
        }
    }





}
