package com.SAChatApp.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.UUID;

public class BizeUlasinActivity extends AppCompatActivity {

    private EditText send_gorusoneri;
    private Button btn_gonderoneri;
    private ImageButton gorusoneriback;

    private void init(){
        gorusoneriback = findViewById(R.id.gorusoneriback);
        btn_gonderoneri = findViewById(R.id.btn_gonderoneri);
        send_gorusoneri = findViewById(R.id.send_gorusoneri);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bize_ulasin);

        init();

        gorusoneriback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BizeUlasinActivity.this.onBackPressed();
            }
        });

        btn_gonderoneri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(send_gorusoneri.getText())){
                    String uuid = UUID.randomUUID().toString();
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("bildirim",send_gorusoneri.getText().toString());
                    hashMap.put("uuid",uuid);

                    MainActivity.vb.getDbref("BizeUlasin")
                            .child(MainActivity.vb.getUserID())
                            .child(uuid)
                            .setValue(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(BizeUlasinActivity.this,"Bildiriminiz alındı !",Toast.LENGTH_SHORT).show();
                                        BizeUlasinActivity.this.onBackPressed();
                                    }else{
                                        Toast.makeText(BizeUlasinActivity.this,"Bildirim alınamadı !",Toast.LENGTH_SHORT).show();
                                        BizeUlasinActivity.this.onBackPressed();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(BizeUlasinActivity.this,"Boş bırakılamaz !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}