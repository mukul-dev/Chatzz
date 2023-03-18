package com.mdappsatrms.chatzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessAdapter myTabsAccessAdapter;
    private DatabaseReference reference;
    private String currentUserId;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternetConnection();

        auth= FirebaseAuth.getInstance();

      //  currentUserId= auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference();

        myToolbar=  findViewById(R.id.main_page_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Chatzz");

        myViewPager= findViewById(R.id.main_tabs_pager);
        myTabsAccessAdapter= new TabsAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter((myTabsAccessAdapter));

        myTabLayout= findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= auth.getCurrentUser();
        if(currentUser==null){
            SendUserToLogin();
        }
        else {
                updateUserLastSeen("online");

            VerifyUsersExistence();
        }

    }

//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        FirebaseUser currentUser= auth.getCurrentUser();
//        if(currentUser==null){
//            SendUserToLogin();
//        }
//        else{
//            updateUserLastSeen("offline");
//            VerifyUsersExistence();
//        }
//
//    }


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser= auth.getCurrentUser();
        if(currentUser==null){
            SendUserToLogin();
        }

        else
        {
                updateUserLastSeen("offline");
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FirebaseUser currentUser= auth.getCurrentUser();

        if(currentUser==null){
            SendUserToLogin();
        }
        else
        {
            updateUserLastSeen("offline");
        }


    }


    private void VerifyUsersExistence() {
        String currentUserID= auth.getCurrentUser().getUid();
        reference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){

                }
                else{
                    SendUserToUserDetails();
                    Toast.makeText(MainActivity.this, "Please set the Username ", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void checkInternetConnection(){

        ConnectivityManager manager= (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork= manager.getActiveNetworkInfo();

        if(activeNetwork!=null){

            if(activeNetwork.getType()== ConnectivityManager.TYPE_WIFI){
                Toast.makeText(this, "Wifi is being Used", Toast.LENGTH_SHORT).show();
            }
            else if(activeNetwork.getType()== ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(this, "Mobile Data is being Used", Toast.LENGTH_SHORT).show();
            }
            else{
                    Toast.makeText(this, "No Data connection!", Toast.LENGTH_SHORT).show();

            }

        }

    }



    private void SendUserToLogin() {
        Intent intent= new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.logout_option)
        {

            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
            auth.signOut();

            SendUserToLogin();
        }
        if(item.getItemId()== R.id.find_friends_option)
        {
            SendUserToFindFriends();

        }

        if(item.getItemId()== R.id.friend_request_option)
        {
            SendUserToFriendRequests();

        }

        if(item.getItemId()== R.id.settings_option)
        {
            SendUserToSettings();
        }
        if(item.getItemId()== R.id.create_group_option)
        {
            RequestNewGroup();
        }
        return true;
    }

    private void SendUserToFriendRequests() {
        Intent requestIntent= new Intent(MainActivity.this, FriendRequestsActivity.class);
        startActivity(requestIntent);

    }


    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("eg: School Friends, Office Members..");
        builder.setView(groupNameField);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                Toast.makeText(MainActivity.this, "Creating Group", Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Enter Group Name", Toast.LENGTH_SHORT).show();

                } else {

                    getNewGroup(groupName);

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });
        builder.show();

    }

    private void getNewGroup(String groupName) {

        reference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Your Group has been Created!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void SendUserToSettings() {
        Intent settingsIntent= new Intent(MainActivity.this, SettingsActivity.class);
        //settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }


    private void SendUserToUserDetails() {
        Intent userDetailsIntent= new Intent(MainActivity.this, UserDetailSettings.class);
        userDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(userDetailsIntent);
        finish();
    }

    private void SendUserToFindFriends() {

        Intent findIntent= new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findIntent);

    }

    private void updateUserLastSeen(String state_On_Off_Line)
    {
        currentUserId= auth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> lastSeenMap = new HashMap<>();
        lastSeenMap.put("time", saveCurrentTime);
        lastSeenMap.put("date", saveCurrentDate);
        lastSeenMap.put("On_Off_Line", state_On_Off_Line);

        reference.child("Users").child(currentUserId).child("lastSeenInfo")
                .updateChildren(lastSeenMap);

    }

}