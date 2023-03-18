package com.mdappsatrms.chatzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class FriendRequestsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private String currentUserId;
    private RecyclerView FriendRequestRecycler;
    private DatabaseReference chatReference,contactReference ,userReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        chatReference= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userReference= FirebaseDatabase.getInstance().getReference().child("Users");
        contactReference= FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth= FirebaseAuth.getInstance();
        currentUserId= auth.getCurrentUser().getUid();
        FriendRequestRecycler= findViewById(R.id.friend_request_recycler);
        FriendRequestRecycler.setLayoutManager(new LinearLayoutManager(this));

        toolbar= findViewById(R.id.toolbar_Friend_Request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friend Requests");


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions= new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatReference.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FriendRequestsActivity.FriendRequestHolder> recyclerAdapter= new FirebaseRecyclerAdapter<Contacts, FriendRequestsActivity.FriendRequestHolder>(firebaseRecyclerOptions) {

            @Override
            protected void onBindViewHolder(@NonNull final FriendRequestsActivity.FriendRequestHolder reqholder, int position, @NonNull Contacts model) {


                 final String requestingUserId= getRef(position).getKey();
                DatabaseReference getRefType= getRef(position).child("request_type").getRef();
                getRefType.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                             String req_type= dataSnapshot.getValue().toString();
                            if(req_type.equals("received")){
                                   userReference.child(requestingUserId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            holder.userName.setText(model.getName());
//                                            holder.userStatus.setText(model.getStatus());
//                                            Picasso.get().load(model.getImage()).placeholder(R.drawable.user_img).into(holder.userProfileImage);
                                            if(dataSnapshot.hasChild("image")){


                                                 String reqUserImage= dataSnapshot.child("image").getValue().toString();

                                                Picasso.get().load(reqUserImage).placeholder(R.drawable.user_img).into(reqholder.userProfileImage);

                                                reqholder.userProfileImage.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String visit_Uid= getRef(position).getKey();

                                                        Intent profileIntent= new Intent(FriendRequestsActivity.this, UsersProfileActivity.class);
                                                        profileIntent.putExtra("visitUid",visit_Uid);
                                                        startActivity(profileIntent);

                                                    }
                                                });

                                            }
                                             final String reqUserName= dataSnapshot.child("name").getValue().toString();
                                             final String reqUserStatus= dataSnapshot.child("status").getValue().toString();
                                            reqholder.itemView.findViewById(R.id.req_accept_btn).setVisibility(View.VISIBLE);
                                            reqholder.itemView.findViewById(R.id.req_reject_btn).setVisibility(View.VISIBLE);

                                            reqholder.itemView.findViewById(R.id.req_accept_btn).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    contactReference.child(currentUserId).child(requestingUserId).child("Friends").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                contactReference.child(requestingUserId).child(currentUserId).child("Friends").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            chatReference.child(currentUserId).child(requestingUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        chatReference.child(currentUserId).child(requestingUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(FriendRequestsActivity.this, "Added to Friends", Toast.LENGTH_SHORT).show();
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
                                            });

                                            reqholder.itemView.findViewById(R.id.req_reject_btn).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    chatReference.child(currentUserId).child(requestingUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){

                                                                chatReference.child(requestingUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful()){
                                                                            Toast.makeText(FriendRequestsActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }
                                                                });

                                                            }
                                                        }
                                                    });


                                                }
                                            });

                                            reqholder.userName.setText(reqUserName);
                                            reqholder.userStatus.setText(reqUserStatus);





                                            reqholder.userName.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String visit_Uid= getRef(position).getKey();

                                                    Intent profileIntent= new Intent(FriendRequestsActivity.this, UsersProfileActivity.class);
                                                    profileIntent.putExtra("visitUid",visit_Uid);
                                                    startActivity(profileIntent);

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            }

                            else if(req_type.equals("sent")){
                                reqholder.itemView.findViewById(R.id.userDisplayLayout).setVisibility(View.GONE);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//                 holder.itemView.findViewById(R.id.user_name_view|R.id.user_profile_image).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String visit_Uid= getRef(position).getKey();
//
//                        Intent profileIntent= new Intent(FriendRequestsActivity.this, UsersProfileActivity.class);
//                        profileIntent.putExtra("visitUid",visit_Uid);
//                        startActivity(profileIntent);
//
//                    }
//                });


            }

            @NonNull
            @Override
            public FriendRequestsActivity.FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display,parent,false);

                FriendRequestsActivity.FriendRequestHolder viewHolder= new FriendRequestsActivity.FriendRequestHolder(view);

                return viewHolder;
            }
        };


        FriendRequestRecycler.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();


    }


    public static class FriendRequestHolder extends  RecyclerView.ViewHolder{

        TextView userName, userStatus;
        CircleImageView userProfileImage;
        Button acceptBtn, rejectBtn;

        public FriendRequestHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_name_view);
            userStatus= itemView.findViewById(R.id.user_status_view);
            userProfileImage=itemView.findViewById(R.id.user_profile_image);
            acceptBtn= itemView.findViewById(R.id.req_accept_btn);
            rejectBtn= itemView.findViewById(R.id.req_reject_btn);

        }

    }




}