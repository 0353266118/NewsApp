// File: ui/profile/ProfileActivity.java
package com.example.newsapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.newsapp.R;

import com.example.newsapp.ui.auth.LoginActivity;
import com.example.newsapp.utils.AuthResource;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Ánh xạ các layout có thể click
        RelativeLayout layoutEditProfile = findViewById(R.id.layout_edit_profile);
        RelativeLayout layoutChangePassword = findViewById(R.id.layout_change_password);
        RelativeLayout layoutLogout = findViewById(R.id.layout_logout);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // Thiết lập sự kiện click
        layoutEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        layoutChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        layoutLogout.setOnClickListener(v -> {
            profileViewModel.logout();
        });

        // Quan sát trạng thái
        observeViewModel();
    }

    private void observeViewModel() {
        profileViewModel.getProfileActionState().observe(this, resource -> {
            if (resource.status == AuthResource.AuthStatus.SUCCESS && "Logged out".equals(resource.data)) {
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                // Quay về màn hình Home và xóa tất cả các activity khác trên stack
                Intent intent = new Intent(this, com.example.newsapp.ui.home.HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}