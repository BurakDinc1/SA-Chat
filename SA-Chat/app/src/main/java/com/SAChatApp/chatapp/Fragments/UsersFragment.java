package com.SAChatApp.chatapp.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.SAChatApp.chatapp.Adapter.TumKullanicilarAdapter;
import com.SAChatApp.chatapp.Activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.SAChatApp.chatapp.Model.User;
import com.SAChatApp.chatapp.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class UsersFragment extends Fragment {

    private RecyclerView listtumkullanicilar;
    private TumKullanicilarAdapter adapter;
    private List<User> tumkullanicilar = new ArrayList<>();
    private EditText search_users;
    private Context adapter_icin = getContext();
    private Button ekle_button;

    private void init(View view) {
        search_users = view.findViewById(R.id.search_users);
        ekle_button = view.findViewById(R.id.ekle_button);

        listtumkullanicilar = view.findViewById(R.id.recycler_view);
        listtumkullanicilar.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new TumKullanicilarAdapter(tumkullanicilar, adapter_icin, getActivity());
        if (listtumkullanicilar != null) {
            listtumkullanicilar.setAdapter(adapter);
            listtumkullanicilar.setLayoutManager(linearLayoutManager);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        init(view);

        getFriendsList();

        ekle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EkleArkadas();
            }
        });

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    getFriendsList();
                    ekle_button.setVisibility(View.GONE);
                } else {
                    searchUsers(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void EkleArkadas(){
        HashMap<String, Object> hashMap = new HashMap<>();
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        hashMap.put("tarih", timestamp.getTime());
        MainActivity.vb.getDbref("ArkadasListesi")
                .child(MainActivity.vb.getUserID())
                .child(search_users.getText().toString())
                .setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Kullanıcı eklendi !",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"Kullanıcı eklenemedi !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void searchUsers(String s) {
        MainActivity.vb.getDbref("Users")
                .child(s)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tumkullanicilar.clear();
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if(MainActivity.vb.getUserID().equals(Objects.requireNonNull(user).getId())){
                                Toast.makeText(getActivity(),"Kendi referans adresiniz ile arama yapıyorsunuz !",Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                ekle_button.setVisibility(View.GONE);
                            }else{
                                MainActivity.vb.getDbref("ArkadasListesi")
                                        .child(MainActivity.vb.getUserID())
                                        .child(s)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Toast.makeText(getActivity(),"Bu kullanıcı zaten ekli !",Toast.LENGTH_SHORT).show();
                                                    adapter.notifyDataSetChanged();
                                                    ekle_button.setVisibility(View.GONE);
                                                }else{
                                                    tumkullanicilar.add(user);
                                                    adapter.notifyDataSetChanged();
                                                    ekle_button.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }else{
                            adapter.notifyDataSetChanged();
                            ekle_button.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFriendsList(){
        MainActivity.vb.getDbref("ArkadasListesi")
                .child(MainActivity.vb.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tumkullanicilar.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                String arkadas_id = snapshot1.getKey();
                                MainActivity.vb.getDbref("Users")
                                        .child(Objects.requireNonNull(arkadas_id))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshotz) {
                                                if(snapshotz.exists()){
                                                    User user = snapshotz.getValue(User.class);
                                                    tumkullanicilar.add(user);
                                                    adapter.notifyDataSetChanged();
                                                }else{
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                            }
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
