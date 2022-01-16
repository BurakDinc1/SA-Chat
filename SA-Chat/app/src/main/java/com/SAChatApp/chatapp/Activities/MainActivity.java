package com.SAChatApp.chatapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.SAChatApp.chatapp.R;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.SAChatApp.chatapp.VeriTabani.VeriTabani;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.SAChatApp.chatapp.Fragments.ChatsFragment;
import com.SAChatApp.chatapp.Fragments.ProfileFragment;
import com.SAChatApp.chatapp.Fragments.UsersFragment;
import com.SAChatApp.chatapp.Model.User;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private Toolbar toolbar;
    private FloatingActionButton actionButton;
    private ImageView exit_icon, big_menu_icon,goDNC_icon;
    private SubActionButton.Builder itemBuilder;
    private SubActionButton exit_button, goDNC_button;

    public static VeriTabani vb = VeriTabani.getVeritabani();

    private void StateControle(){
        if(vb.getCurrentuser() == null){
            Intent goWelcome=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goWelcome);
            finish();
        }
    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
    }

    private void menuButonTasarla(){
        big_menu_icon = new ImageView(this); // Create an icon
        big_menu_icon.setImageDrawable( getResources().getDrawable(R.drawable.ic_menu) );

        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(big_menu_icon)
                .setBackgroundDrawable(R.drawable.round_button)
                .build();

        itemBuilder = new SubActionButton.Builder(this);
        exit_icon = new ImageView(this);
        exit_icon.setImageDrawable( getResources().getDrawable(R.drawable.ic_exit) );
        exit_button = itemBuilder.setContentView(exit_icon).setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button)).build();

        goDNC_icon= new ImageView(this);
        goDNC_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_godnc));
        goDNC_button = itemBuilder.setContentView(goDNC_icon).setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button)).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(exit_button)
                .addSubActionView(goDNC_button)
                .attachTo(actionButton)
                .build();

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status("offline");
                vb.getAuth().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
            }
        });

        goDNC_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link="https://play.google.com/store/apps/dev?id=8849801098676486257";
                Uri uri=Uri.parse(link);
                Intent intent =new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        StateControle();

        menuButonTasarla();

        vb.getDbref("Users").child(vb.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.ic_default_userimage);
                } else {
                    Picasso.get().load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        vb.getDbref("Mesajlar").child(MainActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                /*for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Mesaj chat = snapshot.getValue(Mesaj.class);
                    if (!chat.getKimden().equals(vb.getUserID()) && !chat.getGorulme()){
                        unread++;
                    }
                }*/

                if (unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Sohbetler");
                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "("+unread+") Sohbetler");
                }

                viewPagerAdapter.addFragment(new UsersFragment(), "Kullanıcılar");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profil");

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status){

        /*HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        vb.getDbref("Users").child(vb.getUserID()).updateChildren(hashMap);*/

        vb.getDbref("Users").child(vb.getUserID()).child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        MainActivity.vb.getCurrentuser().reload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
