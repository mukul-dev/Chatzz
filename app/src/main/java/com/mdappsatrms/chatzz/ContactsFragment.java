package com.mdappsatrms.chatzz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class ContactsFragment extends Fragment {

    private View friendsView;
    private RecyclerView friendsList;
    private String currentUserId;

    private FirebaseAuth auth;
    private DatabaseReference contactsRef, userRef;


    public ContactsFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        friendsView= inflater.inflate(R.layout.fragment_contacts, container, false);

        friendsList= friendsView.findViewById(R.id.myFriendList);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth= FirebaseAuth.getInstance();
        currentUserId= auth.getCurrentUser().getUid();
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        return friendsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions firebaseRecyclerOptions=  new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactsRef, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,FriendsViewHolder> recyclerAdapter= new FirebaseRecyclerAdapter<Contacts, FriendsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Contacts model) {

                String friendId= getRef(position).getKey();

                userRef.child(friendId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("image")){
                            String profileImage= dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(profileImage).placeholder(R.drawable.user_img).into(holder.friendProfileImage);
                        }

                        String name= dataSnapshot.child("name").getValue().toString();
                        String status= dataSnapshot.child("status").getValue().toString();

                        holder.friendName.setText(name);
                        holder.friendStatus.setText(status);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display,parent,false);
                FriendsViewHolder viewHolder= new FriendsViewHolder(view);

                return viewHolder;
            }
        };

        friendsList.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();

    }

    public  static class FriendsViewHolder extends  RecyclerView.ViewHolder{

         TextView friendName, friendStatus;
         CircleImageView friendProfileImage;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            friendProfileImage= itemView.findViewById(R.id.user_profile_image);
            friendName= itemView.findViewById(R.id.user_name_view);
            friendStatus= itemView.findViewById(R.id.user_status_view);



        }
    }

}