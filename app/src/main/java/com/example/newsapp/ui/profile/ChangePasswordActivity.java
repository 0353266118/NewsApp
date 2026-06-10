// File: ui/profile/ChangePasswordActivity.java
package com.example.newsapp.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.newsapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private Button btnChangeNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangeNow = findViewById(R.id.btn_change_now);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnChangeNow.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }
            profileViewModel.changePassword(newPassword);
        });
    }

    private void observeViewModel() {
        profileViewModel.getProfileActionState().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    btnChangeNow.setEnabled(false);
                    btnChangeNow.setText("Changing...");
                    break;
                case SUCCESS:
                    btnChangeNow.setEnabled(true);
                    btnChangeNow.setText("Change Now");
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    btnChangeNow.setEnabled(true);
                    btnChangeNow.setText("Change Now");
                    Toast.makeText(this, "Error: " + resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}