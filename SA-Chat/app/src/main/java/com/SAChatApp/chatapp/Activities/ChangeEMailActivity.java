package com.SAChatApp.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

public class ChangeEMailActivity extends AppCompatActivity {

    private ImageButton resetemailback;
    private EditText resetemail_yeniemail,resetemail_email,resetemail_sifre;
    private Button btn_reset_email;
    private Dialog progressDialog;

    private void init(){
        resetemailback = findViewById(R.id.resetemailback);
        resetemail_yeniemail = findViewById(R.id.resetemail_yeniemail);
        btn_reset_email = findViewById(R.id.btn_reset_email);
        resetemail_email = findViewById(R.id.resetemail_email);
        resetemail_sifre = findViewById(R.id.resetemail_sifre);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void DialogBaslat() {
        progressDialog = new Dialog(ChangeEMailActivity.this);
        progressDialog.setContentView(R.layout.indicator_progress);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        RotateLoading rotateLoading = progressDialog.findViewById(R.id.rotateloading);
        rotateLoading.start();
        TextView progress_text = progressDialog.findViewById(R.id.progress_text);
        progressDialog.setCanceledOnTouchOutside(false);
        String girisypl = "Lütfen Bekleyiniz...";
        progress_text.setText(girisypl);
        progressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_e_mail);

        init();

        resetemailback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEMailActivity.this.onBackPressed();
            }
        });

        btn_reset_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(resetemail_yeniemail.getText()) || !TextUtils.isEmpty(resetemail_email.getText()) || !TextUtils.isEmpty(resetemail_sifre.getText())){

                    DialogBaslat();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(resetemail_email.getText().toString(), resetemail_sifre.getText().toString());

                    MainActivity.vb.getCurrentuser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        MainActivity.vb.getCurrentuser()
                                                .updateEmail(resetemail_yeniemail.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            MainActivity.vb.getDbref("Users")
                                                                    .child(MainActivity.vb.getUserID())
                                                                    .child("email")
                                                                    .setValue(resetemail_yeniemail.getText().toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                                Toast.makeText(ChangeEMailActivity.this,"Başarılı !",Toast.LENGTH_SHORT).show();
                                                                                ChangeEMailActivity.this.onBackPressed();
                                                                            }else{
                                                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                                Toast.makeText(ChangeEMailActivity.this,"Bir sorun oluştu !",Toast.LENGTH_SHORT).show();
                                                                                ChangeEMailActivity.this.onBackPressed();
                                                                            }
                                                                        }
                                                                    });

                                                        }else{
                                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(ChangeEMailActivity.this,"Başarısız !",Toast.LENGTH_SHORT).show();
                                                            ChangeEMailActivity.this.onBackPressed();
                                                        }
                                                    }
                                                });
                                    }else{
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(ChangeEMailActivity.this,"Giriş bilgilerinizi kontrol edin !",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }else{
                    Toast.makeText(ChangeEMailActivity.this,"Boş alan bırakılamaz !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}