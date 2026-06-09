// File: ui/auth/RegisterActivity.java
package com.example.newsapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.newsapp.R;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // 2. Ánh xạ các Views
        initViews();

        // 3. Thiết lập các sự kiện click
        setupListeners();

        // 4. Lắng nghe trạng thái từ ViewModel
        observeAuthState();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar_register);
        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvGoToLogin = findViewById(R.id.tv_go_to_login);

        // Gán sự kiện cho các nút điều hướng
        ivBack.setOnClickListener(v -> finish());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            // Lấy dữ liệu từ các EditText
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Gọi hàm kiểm tra dữ liệu đầu vào
            if (validateInput(email, password, confirmPassword)) {
                // Nếu dữ liệu hợp lệ, gọi ViewModel để đăng ký
                authViewModel.register(email, password);
            }
        });
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        // Kiểm tra email có rỗng không
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required.");
            return false;
        }
        // Kiểm tra định dạng email có hợp lệ không
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email.");
            return false;
        }
        // Kiểm tra mật khẩu có rỗng không
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required.");
            return false;
        }
        // Kiểm tra độ dài mật khẩu (Firebase yêu cầu tối thiểu 6 ký tự)
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters.");
            return false;
        }
        // Kiểm tra mật khẩu xác nhận có khớp không
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match.");
            return false;
        }
        // Tất cả đều hợp lệ
        return true;
    }

    private void observeAuthState() {
        authViewModel.getAuthState().observe(this, authResource -> {
            if (authResource == null) return;

            switch (authResource.status) {
                case LOADING:
                    // Khi đang tải, hiển thị ProgressBar và vô hiệu hóa nút
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    // Khi thành công, ẩn ProgressBar
                    progressBar.setVisibility(View.GONE);
                    // Thông báo thành công
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    // Đóng màn hình Register và quay lại màn hình Login
                    finish();
                    break;
                case ERROR:
                    // Khi có lỗi, ẩn ProgressBar và kích hoạt lại nút
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    // Hiển thị thông báo lỗi từ Firebase
                    Toast.makeText(this, "Registration Failed: " + authResource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}