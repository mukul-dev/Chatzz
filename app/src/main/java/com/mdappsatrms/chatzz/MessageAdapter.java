package com.mdappsatrms.chatzz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.String;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private List<UserMessages> userMessagesList;
    private FirebaseAuth auth;
    private DatabaseReference usersReference;

    String lastMessage, lastMsgDate,lastMsgTime;


    public   MessageAdapter(List<UserMessages> userMessagesList){
        this.userMessagesList= userMessagesList;

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        auth= FirebaseAuth.getInstance();

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,parent,false);

        return new MessageHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull  MessageHolder holder, int position) {

        String sendersId= auth.getCurrentUser().getUid();
        UserMessages messages= userMessagesList.get(position);
        String fromTo=  messages.getFromto();
        String fromUserId=stringSplitter(fromTo,1);
        String toUserId  =stringSplitter(fromTo,2);

//        String[] arrOfStr =  messages.getFromto().split("$", 0);
//        String fromUserId= arrOfStr[0];
//        String toUserId= arrOfStr[1];


        String messageidtype= messages.getMessageidtype();
        String messageId= stringSplitter(messageidtype,1);
        String messageType=   stringSplitter(messageidtype,2);

        holder.receiverChatText.setVisibility(View.GONE);
        holder.senderChatText.setVisibility(View.GONE);
        holder.receiverImageMsg.setVisibility(View.GONE);
        holder.senderImageMsg.setVisibility(View.GONE);
        holder.msgTimeViewRec.setVisibility(View.GONE);
        holder.imgMsgTimeView.setVisibility(View.GONE);
        holder.imgMsgSeenView.setVisibility(View.GONE);
        holder.msgTimeView.setVisibility(View.GONE);
        holder.msgSeenView.setVisibility(View.GONE);
        holder.imgMsgTimeViewRec.setVisibility(View.GONE);

        final UserMessages messages2= messages;
        final MessageHolder holder2= holder;
        if(messageType.equals("text")){

            if(fromUserId.equals(sendersId)){
                holder2.senderChatText.setVisibility(View.VISIBLE);
                holder2.senderChatText.setBackgroundResource(R.drawable.sender_message_layout);


                holder2.senderChatText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String messageText= messages2.getMessage();
                        holder2.msgTimeView.setVisibility(View.VISIBLE);
                        holder2.msgSeenView.setVisibility(View.VISIBLE);
                        String dateTime=  messages2.getDatetime();
                        String date= stringSplitter(dateTime,1);
                        String time= stringSplitter(dateTime,2);
                        holder2.senderChatText.setText(messageText);
                        holder2.msgTimeView.setText(date+", "+time);
                        holder2.msgSeenView.setImageResource(R.drawable.message_sent);
                        updateLastMessage(messageText,date,time, 1);
                    }
                });
            }
            else{
                
                holder2.receiverChatText.setVisibility(View.VISIBLE);
                holder2.receiverChatText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder2.receiverChatText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String messageText= messages2.getMessage();
                        holder2.msgTimeViewRec.setVisibility(View.VISIBLE);
                        String fromTo=  messages2.getFromto();
                        String fromUserId=stringSplitter(fromTo,1);
                        String toUserId  =stringSplitter(fromTo,2);
                        //        String[] arrOfStr =  messages2.getFromto().split("$", 0);
                        //        String fromUserId= arrOfStr[0];
                        //        String toUserId= arrOfStr[1];


                        String messageidtype= messages2.getMessageidtype();
                        String messageId= stringSplitter(messageidtype,1);
                        String messageType=   stringSplitter(messageidtype,2);

                        String dateTime=  messages2.getDatetime();
                        String date= stringSplitter(dateTime,1);
                        String time= stringSplitter(dateTime,2);

                        holder2.receiverChatText.setText(messageText);
                        holder2.msgTimeViewRec.setText(date+", "+time);
                    }
                });
            }

        }
        else if(messageType.equals("image")){

            if(fromUserId.equals(sendersId)){

                holder.senderImageMsg.setVisibility(View.VISIBLE);
                holder.imgMsgTimeView.setVisibility(View.VISIBLE);
                

                holder.senderImageMsg.setImageResource(R.drawable.download_file);
                final MessageHolder holder1=holder;
                final UserMessages messages1= messages;


                holder.senderImageMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(messages1.getMessage()).into(holder1.senderImageMsg);
                        String dateTime=  messages2.getDatetime();
                        String date= stringSplitter(dateTime,1);
                        String time= stringSplitter(dateTime,2);
                        holder1.imgMsgTimeView.setText(date+", "+time);
                        holder1.msgSeenView.setImageResource(R.drawable.message_sent);
                    }
                });
                
            }
            else{
                holder.receiverImageMsg.setVisibility(View.VISIBLE);
                holder.imgMsgTimeViewRec.setVisibility(View.VISIBLE);
                    holder.receiverImageMsg.setImageResource(R.drawable.download_file);
                    final MessageHolder holder1=holder;
                    final UserMessages messages1= messages;

                    holder.receiverImageMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Picasso.get().load(messages1.getMessage()).into(holder1.receiverImageMsg);
                            String dateTime=  messages1.getDatetime();
                            String date= stringSplitter(dateTime,1);
                            String time= stringSplitter(dateTime,2);

                            holder1.imgMsgTimeViewRec.setText(date+", "+time);

                        }
                    });


            }

        }

    }
    public void updateLastMessage(String messageText, String date, String time, int sender) {  //sender=1, receiver= any no

        if(sender==1)
            lastMessage= "You: "+ messageText;
        else
            lastMessage= messageText;

        lastMsgDate = date;
        lastMsgTime= time;

    }
    public  MessageAdapter(){

        String lastMsg= lastMessage;
        String lastMDate= lastMsgDate;
        String lastMTime= lastMsgTime;

    }




    private String stringSplitter(String fullString,int pos){

        String str1="", str2="";

        int i=0;
        char[] array= fullString.toCharArray();


        if(pos==1){
            for(i=0;i<fullString.length();i++){
            char ch= array[i];
            if(ch!='$')
                str1+=ch;
            else
                break;
        }
            return str1;
        }

        for(i=0;i<fullString.length();i++){
            char ch= array[i];
            if(ch=='$')
            {
                for(int j=i+1;j<fullString.length();j++){
                    ch=array[j];
                    str2+=ch;
                }
                break;
            }
        }

        return str2;

    }



    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }



    public class MessageHolder extends RecyclerView.ViewHolder{

        public TextView senderChatText, receiverChatText, msgTimeView ,imgMsgTimeView, msgTimeViewRec, imgMsgTimeViewRec;

        public ImageView senderImageMsg, receiverImageMsg, msgSeenView, imgMsgSeenView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            senderChatText = itemView.findViewById(R.id.sender_message);
            receiverChatText= itemView.findViewById(R.id.receiver_message);
            senderImageMsg=itemView.findViewById(R.id.sender_image_message);
            receiverImageMsg=itemView.findViewById(R.id.receiver_image_message);
            msgTimeView= itemView.findViewById(R.id.message_time_view);
            msgSeenView= itemView.findViewById(R.id.message_seen_view);
            msgTimeViewRec = itemView.findViewById(R.id.message_time_view_rec);
            imgMsgTimeView= itemView.findViewById(R.id.image_message_time_view);
            imgMsgSeenView= itemView.findViewById(R.id.image_message_seen_view);
            imgMsgTimeViewRec= itemView.findViewById(R.id.image_message_time_view_rec);
        }
    }

}
