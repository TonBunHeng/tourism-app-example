package com.example.tourism_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourism_app.databinding.ItemChatMessageBinding;
import com.example.tourism_app.model.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;

        ChatViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            if (message.isUserMessage()) {
                binding.layoutUserMessage.setVisibility(View.VISIBLE);
                binding.layoutAiMessage.setVisibility(View.GONE);
                binding.tvUserText.setText(message.getText());
                binding.tvUserTime.setText(message.getTimestamp());
            } else {
                binding.layoutAiMessage.setVisibility(View.VISIBLE);
                binding.layoutUserMessage.setVisibility(View.GONE);
                binding.tvAiText.setText(message.getText());
                binding.tvAiTime.setText(message.getTimestamp());
            }
        }
    }
}