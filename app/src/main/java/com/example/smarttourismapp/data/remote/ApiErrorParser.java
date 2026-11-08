package com.example.smarttourismapp.data.remote;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;

/**
 * Unified error parser for Laravel 13 REST API responses.
 * Extracts user-friendly error messages, handles HTTP status codes (401, 403, 404, 422, 429, 500),
 * maps Laravel validation errors to individual form fields, and sanitizes debug stack traces.
 */
public class ApiErrorParser {

    private static final Gson GSON = new Gson();

    public static class ParsedError {
        private final int statusCode;
        private final String message;
        private final Map<String, String> fieldErrors;

        public ParsedError(int statusCode, String message, Map<String, String> fieldErrors) {
            this.statusCode = statusCode;
            this.message = message;
            this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public String getFieldError(String fieldName) {
            return fieldErrors.get(fieldName);
        }
    }

    @NonNull
    public static ParsedError parse(@Nullable Response<?> response) {
        if (response == null) {
            return new ParsedError(0, "No response received from server.", new HashMap<>());
        }

        int code = response.code();
        String parsedMessage = null;
        Map<String, String> fieldErrors = new HashMap<>();

        if (response.errorBody() != null) {
            try {
                String errorJson = response.errorBody().string();
                if (!TextUtils.isEmpty(errorJson)) {
                    JsonObject root = GSON.fromJson(errorJson, JsonObject.class);
                    if (root != null) {
                        // 1. Check for Laravel validation errors object
                        if (root.has("errors") && root.get("errors").isJsonObject()) {
                            JsonObject errorsObj = root.getAsJsonObject("errors");
                            for (String key : errorsObj.keySet()) {
                                JsonElement val = errorsObj.get(key);
                                String fieldMsg = null;
                                if (val.isJsonArray()) {
                                    JsonArray arr = val.getAsJsonArray();
                                    if (arr.size() > 0) {
                                        fieldMsg = arr.get(0).getAsString();
                                    }
                                } else if (val.isJsonPrimitive()) {
                                    fieldMsg = val.getAsString();
                                }
                                if (fieldMsg != null) {
                                    fieldErrors.put(key, sanitize(fieldMsg, code));
                                    if (parsedMessage == null) {
                                        parsedMessage = fieldMsg;
                                    }
                                }
                            }
                        }

                        // 2. Check for root message
                        if (root.has("message") && !root.get("message").isJsonNull()) {
                            String rootMsg = root.get("message").getAsString();
                            // If root message is generic like "The given data was invalid.", prefer the first field error
                            if (!"The given data was invalid.".equalsIgnoreCase(rootMsg.trim()) || parsedMessage == null) {
                                parsedMessage = rootMsg;
                            }
                        }
                    }
                }
            } catch (IOException | RuntimeException ignored) {
            }
        }

        // Fallback default messages based on HTTP status code
        if (TextUtils.isEmpty(parsedMessage)) {
            parsedMessage = getDefaultMessageForStatusCode(code);
        } else {
            parsedMessage = sanitize(parsedMessage, code);
        }

        return new ParsedError(code, parsedMessage, fieldErrors);
    }

    @NonNull
    public static String parseMessage(@Nullable Response<?> response) {
        return parse(response).getMessage();
    }

    @NonNull
    public static Map<String, String> parseFieldErrors(@Nullable Response<?> response) {
        return parse(response).getFieldErrors();
    }

    @NonNull
    public static String parseThrowable(@Nullable Throwable t) {
        if (t == null) {
            return "An unexpected error occurred.";
        }
        if (t instanceof ConnectException) {
            return "Unable to connect to server. Please verify your internet connection or check backend status.";
        }
        if (t instanceof SocketTimeoutException) {
            return "Connection timed out. Server took too long to respond. Please try again.";
        }
        if (t instanceof UnknownHostException) {
            return "Server could not be reached. Please check your network connection.";
        }
        String msg = t.getMessage();
        return !TextUtils.isEmpty(msg) ? msg : "Network communication error.";
    }

    private static String getDefaultMessageForStatusCode(int code) {
        switch (code) {
            case 400:
                return "Invalid request. Please check your inputs.";
            case 401:
                return "Session expired or unauthorized. Please sign in again.";
            case 403:
                return "Access denied. You do not have permission to perform this action.";
            case 404:
                return "The requested destination, business, or resource was not found.";
            case 422:
                return "Please check the highlighted form fields and try again.";
            case 429:
                return "Too many requests. Please slow down and try again in a moment.";
            case 500:
            case 502:
            case 503:
            case 504:
                return "A server error occurred. Please try again later.";
            default:
                return "Unexpected server response (" + code + ").";
        }
    }

    private static String sanitize(String raw, int statusCode) {
        if (raw == null || raw.trim().isEmpty()) {
            return getDefaultMessageForStatusCode(statusCode);
        }

        String lower = raw.toLowerCase();
        if (lower.contains("sqlstate") ||
            lower.contains("queryexception") ||
            lower.contains("pdoexception") ||
            lower.contains("connection refused") ||
            lower.contains("vendor/laravel") ||
            lower.contains("stack trace:") ||
            lower.contains(".php:") ||
            lower.contains("integrity constraint violation")) {
            return "A database server error occurred. Please try again later.";
        }

        return raw;
    }
}
