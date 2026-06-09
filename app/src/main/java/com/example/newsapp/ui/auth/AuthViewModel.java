// File: ui/auth/AuthViewModel.java
package com.example.newsapp.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.repository.AuthRepository;
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

// Lớp tiện ích để quản lý trạng thái, rất hữu ích
class AuthResource<T> {
    public enum AuthStatus { SUCCESS, ERROR, LOADING }
    public final AuthStatus status;
    public final T data;
    public final String message;

    private AuthResource(AuthStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> AuthResource<T> success(T data) {
        return new AuthResource<>(AuthStatus.SUCCESS, data, null);
    }
    public static <T> AuthResource<T> error(String msg) {
        return new AuthResource<>(AuthStatus.ERROR, null, msg);
    }
    public static <T> AuthResource<T> loading() {
        return new AuthResource<>(AuthStatus.LOADING, null, null);
    }
}