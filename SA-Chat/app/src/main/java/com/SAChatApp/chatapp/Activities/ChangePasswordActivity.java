package com.SAChatApp.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton change_password_back_btn;
    private MaterialEditText new_password_one, new_password_two , new_password_email, new_password_eskipassword;
    private Button change_password_btn;
    private Dialog progressDialog;

    private void init(){
        change_password_back_btn = findViewById(R.id.changepasswordback);
        new_password_one = findViewById(R.id.new_password_one);
        new_password_two=findViewById(R.id.new_password_two);
        change_password_btn=findViewById(R.id.btn_change_password);
        new_password_email = findViewById(R.id.new_password_email);
        new_password_eskipassword = findViewById(R.id.new_password_eskipassword);
    }

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void DialogBaslat() {
        progressDialog = new Dialog(ChangePasswordActivity.this);
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
        setContentView(R.layout.activity_change_password);

        init();

        change_password_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordActivity.this.onBackPressed();
            }
        });

        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetBaglantiKontrol()){
                    if(TextUtils.isEmpty(Objects.requireNonNull(new_password_one.getText()).toString()) ||
                            TextUtils.isEmpty(Objects.requireNonNull(new_password_two.getText()).toString()) ||
                            TextUtils.isEmpty(Objects.requireNonNull(new_password_email.getText()).toString()) ||
                            TextUtils.isEmpty(Objects.requireNonNull(new_password_eskipassword.getText()).toString())){
                        Toast.makeText(ChangePasswordActivity.this,"Şifre alanları boş bırakılamaz !",Toast.LENGTH_SHORT).show();
                    }else{
                        if(TextUtils.equals(new_password_one.getText().toString(), new_password_two.getText().toString())){
                            DialogBaslat();

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(new_password_email.getText().toString(), new_password_eskipassword.getText().toString());

                            MainActivity.vb.getCurrentuser().reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                MainActivity.vb.getCurrentuser()
                                                        .updatePassword(new_password_one.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            MainActivity.vb.getDbref("Users")
                                                                    .child(MainActivity.vb.getUserID())
                                                                    .child("password")
                                                                    .setValue(new_password_one.getText().toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                                Toast.makeText(ChangePasswordActivity.this,"Şifreniz başarılı bir şekilde değiştirildi.",Toast.LENGTH_SHORT).show();
                                                                                ChangePasswordActivity.this.onBackPressed();
                                                                            }else{
                                                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                                Toast.makeText(ChangePasswordActivity.this,"Şifreniz başarılı bir şekilde değiştirildi.",Toast.LENGTH_SHORT).show();
                                                                                ChangePasswordActivity.this.onBackPressed();
                                                                            }
                                                                        }
                                                                    });


                                                        }else{
                                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(ChangePasswordActivity.this,"Bir hata oluştu ! Lütfen daha sonra tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(ChangePasswordActivity.this,"Giriş bilgilerini kontrol et !",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }else{
                            Toast.makeText(ChangePasswordActivity.this,"Şifreler uyuşmuyor !",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(ChangePasswordActivity.this, "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        super.onBackPressed();
    }
}