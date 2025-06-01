package com.example.mcpro.ChatFunctionality;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mcpro.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent() ? TYPE_SENT : TYPE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof SentViewHolder) {
            //((SentViewHolder) holder).sentMessage.setText(message.getContent());
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.sentMessage.setText(message.getContent());
            try {
                String rawTimestamp = message.getTimestamp(); // e.g., "2025-06-01 14:35:22"
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(rawTimestamp); // Parse from string to Date

                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // Format to time
                String formattedTime = outputFormat.format(date); // e.g., "02:35 PM"

                viewHolder.messageTime.setText(formattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
                viewHolder.messageTime.setText(""); // Fallback in case of error
            }

        } else {
            ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
            viewHolder.receivedMessage.setText(message.getContent());

            try {
                String rawTimestamp = message.getTimestamp(); // e.g., "2025-06-01 14:35:22"
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(rawTimestamp); // Parse from string to Date

                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // Format to time
                String formattedTime = outputFormat.format(date); // e.g., "02:35 PM"

                viewHolder.messageTime.setText(formattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
                viewHolder.messageTime.setText(""); // Fallback in case of error
            }

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessage;
        TextView messageTime;

        SentViewHolder(View itemView) {
            super(itemView);
            sentMessage = itemView.findViewById(R.id.sentMessageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessage;
        TextView messageTime;
        //TextView messageUsername;

        ReceivedViewHolder(View itemView) {
            super(itemView);
            receivedMessage = itemView.findViewById(R.id.receivedMessageText);
            messageTime = itemView.findViewById(R.id.messageTime);
            //messageUsername = itemView.findViewById(R.id.messageUsername);
        }
    }
}
