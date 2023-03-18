package com.mdappsatrms.chatzz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSetings;
    private EditText userName, userStatus;
    public CircleImageView userProfileImage;
    private String currentUserId;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private static final  int GalleryPick=1;
    private StorageReference userProfileImagesRef;
    private Switch AutoMediaDLSwitch;
    public int autoMedia=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth= FirebaseAuth.getInstance();
        currentUserId= auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        InitializeFields();

        updateAccountSetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        ReviewUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        AutoMediaDLSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    autoMedia=1;
                }
                else {
                    autoMedia=0;
                }

            }
        });



    }




    private void UpdateSettings() {
        progressBar.setVisibility(View.VISIBLE);
        String setUsername= userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUsername)){
            Toast.makeText(this, "Enter Valid Username", Toast.LENGTH_SHORT).show();
        }

        else
        {

            HashMap<String,Object> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUsername);
            profileMap.put("status",setStatus);

            reference.child("Users").child(currentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        SendUserToMain();

                    }
                    else{
                        String message= task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });


        }

        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("image"))
                {
                    String gettingUserImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(gettingUserImage).into(userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void InitializeFields() {

        updateAccountSetings= findViewById(R.id.update_settings_button);
        userName= findViewById(R.id.set_user_name);
        userStatus= findViewById(R.id.set_user_status);
        progressBar= findViewById(R.id.progressBar);
        userProfileImage= findViewById(R.id.set_profile_image);
        TextView detailId= findViewById(R.id.detailId);
        detailId.setText("Contact");
        AutoMediaDLSwitch= findViewById(R.id.autoMediaSwitch);

    }
    private void SendUserToMain() {

        Intent mainIntent= new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode== GalleryPick) && (resultCode== RESULT_OK) && data!=null){
            Uri ImageUri= data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                Uri resultUri= result.getUri();

                StorageReference filePath= userProfileImagesRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();

                                reference.child("Users").child(currentUserId).child("image")
                                        .setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(SettingsActivity.this, "Uploading Image..", Toast.LENGTH_SHORT).show();

                                                }
                                                else{
                                                    String message = task.getException().toString();
                                                    Toast.makeText(SettingsActivity.this, "Error: " + message,Toast.LENGTH_SHORT).show();


                                                }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });

                            }
                        });

                    }
                });
            }
        }

    }

    private void ReviewUserInfo() {

        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image")){
                     String gettingUserName= dataSnapshot.child("name").getValue().toString();
                     String gettingUserStatus= dataSnapshot.child("status").getValue().toString();
                     String gettingUserImage= dataSnapshot.child("image").getValue().toString();

                    userName.setText(gettingUserName);
                    userStatus.setText(gettingUserStatus);
                    Picasso.get().load(gettingUserImage).into(userProfileImage);

                }
                else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name") )){
                    String gettingUserName= dataSnapshot.child("name").getValue().toString();
                    String gettingUserStatus = dataSnapshot.child("status").getValue().toString();
                    userName.setText(gettingUserName);
                    userStatus.setText(gettingUserStatus);
                }
                else {
                    Toast.makeText(SettingsActivity.this, "Please Set Your Profile ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}