package com.example.smarttourismapp.data.remote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Generic wrapper for UI state and network repository responses (MVVM pattern).
 *
 * @param <T> Payload data type
 */
public class Resource<T> {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    @NonNull
    private final Status status;

    @Nullable
    private final T data;

    @Nullable
    private final String message;

    @NonNull
    private final Map<String, String> fieldErrors;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Map<String, String> fieldErrors) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.fieldErrors = fieldErrors != null ? fieldErrors : Collections.emptyMap();
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> success(@Nullable T data, @Nullable String message) {
        return new Resource<>(Status.SUCCESS, data, message, null);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null, null);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    public static <T> Resource<T> error(@NonNull String message) {
        return new Resource<>(Status.ERROR, null, message, null);
    }

    public static <T> Resource<T> error(@NonNull String message, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, message, null);
    }

    public static <T> Resource<T> error(@NonNull String message, @Nullable Map<String, String> fieldErrors) {
        return new Resource<>(Status.ERROR, null, message, fieldErrors);
    }

    public static <T> Resource<T> error(@NonNull String message, @Nullable T data, @Nullable Map<String, String> fieldErrors) {
        return new Resource<>(Status.ERROR, data, message, fieldErrors);
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @NonNull
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    @Nullable
    public String getFieldError(@NonNull String field) {
        return fieldErrors.get(field);
    }
}
