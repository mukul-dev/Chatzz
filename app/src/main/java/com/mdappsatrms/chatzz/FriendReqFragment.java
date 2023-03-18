package com.mdappsatrms.chatzz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendReqFragment extends Fragment {

    String currentUserId;
    private DatabaseReference chatReference, userReference;
    private FirebaseAuth auth;

    private View FriendRequestsView;
    private RecyclerView RequestList;

    public FriendReqFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FriendRequestsView= inflater.inflate(R.layout.fragment_friend_req, container, false);

        chatReference= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userReference= FirebaseDatabase.getInstance().getReference().child("Users");
        auth= FirebaseAuth.getInstance();
        currentUserId= auth.getCurrentUser().getUid();

        RequestList= FriendRequestsView.findViewById(R.id.request_list_recycler);
        RequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return  FriendRequestsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions= new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatReference.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FriendRequestHolder> requestRecyclerAdapter= new FirebaseRecyclerAdapter<Contacts, FriendRequestHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendRequestHolder holder, int position, @NonNull Contacts model) {

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
                                        if(dataSnapshot.hasChild("image")){
                                            final String reqUserImage= dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(reqUserImage).placeholder(R.drawable.user_img).into(holder.userProfileImage);
                                            final String reqUserName= dataSnapshot.child("name").getValue().toString();
                                            final String reqUserStatus= dataSnapshot.child("status").getValue().toString();

                                            holder.userName.setText(reqUserName);
                                            holder.userStatus.setText(reqUserStatus);

                                        }
                                        else{
                                            final String reqUserName= dataSnapshot.child("name").getValue().toString();
                                            final String reqUserStatus= dataSnapshot.child("status").getValue().toString();

                                            holder.userName.setText(reqUserName);
                                            holder.userStatus.setText(reqUserStatus);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }


                            else if(req_type.equals("sent")){

                                holder.itemView.findViewById(R.id.userDisplayLayout).setVisibility(View.GONE);

//                                Button reqSent=holder.itemView.findViewById(R.id.req_accept_btn);
//                                reqSent.setText("Request Sent");
//                                Button cancelSent=holder.itemView.findViewById(R.id.req_reject_btn);
//                                cancelSent.setText("Cancel Request");
//
//                                userReference.child(requestingUserId).addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if(dataSnapshot.hasChild("image")){
//                                            final String reqUserImage= dataSnapshot.child("image").getValue().toString();
//                                            Picasso.get().load(reqUserImage).placeholder(R.drawable.user_img).into(holder.userProfileImage);
//                                            final String reqUserName= dataSnapshot.child("name").getValue().toString();
//                                            final String reqUserStatus= dataSnapshot.child("status").getValue().toString();
//
//                                            holder.userName.setText(reqUserName);
//                                            holder.userStatus.setText(reqUserStatus);
//
//                                        }
//                                        else{
//                                            final String reqUserName= dataSnapshot.child("name").getValue().toString();
//                                            final String reqUserStatus= dataSnapshot.child("status").getValue().toString();
//
//                                            holder.userName.setText(reqUserName);
//                                            holder.userStatus.setText(reqUserStatus);
//
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//

                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.itemView.findViewById(R.id.req_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.req_reject_btn).setVisibility(View.VISIBLE);

            }

            @NonNull
            @Override
            public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display,parent,false);

                   FriendRequestHolder holder= new FriendRequestHolder(view);

                return holder;
            }
        };

        RequestList.setAdapter(requestRecyclerAdapter);
        requestRecyclerAdapter.startListening();


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