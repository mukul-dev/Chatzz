package com.mdappsatrms.chatzz;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class PhoneLoginActivity extends AppCompatActivity
{
    private EditText InputUserPhoneNumber, InputUserVerificationCode;
    private Button SendVerificationCodeButton, VerifyButton;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        mAuth = FirebaseAuth.getInstance();


        InputUserPhoneNumber = (EditText) findViewById(R.id.phone_no_input);
        InputUserVerificationCode = (EditText) findViewById(R.id.phone_verification_input);
        SendVerificationCodeButton = (Button) findViewById(R.id.send_verification_code);
        VerifyButton = (Button) findViewById(R.id.verify_button);
        loadingBar = new ProgressDialog(this);


        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phoneNumber = "+91"+ InputUserPhoneNumber.getText().toString();

                if (phoneNumber.length()!=13)
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter valid phone number first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, PhoneLoginActivity.this, callbacks);
                }
            }
        });



        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);


                String verificationCode = InputUserVerificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait, while we are verifying verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                String message= e.toString();
                Toast.makeText(PhoneLoginActivity.this, "ERROR : "+message , Toast.LENGTH_LONG).show();
                loadingBar.dismiss();

                InputUserPhoneNumber.setVisibility(View.VISIBLE);
                SendVerificationCodeButton.setVisibility(View.VISIBLE);

                InputUserVerificationCode.setVisibility(View.INVISIBLE);
                VerifyButton.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);

                InputUserVerificationCode.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);
            }
        };
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
























//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseException;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthProvider;
//
//import java.util.concurrent.TimeUnit;
//
//public class PhoneLoginActivity extends AppCompatActivity {
//
//    private Button sendVerificationButton, VerifyButton;
//    private EditText InputPhoneNumber, InputVerificationCode;
//    private String mVerificationId;
//    private PhoneAuthProvider.ForceResendingToken mResendToken;
//    private FirebaseAuth mAuth;
//
//    private ProgressDialog loadingBar;
//
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_phone_login);
//
//        sendVerificationButton= findViewById(R.id.send_verification_code);
//        VerifyButton= findViewById(R.id.verify_button);
//        InputPhoneNumber= findViewById(R.id.phone_no_input);
//        InputVerificationCode= findViewById(R.id.phone_verification_input);
//
//        loadingBar= new ProgressDialog(this);
//
//        mAuth=FirebaseAuth.getInstance();
//
//        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String phoneNumber= InputPhoneNumber.getText().toString();
//
//                if(TextUtils.isEmpty(phoneNumber)){
//                    Toast.makeText(PhoneLoginActivity.this, "Enter Valid Phone No.", Toast.LENGTH_SHORT).show();
//                }
//                else{
//
//                    loadingBar.setTitle("Phone Verification");
//                    loadingBar.setMessage("Please Wait.. Authenticating..");
//                        loadingBar.show();
//                        loadingBar.setCanceledOnTouchOutside(false);
//                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,PhoneLoginActivity.this,mCallbacks);
//
//                }
//
//            }
//        });
//
//        VerifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendVerificationButton.setVisibility(View.INVISIBLE);
//                InputPhoneNumber.setVisibility(View.INVISIBLE);
//                String verificationCode=InputVerificationCode.getText().toString();
//                if(TextUtils.isEmpty(verificationCode)){
//                    Toast.makeText(PhoneLoginActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
//                }
//                else {
//
//
//                    loadingBar.setTitle("Code Verification");
//                    loadingBar.setMessage("Please Wait.. while verifying code..");
//                    loadingBar.show();
//                    loadingBar.setCanceledOnTouchOutside(false);
//
//                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
//
//                    signInWithPhoneAuthCredential(credential);
//                }
//            }
//        });
//
//
//
//        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                signInWithPhoneAuthCredential(phoneAuthCredential);
//
//
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//
//                loadingBar.dismiss();
//                Toast.makeText(PhoneLoginActivity.this, "Please Enter Correct Phone no. with Country code", Toast.LENGTH_SHORT).show();
//
//                sendVerificationButton.setVisibility(View.VISIBLE);
//                InputPhoneNumber.setVisibility(View.VISIBLE);
//                VerifyButton.setVisibility(View.INVISIBLE);
//                InputVerificationCode.setVisibility(View.INVISIBLE);
//
//            }
//
//
//            public void onCodeSent(@NonNull String verificationId,
//                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                    loadingBar.dismiss();
//                mVerificationId = verificationId;
//                mResendToken = token;
//
//                Toast.makeText(PhoneLoginActivity.this, "Code Sent to given Phone No.", Toast.LENGTH_SHORT).show();
//
//                sendVerificationButton.setVisibility(View.INVISIBLE);
//                InputPhoneNumber.setVisibility(View.INVISIBLE);
//                VerifyButton.setVisibility(View.VISIBLE);
//                InputVerificationCode.setVisibility(View.VISIBLE);
//
//            }
//
//        };
//
//    }
//
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            loadingBar.dismiss();
//                            Toast.makeText(PhoneLoginActivity.this, "LoggedIn Succesfully", Toast.LENGTH_SHORT).show();
//
//                            SendToMainActivity();
//
//                        } else {
//                            String message= task.getException().toString();
//                            Toast.makeText(PhoneLoginActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//
//    private  void SendToMainActivity(){
//        Intent mainIntent= new Intent(PhoneLoginActivity.this,MainActivity.class);
//        startActivity(mainIntent);
//        finish();
//    }
//
//}