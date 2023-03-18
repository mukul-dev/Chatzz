package com.mdappsatrms.chatzz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccBtn;
    private EditText userEmail,userPassword;
   //private TextView phoneRegBtn;
    private TextView alreadyAccBtn;

    private FirebaseAuth auth;
    private DatabaseReference rootReference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth= FirebaseAuth.getInstance();
        rootReference= FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        alreadyAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUsertoLogin();
            }
        });


        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });


    }


    private void CreateNewAccount() {
        String email= userEmail.getText().toString();
        String password= userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
        }


        else if(TextUtils.isEmpty(password)|| userPassword.length()<6){
            Toast.makeText(this, "Enter Password greater than 6 characters!", Toast.LENGTH_SHORT).show();
        }


        else{

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we are creating your Account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    
                    if(task.isSuccessful()){

                        String currentUserId= auth.getCurrentUser().getUid();
                        rootReference.child("Users").child(currentUserId).setValue("");

                        SendUsertoMain();
                        Toast.makeText(RegisterActivity.this, "Account created Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{
                        String message= task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
                
        }

    }

    

    private void InitializeFields() {
        createAccBtn = findViewById(R.id.register_button);
        userEmail= findViewById(R.id.register_email);
        userPassword= findViewById(R.id.register_password);
        alreadyAccBtn= findViewById(R.id.already_an_account);
        //phoneRegBtn= findViewById(R.id.phone_no_register);
        loadingBar= new ProgressDialog(RegisterActivity.this);

    }

    private void SendUsertoLogin() {

        Intent loginIntent= new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void SendUsertoMain() {

        Intent mainIntent= new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}