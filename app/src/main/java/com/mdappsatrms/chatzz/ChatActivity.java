package com.mdappsatrms.chatzz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

  //  public String msgstorepath=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))+"/Chatzz/Databases/backupmsg/";

    private String chatReceiverId, chatReceiverName, chatSenderId ,  chatSenderName , receiverProfileImage,saveCurrentTime, saveCurrentDate,dateString, checkType, fileURL;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private Toolbar toolbar;
    private RecyclerView userMessageRecycler;

    private ImageButton sendMsg,sendFilesMsg;
    private EditText messageInput;
    private TextView userNameDis, userOnlineDis;
    private Button loadPrevMessages;
    private CircleImageView userImageDis;

    private Uri fileUri;
    private StorageTask fileUpload;
    private ProgressBar bar;
    private Button backToMain;

    private final List<UserMessages> messagesList= new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chatReceiverId= getIntent().getExtras().get("chatUId").toString();
        chatReceiverName= getIntent().getExtras().get("chatUName").toString();
        receiverProfileImage=getIntent().getExtras().get("userUImage").toString();
        chatSenderName= getIntent().getExtras().get("chatSName").toString();
        auth= FirebaseAuth.getInstance();
        chatSenderId= auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference();

        toolbar= findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar= getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_bar_layout,null);
        actionBar.setCustomView(view);

        bar=findViewById(R.id.progressBarForImageSending);
        userNameDis= findViewById(R.id.chat_user_name);
        userOnlineDis= findViewById(R.id.lastSeen);
        userImageDis= findViewById(R.id.chat_user_image);
        backToMain= findViewById(R.id.back_to_main);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat dateForFolder= new SimpleDateFormat("ddMMyyyy");
        dateString= dateForFolder.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        messageAdapter= new MessageAdapter(messagesList);
        userMessageRecycler= findViewById(R.id.chat_message_list);
        linearLayoutManager= new LinearLayoutManager(this);
        userMessageRecycler.setLayoutManager(linearLayoutManager);
        userMessageRecycler.setAdapter(messageAdapter);
        sendMsg=(ImageButton) findViewById(R.id.send_message);
        sendFilesMsg= (ImageButton) findViewById(R.id.send_docs);
        messageInput= findViewById(R.id.input_message);

        userNameDis.setText(chatReceiverName);
        Picasso.get().load(receiverProfileImage).placeholder(R.drawable.user_img).into(userImageDis);

        //"/mof"+dateString+".cbm"


        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sendFilesMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence fileType[]=new CharSequence[]
                        {
                                "Images",
                                "Videos",
                                "Files"
                        };

                AlertDialog.Builder builder= new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Send");
                builder.setItems(fileType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which==0){
                                checkType= "image";
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent.createChooser(intent, "Select Image to send"), 1426);
                        }
                        else if(which==1){
                                checkType= "video";
                        }
                        else if(which==2){
                                checkType= "files";
                        }

                    }
                });
                builder.show();
            }
        });


        getLastSeenInfo();

    }


    @Override
    protected void onStart() {
        super.onStart();


        messageLoader();
        updateUserLastSeen("online");
        getLastSeenInfo();

//        loadPrevMessages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                i+=5;
//
//                messagesList.clear();
//                messageLoader(i);
//            }
//        });

    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//
//
//
//            if(stateOnOffLine.equals("offline"))
//            {
//                stateOnOffLine= "online";
//                updateUserLastSeen(stateOnOffLine);
//            }
//
//
//
//    }
//
//    @Override
//    protected void onStop()
//    {
//        super.onStop();
//        FirebaseUser currentUser= auth.getCurrentUser();
//        if (currentUser != null)
//        {
//            stateOnOffLine= "offline";
//            updateUserLastSeen(stateOnOffLine);
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        getLastSeenInfo();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        messagesList.clear();


    }

    private void messageLoader() {

        reference.child("Messages").child(chatSenderId).child(chatReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserMessages messages= dataSnapshot.getValue(UserMessages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                userMessageRecycler.smoothScrollToPosition(userMessageRecycler.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu2,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()== R.id.view_profile_option)
        {
            Intent i= new Intent(ChatActivity.this, UsersProfileActivity.class);
            i.putExtra("visitUid",chatReceiverId);
            startActivity(i);
        }

        if(item.getItemId()== R.id.clear_chat_option)
        {

        }


        if(item.getItemId()== R.id.set_chat_pass)
        {

        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == 1426) && (resultCode == RESULT_OK) && data != null && data.getData()!=null ){

            bar.setVisibility(View.VISIBLE);

            fileUri= data.getData();
            if(!checkType.equals("image")){
                Toast.makeText(this, "Select Image Please", Toast.LENGTH_SHORT).show();
            }
            else if(checkType.equals("image")){
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("images");

                final String senderRef= "Messages/" +chatSenderId+"/"+chatReceiverId;
                final String receiverRef= "Messages/" +chatReceiverId+"/"+chatSenderId;

                DatabaseReference messageKeyRef= reference.child("Messages").child(chatSenderId).child(chatReceiverId).push();
                final String messageId= messageKeyRef.getKey();

                final StorageReference filePath= storageReference.child(messageId+"."+"jpg");
                fileUpload= filePath.putFile(fileUri);

                fileUpload.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(task.isSuccessful()){

                        }
                        else{

//                            String error= task.getException().toString();
//                            Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadLink= task.getResult();
                        fileURL= downloadLink.toString();
                        Map imageLayout= new HashMap();
                        imageLayout.put("message", fileURL);
                        imageLayout.put("filename", fileUri.getLastPathSegment());
                        imageLayout.put("fromto", chatSenderId+"$"+chatReceiverId);
                        imageLayout.put("messageidtype", messageId+"$"+checkType);
                        imageLayout.put("datetime", saveCurrentDate+"$"+saveCurrentTime);
                       // imageLayout.put("state", "sent" );


                        Map textDetails= new HashMap();
                        textDetails.put(senderRef+"/"+ messageId,imageLayout);
                        textDetails.put(receiverRef+"/"+ messageId,imageLayout);

                        reference.updateChildren(textDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful()){

                                }
                                else{

                                }
                                bar.setVisibility(View.GONE);
                            }
                        });


                    }
                });

            }
            else{
                Toast.makeText(this, "Error. something went wrong", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void getLastSeenInfo(){

            reference.child("Users").child(chatReceiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("lastSeenInfo").hasChild("On_Off_Line")){
                        String date = dataSnapshot.child("lastSeenInfo").child("date").getValue().toString();
                        String time = dataSnapshot.child("lastSeenInfo").child("time").getValue().toString();
                        String onOffLine = dataSnapshot.child("lastSeenInfo").child("On_Off_Line").getValue().toString();


                        if(onOffLine.equals("online")){
// set online image here
                            userOnlineDis.setText("Online");
                        }
                        else if(onOffLine.equals("offline")){

                            userOnlineDis.setText("Last seen- "+time+" on "+date);

                        }


                    }
                    else{
                        userOnlineDis.setText("");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    private void sendMessage() throws IOException {
        String Message= messageInput.getText().toString();
        if(!(TextUtils.isEmpty(Message))){

            DatabaseReference messageKeyRef= reference.child("Messages").child(chatSenderId).child(chatReceiverId).push();
            String messageId= messageKeyRef.getKey();
            Map textLayout= new HashMap();
            textLayout.put("message", Message);
            textLayout.put("fromto", chatSenderId+"$"+chatReceiverId);
            textLayout.put("messageidtype", messageId+"$"+"text");
            textLayout.put("datetime", saveCurrentDate+"$"+saveCurrentTime);
            //textLayout.put("state","st"); // sent delivered seen

            String senderRef= "Messages/" +chatSenderId+"/"+chatReceiverId;
            String receiverRef= "Messages/" +chatReceiverId+"/"+chatSenderId;

            Map textDetails= new HashMap();
            textDetails.put(senderRef+"/"+ messageId,textLayout);
            textDetails.put(receiverRef+"/"+ messageId,textLayout);

            reference.updateChildren(textDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    
                    if(task.isSuccessful()){
                        //seenInfo.setText("Sent");
                        //seenInfo.setTextColor(Color.parseColor("#19FF00"));
                    }
                    else{
                        //seenInfo.setText("Not Sent");
                        //seenInfo.setTextColor(Color.parseColor("#FF0000"));
                    }
                    messageInput.setText("");
                }
            });

            //String filepath= Environment.getRootDirectory()+"CHATZZ/Databases/backups/"+chatReceiverId+"/"+"msdat"+dateString+".cbm";

//            File file= new File(Environment.getExternalStorageDirectory()+ "Chatzz/Databases/backups/" + dateString + ".cbm");
//            if(!file.exists()){
//                file.createNewFile();
//            }
//
//            BufferedWriter bufferedWriter;
//            try (FileWriter writer = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "Chatzz/Databases/backups/" + dateString + ".cbm", true)) {
//
//                bufferedWriter = new BufferedWriter(writer);
//            }

            String msgData= messageId+"#`$`#"+Message+"#`$`#"+saveCurrentTime+"#`msg`#\n";
            //bufferedWriter.write(msgData);

//            FileOutputStream fos= openFileOutput(chatReceiverId+dateString, MODE_PRIVATE);
//            fos.write(msgData.getBytes());
//            fos.close();
//            Toast.makeText(this, "File written", Toast.LENGTH_SHORT).show();
//
//            FileInputStream fis= openFileInput(chatReceiverId+dateString);
//            InputStreamReader inputStreamReader= new InputStreamReader(fis);
//            BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
//            StringBuffer stringBuffer= new StringBuffer();
//            String msgDataRead;
//
//            while ((msgDataRead= bufferedReader.readLine())!=null){
//                stringBuffer.append(msgDataRead);
//            }
//
//            messageInput.setText(stringBuffer.toString());


        }
    }


    private void updateUserLastSeen(String state_On_Off_Line)
    {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> lastSeenMap = new HashMap<>();
        lastSeenMap.put("time", saveCurrentTime);
        lastSeenMap.put("date", saveCurrentDate);
        lastSeenMap.put("On_Off_Line", state_On_Off_Line);

        reference.child("Users").child(chatSenderId).child("lastSeenInfo")
                .updateChildren(lastSeenMap);

    }


}