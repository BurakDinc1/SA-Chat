package com.SAChatApp.chatapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button btn_login;
    TextView forgot_password, go_register;
    Dialog progressDialog;

    private void init(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);
        go_register=findViewById(R.id.go_register);
    }

    private void DialogBaslat() {
        progressDialog = new Dialog(LoginActivity.this);
        progressDialog.setContentView(R.layout.indicator_progress);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        RotateLoading rotateLoading = progressDialog.findViewById(R.id.rotateloading);
        rotateLoading.start();
        TextView progress_text = progressDialog.findViewById(R.id.progress_text);
        progressDialog.setCanceledOnTouchOutside(false);
        String girisypl = "Giriş Yapılıyor...";
        progress_text.setText(girisypl);
        progressDialog.show();
    }

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        go_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "All fileds are required", Toast.LENGTH_SHORT).show();
                } else {
                    if(internetBaglantiKontrol()){
                        DialogBaslat();
                        MainActivity.vb.getAuth().signInWithEmailAndPassword(txt_email, txt_password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                            finish();
                                        } else {
                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(LoginActivity.this, "Giriş bilgilerinizi kontrol ediniz !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(),"İnternet bağlantınızı kontrol edin !",Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
    }
}
