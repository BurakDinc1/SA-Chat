package com.SAChatApp.chatapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.SAChatApp.chatapp.Adapter.SohbetlerAdapter;
import com.SAChatApp.chatapp.Activities.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.SAChatApp.chatapp.Model.User;
import com.SAChatApp.chatapp.Notifications.Token;
import com.SAChatApp.chatapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SohbetlerAdapter sohbetadapter;
    private List<User> mesajlar=new ArrayList<>();
    private LinearLayout followfriends;

    private void init(View view){
        recyclerView = view.findViewById(R.id.recycler_view);
        followfriends = view.findViewById(R.id.followfriends);
        LinearLayoutManager sohbetmanager = new LinearLayoutManager(getContext());
        sohbetadapter=new SohbetlerAdapter(mesajlar,getContext(),getActivity());
        if(recyclerView!=null){
            recyclerView.setAdapter(sohbetadapter);
            recyclerView.setLayoutManager(sohbetmanager);
        }
    }

    private void SohbetleriCek() {
        MainActivity.vb.getDbref("Mesajlar")
                .child(MainActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mesajlar.clear();
                        if (dataSnapshot.exists()) {
                            recyclerView.setVisibility(View.VISIBLE);
                            followfriends.setVisibility(View.GONE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uid = Objects.requireNonNull(snapshot.getKey());
                                MainActivity.vb.getDbref("Users")
                                        .child(uid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                User e = dataSnapshot2.getValue(User.class);
                                                mesajlar.add(e);
                                                sohbetadapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            followfriends.setVisibility(View.VISIBLE);
                            sohbetadapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        init(view);

        SohbetleriCek();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(MainActivity.vb.getUserID()).setValue(token1);
    }


}
