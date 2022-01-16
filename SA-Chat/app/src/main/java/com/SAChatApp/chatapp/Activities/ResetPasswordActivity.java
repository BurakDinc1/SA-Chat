package com.SAChatApp.chatapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText send_email;
    private Button btn_reset;
    private ImageButton resetpasswordback;
    private Dialog progressDialog;

    private void DialogBaslat() {
        progressDialog = new Dialog(ResetPasswordActivity.this);
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

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void init(){
        send_email = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);
        resetpasswordback = findViewById(R.id.resetpasswordback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        init();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(internetBaglantiKontrol()){
                    String email = send_email.getText().toString();

                    if (email.equals("")){
                        Toast.makeText(ResetPasswordActivity.this, "Lütfen geçerli bir e-mail adresi giriniz !", Toast.LENGTH_SHORT).show();
                    } else {
                        DialogBaslat();
                        MainActivity.vb.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(ResetPasswordActivity.this, "Lütfen e-posta adresini kontrol et !", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                } else {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(ResetPasswordActivity.this, "Bir hata oluştu ! Lütfen daha sonra tekrar dene.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }else{
                    Toast.makeText(ResetPasswordActivity.this, "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_LONG).show();
                }

            }
        });

        resetpasswordback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordActivity.this.onBackPressed();
            }
        });

    }
}
