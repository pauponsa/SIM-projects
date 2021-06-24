package com.eventic.src.presentation.activities.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eventic.src.domain.Message;
import com.example.eventic.R;

import java.util.Date;
import java.util.List;

public class chatAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SYSTEM = 3;

    private Context mContext;
    private List<Message> mMessageList;

    public chatAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;

        int currentYear = 0;
        int currentMonth = 0;
        int currentDay = 0;

        for (int i = 0; i < mMessageList.size(); i++) {
            Message message = mMessageList.get(i);
            if (message.getType() != Message.Type.system) {
                if (message.getDate().getYear() != currentYear) {
                    currentYear = message.getDate().getYear();
                    mMessageList.add(i, new Message("" + currentYear, message.getDate(), Message.Type.system));
                    i++;
                }

                if (message.getDate().getMonth() != currentMonth || message.getDate().getDate() != currentDay) {
                    currentMonth = message.getDate().getMonth();
                    currentDay = message.getDate().getDate();
                    mMessageList.add(i, new Message(getMonthName(currentMonth) + " " + currentDay, message.getDate(), Message.Type.system));
                    i++;
                }
            }
        }
    }

    private String getMonthName(int month) {
        switch (month + 1) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return String.valueOf(month);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if(message.getType() == Message.Type.sent) return VIEW_TYPE_MESSAGE_SENT;
        else if (message.getType() == Message.Type.received) return VIEW_TYPE_MESSAGE_RECEIVED;
        else return VIEW_TYPE_MESSAGE_SYSTEM;
    }

    public Message getMessage(int position) {
        return mMessageList.get(position);
    }

    // Inflates the appropriate layout according to the ViewType.
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);
            return new sentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received, parent, false);
            return new receivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_SYSTEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_system, parent, false);
            return new systemMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((sentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((receivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_SYSTEM:
                ((systemMessageHolder) holder).bind(message);
                break;
        }
    }

    public void addMessage(Message message) {
        mMessageList.add(message);
        notifyItemInserted(mMessageList.size());
        notifyDataSetChanged();
    }

    private class sentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        sentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_chat_message_me);
            timeText = itemView.findViewById(R.id.text_chat_timestamp_me);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            Date messageDate = message.getDate();

            String hour = String.valueOf(message.getDate().getHours());
            if (message.getDate().getMinutes() < 10) hour = "0" + hour;
            String minute = String.valueOf(message.getDate().getMinutes());
            if (message.getDate().getMinutes() < 10) minute = "0" + minute;
            String time = hour + ":" + minute;
            timeText.setText(time);
            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getDate()));
        }
    }

    private class receivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        receivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_chat_message_other);
            timeText = itemView.findViewById(R.id.text_chat_timestamp_other);
            nameText = itemView.findViewById(R.id.system_message_text);
            //profileImage = itemView.findViewById(R.id.image_chat_profile_other);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            String hour = String.valueOf(message.getDate().getHours());
            if (message.getDate().getMinutes() < 10) hour = "0" + hour;
            String minute = String.valueOf(message.getDate().getMinutes());
            if (message.getDate().getMinutes() < 10) minute = "0" + minute;

            String time = hour + ":" + minute;
            timeText.setText(time);

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getDate()));

            //nameText.setText(message.getSenderID().getUsername());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class systemMessageHolder extends RecyclerView.ViewHolder {
        TextView systemText;
        ImageView profileImage;

        systemMessageHolder(View itemView) {
            super(itemView);
            systemText = itemView.findViewById(R.id.system_message_text);
        }

        void bind(Message message) {
            systemText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getDate()));

            //nameText.setText(message.getSender().getUsername());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }
}