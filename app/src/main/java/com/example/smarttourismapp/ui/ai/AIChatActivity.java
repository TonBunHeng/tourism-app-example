package com.example.smarttourismapp.ui.ai;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarttourismapp.R;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.databinding.ActivityAiChatBinding;
import com.example.smarttourismapp.model.ApiResponse;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIChatActivity extends AppCompatActivity {

    private ActivityAiChatBinding binding;
    private ApiService apiService;
    private ChatMessageAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        setupWelcomeMessage();
        fetchSuggestions();

        binding.btnSendMessage.setOnClickListener(v -> {
            String text = binding.etChatMessage.getText() != null ?
                    binding.etChatMessage.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(text)) {
                sendMessage(text);
                binding.etChatMessage.setText("");
            }
        });
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatMessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(chatAdapter);
    }

    private void setupWelcomeMessage() {
        chatAdapter.addMessage(new ChatMessage(
                "Hello traveler! I am AngkorVers, your AI travel companion for Cambodia. " +
                        "Ask me anything about temple history, hidden gems, dress codes, or custom itineraries!",
                false
        ));
    }

    private void fetchSuggestions() {
        apiService.getAiSuggestions().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<String>>> call, @NonNull Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    populateSuggestionChips(response.body().getData());
                } else {
                    useDefaultSuggestions();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<String>>> call, @NonNull Throwable t) {
                useDefaultSuggestions();
            }
        });
    }

    private void useDefaultSuggestions() {
        List<String> defaults = new ArrayList<>();
        defaults.add("Plan 3-day Siem Reap trip");
        defaults.add("Angkor Wat dress code");
        defaults.add("Top Khmer street food to try");
        defaults.add("Best sunset spot in Angkor");
        populateSuggestionChips(defaults);
    }

    private void populateSuggestionChips(List<String> suggestions) {
        binding.chipGroupSuggestions.removeAllViews();
        for (String suggestion : suggestions) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_chip_filter, binding.chipGroupSuggestions, false);
            chip.setText(suggestion);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setOnClickListener(v -> sendMessage(suggestion));
            binding.chipGroupSuggestions.addView(chip);
        }
    }

    private void sendMessage(String userText) {
        chatAdapter.addMessage(new ChatMessage(userText, true));
        binding.rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        binding.layoutAiThinking.setVisibility(View.VISIBLE);

        Map<String, Object> body = new HashMap<>();
        body.put("message", userText);

        apiService.chatWithAi(body).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                binding.layoutAiThinking.setVisibility(View.GONE);

                String reply = "I am currently unable to generate a response. Please try asking again shortly!";
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Map<String, Object> data = response.body().getData();
                    if (data.containsKey("reply") && data.get("reply") != null) {
                        reply = String.valueOf(data.get("reply"));
                    } else if (data.containsKey("message") && data.get("message") != null) {
                        reply = String.valueOf(data.get("message"));
                    } else if (data.containsKey("response") && data.get("response") != null) {
                        reply = String.valueOf(data.get("response"));
                    }
                }

                chatAdapter.addMessage(new ChatMessage(reply, false));
                binding.rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Throwable t) {
                binding.layoutAiThinking.setVisibility(View.GONE);
                chatAdapter.addMessage(new ChatMessage(
                        "Angkor tips: For temple visits, remember to dress respectfully covering shoulders and knees. Stay hydrated and purchase your Angkor Pass in advance!",
                        false
                ));
                binding.rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }
}
