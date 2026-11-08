package com.example.smarttourismapp.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiErrorParser;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.data.remote.Resource;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.AuthResponse;
import com.example.smarttourismapp.model.GoogleLoginRequest;
import com.example.smarttourismapp.model.LoginRequest;
import com.example.smarttourismapp.model.RegisterRequest;
import com.example.smarttourismapp.model.User;
import com.example.smarttourismapp.utils.AppPreference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository layer for Authentication & User Profile.
 * Supports traditional Callback patterns and LiveData Resource emissions.
 */
public class AuthRepository {

    private final ApiService apiService;
    private final AppPreference appPreference;

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public AuthRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
        this.appPreference = new AppPreference(context);
    }

    public LiveData<Resource<User>> loginLiveData(String email, String password, boolean rememberMe) {
        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse auth = response.body().getData();
                    if (auth != null) {
                        if (auth.getToken() != null) {
                            appPreference.saveAuthToken(auth.getToken());
                        }
                        if (auth.getUser() != null) {
                            appPreference.saveUser(auth.getUser());
                        }
                        appPreference.setRememberMe(rememberMe, email);
                        liveData.setValue(Resource.success(auth.getUser(), response.body().getMessage()));
                        return;
                    }
                }
                ApiErrorParser.ParsedError err = ApiErrorParser.parse(response);
                liveData.setValue(Resource.error(err.getMessage(), err.getFieldErrors()));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(ApiErrorParser.parseThrowable(t)));
            }
        });

        return liveData;
    }

    public void login(String email, String password, boolean rememberMe, AuthCallback<User> callback) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse auth = response.body().getData();
                    if (auth != null) {
                        if (auth.getToken() != null) {
                            appPreference.saveAuthToken(auth.getToken());
                        }
                        if (auth.getUser() != null) {
                            appPreference.saveUser(auth.getUser());
                        }
                        appPreference.setRememberMe(rememberMe, email);
                        callback.onSuccess(auth.getUser());
                        return;
                    }
                }
                ApiErrorParser.ParsedError err = ApiErrorParser.parse(response);
                callback.onError(err.getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public LiveData<Resource<User>> registerLiveData(RegisterRequest request) {
        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse auth = response.body().getData();
                    if (auth != null) {
                        if (auth.getToken() != null) {
                            appPreference.saveAuthToken(auth.getToken());
                        }
                        if (auth.getUser() != null) {
                            appPreference.saveUser(auth.getUser());
                        }
                        liveData.setValue(Resource.success(auth.getUser(), response.body().getMessage()));
                        return;
                    }
                }
                ApiErrorParser.ParsedError err = ApiErrorParser.parse(response);
                liveData.setValue(Resource.error(err.getMessage(), err.getFieldErrors()));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(ApiErrorParser.parseThrowable(t)));
            }
        });

        return liveData;
    }

    public void register(RegisterRequest request, AuthCallback<User> callback) {
        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse auth = response.body().getData();
                    if (auth != null) {
                        if (auth.getToken() != null) {
                            appPreference.saveAuthToken(auth.getToken());
                        }
                        if (auth.getUser() != null) {
                            appPreference.saveUser(auth.getUser());
                        }
                        callback.onSuccess(auth.getUser());
                        return;
                    }
                }
                ApiErrorParser.ParsedError err = ApiErrorParser.parse(response);
                callback.onError(err.getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public LiveData<Resource<User>> googleLoginLiveData(GoogleLoginRequest request) {
        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.googleLogin(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse auth = response.body().getData();
                    if (auth != null) {
                        if (auth.getToken() != null) {
                            appPreference.saveAuthToken(auth.getToken());
                        }
                        if (auth.getUser() != null) {
                            appPreference.saveUser(auth.getUser());
                        }
                        liveData.setValue(Resource.success(auth.getUser()));
                        return;
                    }
                }
                ApiErrorParser.ParsedError err = ApiErrorParser.parse(response);
                liveData.setValue(Resource.error(err.getMessage()));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                liveData.setValue(Resource.error(ApiErrorParser.parseThrowable(t)));
            }
        });

        return liveData;
    }

    public void getCurrentUser(AuthCallback<User> callback) {
        apiService.me().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        appPreference.saveUser(user);
                        callback.onSuccess(user);
                        return;
                    }
                }
                if (response.code() == 401) {
                    appPreference.clearSession();
                }
                callback.onError(ApiErrorParser.parseMessage(response));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void updateProfile(String name, String phone, String location, String bio, AuthCallback<User> callback) {
        Map<String, Object> updates = new HashMap<>();
        if (name != null) updates.put("name", name.trim());
        if (phone != null) updates.put("phone", phone.trim());
        if (location != null) updates.put("location", location.trim());
        if (bio != null) updates.put("bio", bio.trim());

        apiService.updateProfile(updates).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updated = response.body().getData();
                    if (updated != null) {
                        appPreference.saveUser(updated);
                        callback.onSuccess(updated);
                        return;
                    }
                }
                callback.onError(ApiErrorParser.parseMessage(response));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void changePassword(String currentPassword, String newPassword, String confirmPassword, AuthCallback<Void> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("current_password", currentPassword);
        body.put("password", newPassword);
        body.put("password_confirmation", confirmPassword);

        apiService.changePassword(body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorParser.parseMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void uploadAvatar(File file, AuthCallback<User> callback) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        apiService.uploadAvatar(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        appPreference.saveUser(user);
                        callback.onSuccess(user);
                        return;
                    }
                }
                callback.onError(ApiErrorParser.parseMessage(response));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                callback.onError(ApiErrorParser.parseThrowable(t));
            }
        });
    }

    public void logout(AuthCallback<Void> callback) {
        apiService.logout().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                appPreference.clearSession();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                appPreference.clearSession();
                callback.onSuccess(null);
            }
        });
    }
}
