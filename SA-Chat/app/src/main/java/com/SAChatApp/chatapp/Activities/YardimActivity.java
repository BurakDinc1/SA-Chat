package com.SAChatApp.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.SAChatApp.chatapp.R;

public class YardimActivity extends AppCompatActivity {

    private ImageButton yardimactivityback;

    private void init(){
        yardimactivityback = findViewById(R.id.yardimactivityback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yardim);

        init();

        yardimactivityback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YardimActivity.this.onBackPressed();
            }
        });
    }
}