package com.mdappsatrms.chatzz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import static com.mdappsatrms.chatzz.R.color.colorPrimary;

public class GroupChatActivity extends AppCompatActivity {
    private String selGroupName, userId, userName;
    private Toolbar toolbar;
    private  EditText userMsgInput;
    private ScrollView scrollView;
    private TextView showTxtMsg;
    private ImageButton sendGroupMessage;
    private FirebaseAuth auth;
    private  DatabaseReference reference, groupReference, msgIdRef;
    private String presentDate,presentTime;
 //   private long msgIdForBackups=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        selGroupName= getIntent().getExtras().get("group_name").toString();

        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        groupReference= FirebaseDatabase.getInstance().getReference().child("Groups").child(selGroupName);


        auth= FirebaseAuth.getInstance();
        userId= auth.getCurrentUser().getUid();



        toolbar= findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(selGroupName);

        sendGroupMessage= findViewById(R.id.send_group_msg);
        showTxtMsg= findViewById(R.id.group_chat_text);
        scrollView=findViewById(R.id.my_scroll_view);
        userMsgInput= findViewById(R.id.input_group_message);
        groupUserInfo();

        sendGroupMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userMsgInput.getText())){

                }
                else {
                    GroupMessageSending1();
                 //   msgIdForBackups++;
                    userMsgInput.setText("");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        groupReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    DisplayMessages(dataSnapshot);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){

                    DisplayMessages(dataSnapshot);

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


//To send message
    private void GroupMessageSending1() {

        String userMsgId = groupReference.push().getKey(), userMessage= userMsgInput.getText().toString();


        if(TextUtils.isEmpty(userMessage)){

        }
        else{
            Calendar GetDate= Calendar.getInstance();
            SimpleDateFormat dateFormat= new SimpleDateFormat("dd MMM, yyyy");
            presentDate= dateFormat.format(GetDate.getTime());

            Calendar GetTime= Calendar.getInstance();
            SimpleDateFormat timeFormat= new SimpleDateFormat("hh:mm a");
            presentTime= timeFormat.format(GetTime.getTime());

            HashMap<String, Object> groupMsgInfo = new HashMap<>();
            groupReference.updateChildren(groupMsgInfo);

            msgIdRef= groupReference.child(userMsgId);

            HashMap<String, Object> MsgIdInfo = new HashMap<>();
            groupReference.updateChildren(MsgIdInfo);

            msgIdRef= groupReference.child(userMsgId);

            HashMap<String, Object> MsgInfoMap = new HashMap<>();
            MsgInfoMap.put("name",userName);
            MsgInfoMap.put("message",userMessage);
            MsgInfoMap.put("time",presentTime);
            MsgInfoMap.put("date",presentDate);

            msgIdRef.updateChildren(MsgInfoMap);


        }

    }


    // To get username of user in a group
    private void groupUserInfo() {
        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userName= dataSnapshot.child("name").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator= dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String msgDate= (String) ((DataSnapshot)iterator.next()).getValue();
            String msgMessage= (String) ((DataSnapshot)iterator.next()).getValue();
            String msgName= (String) ((DataSnapshot)iterator.next()).getValue();

            String msgTime= (String) ((DataSnapshot)iterator.next()).getValue();
           // String msgUserId= (String)((DataSnapshot)iterator.next()).getValue();

            showTxtMsg.append(msgName+ " " + msgTime + "\n" + msgMessage+"\n"+ msgDate+ "\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }



    }



}