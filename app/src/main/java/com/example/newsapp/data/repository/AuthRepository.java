// File: data/repository/AuthRepository.java
package com.example.newsapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private static AuthRepository instance;
    private final FirebaseAuth firebaseAuth;

    // LiveData để thông báo trạng thái người dùng cho toàn bộ ứng dụng
    private final MutableLiveData<FirebaseUser> userLiveData;

    private AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        userLiveData = new MutableLiveData<>();
        // Ngay khi tạo, kiểm tra xem có người dùng nào đã đăng nhập từ lần trước không
        userLiveData.postValue(firebaseAuth.getCurrentUser());
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    // --- Các phương thức công khai cho ViewModel sử dụng ---

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Task<AuthResult> là một đối tượng của Firebase, cho phép ViewModel xử lý kết quả
    public Task<AuthResult> register(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> userLiveData.postValue(authResult.getUser()));
    }

    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> userLiveData.postValue(authResult.getUser()));
    }

    public void logout() {
        firebaseAuth.signOut();
        userLiveData.postValue(null);
    }
}