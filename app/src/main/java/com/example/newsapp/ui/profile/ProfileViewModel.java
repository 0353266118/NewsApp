// File: ui/profile/ProfileViewModel.java
package com.example.newsapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.repository.AuthRepository;

import com.example.newsapp.utils.AuthResource;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository;

    // LiveData để lấy thông tin người dùng hiện tại
    private final LiveData<FirebaseUser> userLiveData;

    // LiveData để thông báo trạng thái của các hành động (update, logout...)
    private final MutableLiveData<AuthResource<String>> profileActionState = new MutableLiveData<>();

    public ProfileViewModel() {
        authRepository = AuthRepository.getInstance();
        userLiveData = authRepository.getUserLiveData();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<AuthResource<String>> getProfileActionState() {
        return profileActionState;
    }

    public void updateProfile(String newName) {
        profileActionState.setValue(AuthResource.loading());
        authRepository.updateUserProfile(newName)
                .addOnSuccessListener(aVoid -> profileActionState.setValue(AuthResource.success("Profile updated successfully")))
                .addOnFailureListener(e -> profileActionState.setValue(AuthResource.error(e.getMessage())));
    }

    public void changePassword(String newPassword) {
        profileActionState.setValue(AuthResource.loading());
        authRepository.updateUserPassword(newPassword)
                .addOnSuccessListener(aVoid -> profileActionState.setValue(AuthResource.success("Password changed successfully")))
                .addOnFailureListener(e -> profileActionState.setValue(AuthResource.error(e.getMessage())));
    }

    public void logout() {
        authRepository.logout();
        profileActionState.setValue(AuthResource.success("Logged out"));
    }
}