package com.mdappsatrms.chatzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailSettings extends AppCompatActivity {

    private EditText userName;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String currentUserId;
    private Button getStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_settings);




        auth= FirebaseAuth.getInstance();
        currentUserId= auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        userName= findViewById(R.id.set_user_name_det);
        getStarted= findViewById(R.id.save_button);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setUsername= userName.getText().toString();

                if (TextUtils.isEmpty(setUsername)){
                    Toast.makeText(UserDetailSettings.this, "Enter Valid Username", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String,String> profileMap=new HashMap<>();
                    profileMap.put("uid",currentUserId);
                    profileMap.put("name",setUsername);
                    profileMap.put("status", "Available Now");

                    reference.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(UserDetailSettings.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                SendUserToMain();

                            }
                            else{
                                String message= task.getException().toString();
                                Toast.makeText(UserDetailSettings.this, "Error:" + message, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });



                }

            }
        });


    }

    private void SendUserToMain() {

        Intent mainIntent= new Intent(UserDetailSettings.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}