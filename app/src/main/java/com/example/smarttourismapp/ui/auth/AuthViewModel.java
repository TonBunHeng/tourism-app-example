package com.example.smarttourismapp.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarttourismapp.data.remote.Resource;
import com.example.smarttourismapp.data.repository.AuthRepository;
import com.example.smarttourismapp.model.GoogleLoginRequest;
import com.example.smarttourismapp.model.RegisterRequest;
import com.example.smarttourismapp.model.User;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<User>> loginState = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> registerState = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> profileState = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> passwordChangeState = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<User>> getLoginState() {
        return loginState;
    }

    public LiveData<Resource<User>> getRegisterState() {
        return registerState;
    }

    public LiveData<Resource<User>> getProfileState() {
        return profileState;
    }

    public LiveData<Resource<Void>> getPasswordChangeState() {
        return passwordChangeState;
    }

    public void login(String email, String password, boolean rememberMe) {
        loginState.setValue(Resource.loading());
        authRepository.login(email, password, rememberMe, new AuthRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User result) {
                loginState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                loginState.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void register(RegisterRequest request) {
        registerState.setValue(Resource.loading());
        authRepository.register(request, new AuthRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User result) {
                registerState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                registerState.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void googleLogin(GoogleLoginRequest request) {
        loginState.setValue(Resource.loading());
        authRepository.googleLoginLiveData(request).observeForever(loginState::setValue);
    }

    public void updateProfile(String name, String phone, String location, String bio) {
        profileState.setValue(Resource.loading());
        authRepository.updateProfile(name, phone, location, bio, new AuthRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User result) {
                profileState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                profileState.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        passwordChangeState.setValue(Resource.loading());
        authRepository.changePassword(currentPassword, newPassword, confirmPassword, new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                passwordChangeState.setValue(Resource.success(null));
            }

            @Override
            public void onError(String errorMessage) {
                passwordChangeState.setValue(Resource.error(errorMessage));
            }
        });
    }
}
