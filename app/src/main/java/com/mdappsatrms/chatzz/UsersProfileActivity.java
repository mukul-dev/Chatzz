package com.mdappsatrms.chatzz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersProfileActivity extends AppCompatActivity {

    private String sendersUid,receiverUid, contactStatus;
    private CircleImageView visitProfileImg;
    private TextView visitName, visitStatus;
    private Button SendMessageRequest, RejectMessageRequest;
    private FirebaseAuth auth;
    private DatabaseReference userRef, requestRef, contactsRef;
    private ImageView fullImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        receiverUid= getIntent().getExtras().get("visitUid").toString();

        requestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth= FirebaseAuth.getInstance();
        fullImage= findViewById(R.id.full_image);

        sendersUid= auth.getCurrentUser().getUid();
        visitProfileImg= findViewById(R.id.visit_profile_image);
        visitName= findViewById(R.id.visit_userName);
        visitStatus= findViewById(R.id.visit_userStatus);
        SendMessageRequest= findViewById(R.id.Send_msg_request);
        RejectMessageRequest= findViewById(R.id.Reject_msg_request);
        contactStatus= "unknown";

        GetUserInfo();

    }

    private void GetUserInfo() {

        userRef.child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("image")) {
                        final String userImage = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(userImage).placeholder(R.drawable.user_img).into(visitProfileImg);
                         final Button close= findViewById(R.id.closeImage);
                        visitProfileImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fullImage.setVisibility(View.VISIBLE);
                                close.setVisibility(View.VISIBLE);
                                Picasso.get().load(userImage).placeholder(R.drawable.user_img).into(fullImage);
                            }
                        });

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fullImage.setVisibility(View.GONE);
                                close.setVisibility(View.GONE);
                            }
                        });

                    }

                    String userName= dataSnapshot.child("name").getValue().toString();
                        String userStatus= dataSnapshot.child("status").getValue().toString();

                        visitName.setText(userName);
                        visitStatus.setText(userStatus);

                        ManageChatRequests();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ManageChatRequests() {

        requestRef.child(sendersUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(receiverUid)){
                        String reqType= dataSnapshot.child(receiverUid).child("request_type").getValue().toString();

                        if(reqType.equals("sent")){
                            contactStatus="requested";
                            SendMessageRequest.setText("Cancel Request");
                        }
                        else if(reqType.equals("received")){// received
                            contactStatus= "request_received";
                            SendMessageRequest.setText("Accept Request");
                            RejectMessageRequest.setVisibility(View.VISIBLE);
                            RejectMessageRequest.setEnabled(true);

                            RejectMessageRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        CancelChatRequest();
                                }
                            });

                        }

                    }
                    else{

                        contactsRef.child(sendersUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild(receiverUid)){
                                    contactStatus= "friends";
                                    SendMessageRequest.setText("Remove Friend");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!sendersUid.equals(receiverUid)){

            SendMessageRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessageRequest.setEnabled(false);

                    if(contactStatus.equals("unknown")){
                        sendChatRequest();
                    }
                    else if(contactStatus.equals("requested")){
                        CancelChatRequest();
                    }
                    else if(contactStatus.equals("request_received")){
                        AcceptChatRequest();
                    }
                    else if(contactStatus.equals("friends")){
                        RemoveFriend();
                    }

                }
            });

        }
        else
        {
            SendMessageRequest.setVisibility(View.INVISIBLE);
        }

    }

    private void RemoveFriend() {

        contactsRef.child(sendersUid).child(receiverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    contactsRef.child(receiverUid).child(sendersUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                SendMessageRequest.setEnabled(true);
                                contactStatus="unknown";
                                SendMessageRequest.setText("Send Request ");
                                RejectMessageRequest.setVisibility(View.INVISIBLE);
                                RejectMessageRequest.setEnabled(false);

                            }

                        }
                    });

                }
            }
        });


    }

    private void AcceptChatRequest() {

        contactsRef.child(sendersUid).child(receiverUid).child("Friends").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    contactsRef.child(receiverUid).child(sendersUid).child("Friends").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                requestRef.child(sendersUid).child(receiverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            requestRef.child(sendersUid).child(receiverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        SendMessageRequest.setEnabled(true);
                                                        contactStatus="friends";
                                                        SendMessageRequest.setText("Remove Friend");
                                                        RejectMessageRequest.setVisibility(View.INVISIBLE);
                                                        RejectMessageRequest.setEnabled(false);

                                                    }

                                                }
                                            });

                                        }

                                    }
                                });

                            }

                        }
                    });

                }

            }
        });


    }

    private void CancelChatRequest() {

        requestRef.child(sendersUid).child(receiverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    requestRef.child(receiverUid).child(sendersUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    SendMessageRequest.setEnabled(true);
                                    contactStatus="unknown";
                                    SendMessageRequest.setText("Send Request ");
                                    RejectMessageRequest.setVisibility(View.INVISIBLE);
                                    RejectMessageRequest.setEnabled(false);

                                }

                        }
                    });

                }
            }
        });


    }

    private void sendChatRequest() {

        requestRef.child(sendersUid).child(receiverUid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    requestRef.child(receiverUid).child(sendersUid).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                SendMessageRequest.setEnabled(true);
                                contactStatus= "requested";
                                SendMessageRequest.setText("Cancel Request");
                            }

                        }
                    });

                }

            }
        });


    }
}