package com.mdappsatrms.chatzz;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private RecyclerView userChatList;
    private View chatsView;
    private String currentUserId,chatSenName;

    private DatabaseReference userReference,chatsReference;
    private FirebaseAuth auth;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatsView= inflater.inflate(R.layout.fragment_chats, container, false);
        userChatList=chatsView.findViewById(R.id.user_chat_recycler);
        userChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth= FirebaseAuth.getInstance();
        currentUserId= auth.getCurrentUser().getUid();
        chatsReference= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userReference= FirebaseDatabase.getInstance().getReference().child("Users");


        return chatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions= new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatsReference,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ChatListHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Contacts, ChatListHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatListHolder holder, int position, @NonNull Contacts model) {


                     final String ChatUserId = getRef(position).getKey();

                final String[] chatUserImage = {"user_img"};
                userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatSenName= dataSnapshot.child("name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                        userReference.child(ChatUserId).orderByChild("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {
//                                    MessageAdapter msg= new MessageAdapter();
//                                    String lastMessage= msg.lastMessage;
//                                    String lastMsgDate= msg.lastMsgDate;
//                                    String lastMsgTime= msg.lastMsgTime;
//
//                                    holder.dateView.setVisibility(View.VISIBLE);
//                                    holder.timeView.setVisibility(View.VISIBLE);
//
//                                    holder.chatUserStatus.setText(lastMessage);
//                                    holder.dateView.setText(lastMsgDate);
//                                    holder.timeView.setText(lastMsgTime);

                                    if (dataSnapshot.hasChild("image")) {

                                        chatUserImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(chatUserImage[0]).placeholder(R.drawable.user_img).into(holder.chatProfileImage);
                                    }
                                    final String chatUName = dataSnapshot.child("name").getValue().toString();
                                    // final String chatUserStatus = dataSnapshot.child("status").toString();
                                    holder.itemView.findViewById(R.id.layoutBtnContainer).setVisibility(View.GONE);
                                    holder.chatUserName.setText(chatUName);

                                    if(dataSnapshot.child("lastSeenInfo").hasChild("On_Off_Line")){

                                        String onOffLine = dataSnapshot.child("lastSeenInfo").child("On_Off_Line").getValue().toString();

                                        if(onOffLine.equals("online")){
// set online image here
                                            holder.onlineImage.setVisibility(View.VISIBLE);
                                        }
                                        else if(onOffLine.equals("offline")){

                                            holder.onlineImage.setVisibility(View.INVISIBLE);

                                        }

                                    }

                                    else{
                                        holder.chatUserStatus.setText("");
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkSecurity();
                                            Intent chatsIntent= new Intent(getContext(),ChatActivity.class);
                                            chatsIntent.putExtra("chatUId",ChatUserId);
                                            chatsIntent.putExtra("chatUName",chatUName);
                                            chatsIntent.putExtra("chatSName", chatSenName);
                                            chatsIntent.putExtra("userUImage", chatUserImage[0]);
                                            startActivity(chatsIntent);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @NonNull
            @Override
            public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display,parent,false);

                return new ChatListHolder(view) ;
            }
        };

        userChatList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void checkSecurity() {
    }

    public static class ChatListHolder extends RecyclerView.ViewHolder{

        TextView chatUserName, chatUserStatus;
        CircleImageView chatProfileImage;
        ImageView onlineImage;
        TextView dateView, timeView;                        //buttons as text views
        public ChatListHolder(@NonNull View itemView) {
            super(itemView);

            chatProfileImage= itemView.findViewById(R.id.user_profile_image);
            chatUserName= itemView.findViewById(R.id.user_name_view);
            chatUserStatus= itemView.findViewById(R.id.user_status_view);
            onlineImage=itemView.findViewById(R.id.online_now);
            dateView=(TextView) itemView.findViewById(R.id.req_accept_btn);
            timeView=(TextView) itemView.findViewById(R.id.req_reject_btn);


        }
    }

}