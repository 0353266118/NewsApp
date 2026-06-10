// File: ui/auth/AuthViewModel.java
package com.example.newsapp.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.repository.AuthRepository;
import com.example.newsapp.utils.AuthResource;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    // LiveData để thông báo trạng thái (loading, success, error) về cho View
    private final MutableLiveData<AuthResource<FirebaseUser>> authState = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
    }

    public LiveData<AuthResource<FirebaseUser>> getAuthState() {
        return authState;
    }

    public void login(String email, String password) {
        authState.setValue(AuthResource.loading()); // Báo cho View biết đang loading
        authRepository.login(email, password)
                .addOnSuccessListener(authResult -> {
                    // Thành công, báo cho View biết
                    authState.setValue(AuthResource.success(authResult.getUser()));
                })
                .addOnFailureListener(e -> {
                    // Thất bại, báo lỗi cho View
                    authState.setValue(AuthResource.error(e.getMessage()));
                });
    }

    public void register(String email, String password) {
        authState.setValue(AuthResource.loading());
        authRepository.register(email, password)
                .addOnSuccessListener(authResult -> {
                    authState.setValue(AuthResource.success(authResult.getUser()));
                })
                .addOnFailureListener(e -> {
                    authState.setValue(AuthResource.error(e.getMessage()));
                });
    }
}

