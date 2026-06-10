// File: ui/profile/EditProfileActivity.java
package com.example.newsapp.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.newsapp.R;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;
    private EditText etUsername, etEmail;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnSaveChanges.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            if (TextUtils.isEmpty(newUsername)) {
                etUsername.setError("Username cannot be empty");
                return;
            }
            profileViewModel.updateProfile(newUsername);
        });
    }

    private void observeViewModel() {
        // Lấy thông tin người dùng hiện tại để hiển thị
        profileViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                etUsername.setText(firebaseUser.getDisplayName());
                etEmail.setText(firebaseUser.getEmail());
            }
        });

        // Lắng nghe kết quả của hành động cập nhật
        profileViewModel.getProfileActionState().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    btnSaveChanges.setEnabled(false);
                    btnSaveChanges.setText("Saving...");
                    break;
                case SUCCESS:
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Save Changes");
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình sau khi cập nhật thành công
                    break;
                case ERROR:
                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Save Changes");
                    Toast.makeText(this, "Error: " + resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}