package com.example.smarttourismapp.ui.ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourismapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_AI = 2;

    private final List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> list) {
        messages.clear();
        if (list != null) {
            messages.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? TYPE_USER : TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_ai, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvMessage.setText(message.getMessage());
        } else if (holder instanceof AiViewHolder) {
            ((AiViewHolder) holder).tvMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMessage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMessage;

        AiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
