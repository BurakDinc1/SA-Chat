package com.SAChatApp.chatapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.Adapter.MesajAdapter;
import com.SAChatApp.chatapp.Model.Mesaj;
import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.SAChatApp.chatapp.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private ImageButton btn_send;
    private EditText text_send;
    private Intent intent;
    private String userid;
    private Toolbar toolbar;
    boolean notify = false;
    private Query query;
    private ValueEventListener ab;
    private RecyclerView recyclerView;
    private List<Mesaj> mesajlar = new ArrayList<>();
    private MesajAdapter adapter;

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void MesajlariGetir() {
        MainActivity.vb.getDbref("Mesajlar")
                .child(MainActivity.vb.getUserID()).child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mesajlar.clear();
                        if (dataSnapshot.exists()) {
                            recyclerView.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Mesaj m = snapshot.getValue(Mesaj.class);
                                mesajlar.add(m);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        adapter = new MesajAdapter(mesajlar,this);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        //goruldu yapmak için gereksinimler
        query = MainActivity.vb.getDbref("Mesajlar")
                .child(userid)
                .child(MainActivity.vb.getUserID());

        ab = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Mesaj mesaj = snapshot.getValue(Mesaj.class);
                        if (mesaj != null && mesaj.getKimden().equals(userid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("gorulme", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //goruldu yapmak için gereksinimler

    }

    private void SendMessage() {

        if(internetBaglantiKontrol()){
            notify=true;
            if (!TextUtils.isEmpty(text_send.getText().toString())) {
                final String text = text_send.getText().toString();
                final Mesaj yenimesaj = new Mesaj(text,"mesaj");
                MainActivity.vb.getDbref("Mesajlar")
                        .child(MainActivity.vb.getUserID())
                        .child(userid)
                        .push()
                        .setValue(yenimesaj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.vb.getDbref("Mesajlar")
                                .child(userid)
                                .child(MainActivity.vb.getUserID())
                                .push()
                                .setValue(yenimesaj).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btn_send.setEnabled(true);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                text_send.setText(null);
                                btn_send.setEnabled(true);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btn_send.setEnabled(true);
                    }
                });
            } else {
                btn_send.setEnabled(true);
            }
        }else{
            Toast.makeText(MessageActivity.this, "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();

        KullaniciBilgiCek();

        MesajlariGetir();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

    }

    private void KullaniciBilgiCek(){
        MainActivity.vb.getDbref("Users")
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user.getStatus().equals("online")){
                            username.setText(user.getUsername()+" · Çevrimiçi");
                        }else{
                            username.setText(user.getUsername()+" · Çevrimdışı");
                        }

                        if (user.getImageURL().equals("default")) {
                            profile_image.setImageResource(R.drawable.ic_default_userimage);
                        } else {
                            Picasso.get().load(user.getImageURL()).into(profile_image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void GorulduYap() {
        query.addValueEventListener(ab);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GorulduYap();
    }

    private void status(String status) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        MainActivity.vb.getDbref("Users").child(MainActivity.vb.getUserID()).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        currentUser("none");
    }
}
