package com.mdappsatrms.chatzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView findFrndsRecycler;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userReference= FirebaseDatabase.getInstance().getReference().child("Users");

        findFrndsRecycler= findViewById(R.id.findfriends_recycler);
        findFrndsRecycler.setLayoutManager(new LinearLayoutManager(this));

        toolbar= findViewById(R.id.toolbar_findFriends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions= new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userReference,Contacts.class)
        .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendsHolder> recyclerAdapter= new FirebaseRecyclerAdapter<Contacts, FindFriendsHolder>(firebaseRecyclerOptions) {

            @Override
            protected void onBindViewHolder(@NonNull FindFriendsHolder holder, final int position, @NonNull Contacts model) {

                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.user_img).into(holder.userProfileImage);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_Uid= getRef(position).getKey();

                        Intent profileIntent= new Intent(FindFriendsActivity.this, UsersProfileActivity.class);
                        profileIntent.putExtra("visitUid",visit_Uid);
                        startActivity(profileIntent);

                    }
                });


            }

            @NonNull
            @Override
            public FindFriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display,parent,false);

                FindFriendsHolder viewHolder= new FindFriendsHolder(view);

                return viewHolder;
            }
        };


        findFrndsRecycler.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();


    }


    public static class FindFriendsHolder extends  RecyclerView.ViewHolder{

        TextView userName, userStatus;
        CircleImageView userProfileImage;

        public FindFriendsHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_name_view);
            userStatus= itemView.findViewById(R.id.user_status_view);
            userProfileImage=itemView.findViewById(R.id.user_profile_image);

        }
    }


}