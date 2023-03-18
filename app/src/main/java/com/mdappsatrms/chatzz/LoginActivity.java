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

public class LoginActivity extends AppCompatActivity {

    //private FirebaseUser currentUser;
    private Button loginButtn, phoneRegButton;
    private EditText userEmail,userPassword;
    private TextView forgetPass,createNewAcc;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth= FirebaseAuth.getInstance();
        //     currentUser= auth.getCurrentUser();

        InitializeFields();

        createNewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegister();

            }
        });

        loginButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        phoneRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPhoneVerification();
            }
        });
    }

    private void AllowUserToLogin() {

        String email= userEmail.getText().toString();
        String password= userPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)|| userPassword.length()<6){
            Toast.makeText(LoginActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait ..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToMain();
                        Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{
                        String message= task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void InitializeFields() {

        loginButtn=(Button) findViewById(R.id.login_button);
        phoneRegButton=(Button) findViewById(R.id.phone_no_login);
        userEmail= findViewById(R.id.login_email);
        userPassword= findViewById(R.id.login_password);
        createNewAcc= findViewById(R.id.create_an_account);
        forgetPass= findViewById(R.id.forget_password);
        loadingBar= new ProgressDialog(this);


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (currentUser != null) {
//            SendUserToMain();
//        }
//
//
//    }

    private void SendUserToMain() {

        Intent mainIntent= new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void SendUserToPhoneVerification(){
        Intent regintent= new Intent(LoginActivity.this,PhoneLoginActivity.class);
        startActivity(regintent);
        finish();
    }

    private void SendUserToRegister() {

        Intent regintent= new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(regintent);
        finish();
    }
}