package com.SAChatApp.chatapp.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.SAChatApp.chatapp.Activities.BizeUlasinActivity;
import com.SAChatApp.chatapp.Activities.ChangeEMailActivity;
import com.SAChatApp.chatapp.Activities.ChangePasswordActivity;
import com.SAChatApp.chatapp.Activities.MainActivity;
import com.SAChatApp.chatapp.Activities.YardimActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.SAChatApp.chatapp.Model.User;
import com.SAChatApp.chatapp.R;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private TextView username, userdurum, ref_adres;
    private EditText editting_username, editting_userdurum;
    private Button save_changes, change_password_f_p, ref_adres_paylas, reset_email_profile, dogrulama_gonder,profile_yardim_hata ,bizeulasin;
    private Context context;

    private DatabaseReference reference;
    private FirebaseUser fuser;

    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private Dialog progressDialog;

    private void DialogBaslat() {
        progressDialog = new Dialog(getActivity());
        progressDialog.setContentView(R.layout.indicator_progress);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        RotateLoading rotateLoading = progressDialog.findViewById(R.id.rotateloading);
        rotateLoading.start();
        TextView progress_text = progressDialog.findViewById(R.id.progress_text);
        progressDialog.setCanceledOnTouchOutside(false);
        String girisypl = "Değişiklikler kaydediliyor...";
        progress_text.setText(girisypl);
        progressDialog.show();
    }

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void init(View view) {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        context = getActivity();

        image_profile = view.findViewById(R.id.profile_image);
        ref_adres_paylas = view.findViewById(R.id.ref_adres_paylas);
        ref_adres = view.findViewById(R.id.editting_ref_adres);
        username = view.findViewById(R.id.username);
        reset_email_profile = view.findViewById(R.id.reset_email_profile);
        userdurum = view.findViewById(R.id.userdurum);
        editting_userdurum = view.findViewById(R.id.editting_userdurum);
        editting_username = view.findViewById(R.id.editting_username);
        bizeulasin = view.findViewById(R.id.bizeulasin);
        dogrulama_gonder = view.findViewById(R.id.dogrulama_gonder);
        save_changes = view.findViewById(R.id.save_changes);
        change_password_f_p = view.findViewById(R.id.change_password_f_p);
        profile_yardim_hata = view.findViewById(R.id.profile_yardim_hata);
    }

    private void getUserData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                username.setText(user.getUsername());
                userdurum.setText(user.getDurum());
                editting_userdurum.setText(user.getDurum());
                editting_username.setText(user.getUsername());
                ref_adres.setText(user.getId());
                if (user.getImageURL().equals("default")) {
                    image_profile.setImageResource(R.drawable.ic_default_userimage);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Picasso.get().load(user.getImageURL()).into(image_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void save_Changes(String username, String userdurum) {

        if (internetBaglantiKontrol()) {
            DialogBaslat();
            reference.child("username").setValue(username)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                reference.child("durum").setValue(userdurum)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(getActivity(), "Değişiklikler kaydedildi !", Toast.LENGTH_LONG).show();
                                                } else {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(getActivity(), "Durum güncellenemedi !", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                reference.child("durum").setValue(userdurum)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(getActivity(), "Kullanıcı adı güncellenemedi !", Toast.LENGTH_LONG).show();
                                                } else {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(getActivity(), "Değişiklikler kaydedilemedi !", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_LONG).show();
        }


    }

    private void ReferansAdresiPaylas() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ref_adres.getText().toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Referansı Gönder");
        startActivity(shareIntent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.vb.getCurrentuser().isEmailVerified()){
            dogrulama_gonder.setVisibility(View.GONE);
        }else{
            dogrulama_gonder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        getUserData();

        ref_adres_paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ref_adres.getText().toString())) {
                    Toast.makeText(getActivity(), "Şuanda referans adresinizi paylaşamıyorsunuz !", Toast.LENGTH_SHORT).show();
                } else {
                    ReferansAdresiPaylas();
                }
            }
        });

        bizeulasin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BizeUlasinActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        profile_yardim_hata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), YardimActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_Changes(editting_username.getText().toString(), editting_userdurum.getText().toString());
            }
        });

        change_password_f_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        reset_email_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeEMailActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        dogrulama_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.vb.getCurrentuser().sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "E-Mail adresinizi kontrol edin !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Doğrulama e-postası gönderilemedi !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }

    private void openImage() {
        if (internetBaglantiKontrol()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);
        } else {
            Toast.makeText(getActivity(), "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_LONG).show();
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", "" + mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Başarısız!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "Hiçbir resim seçilmedi !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Yükleniyor", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
