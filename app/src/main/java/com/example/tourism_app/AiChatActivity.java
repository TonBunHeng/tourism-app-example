package com.example.tourism_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tourism_app.adapter.ChatAdapter;
import com.example.tourism_app.databinding.ActivityAiChatBinding;
import com.example.tourism_app.model.ChatMessage;
import com.example.tourism_app.model.MockDataProvider;

public class AiChatActivity extends AppCompatActivity {

    private ActivityAiChatBinding binding;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarAiChat, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBarInsets.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        binding.toolbarAiChat.setNavigationOnClickListener(v -> finish());

        chatAdapter = new ChatAdapter(MockDataProvider.getInitialChatMessages());
        binding.rvChatMessages.setAdapter(chatAdapter);

        binding.chipPrompt1.setOnClickListener(v -> sendPrompt(getString(R.string.prompt_1)));
        binding.chipPrompt2.setOnClickListener(v -> sendPrompt(getString(R.string.prompt_2)));
        binding.chipPrompt3.setOnClickListener(v -> sendPrompt(getString(R.string.prompt_3)));

        binding.btnSendMessage.setOnClickListener(v -> {
            String text = binding.etChatInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendPrompt(text);
                binding.etChatInput.setText("");
            }
        });
    }

    private void sendPrompt(String prompt) {
        chatAdapter.addMessage(new ChatMessage(prompt, true, "Just now"));
        binding.rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String aiResponse = "Great question about '" + prompt + "'! In Siem Reap, Angkor Wat is best viewed at sunrise around 5:30 AM. Make sure to buy your Angkor Pass online or at the ticket counter beforehand!";
            chatAdapter.addMessage(new ChatMessage(aiResponse, false, "Just now"));
            if (binding != null) {
                binding.rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
        }, 1000);
    }
}